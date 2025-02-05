package org.codehaus.groovy2.lang;

import groovy2.lang.FunctionType;
import groovy2.lang.MetaClass;
import groovy2.lang.mop.MOPConverterEvent;
import groovy2.lang.mop.MOPNewInstanceEvent;
import groovy2.lang.mop.MOPPropertyEvent;
import groovy2.lang.mop.MOPInvokeEvent;
import groovy2.lang.mop.MOPOperatorEvent;
import groovy2.lang.mop.MOPResult;

import java.dyn.CallSite;
import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodHandles.Lookup;
import java.dyn.MethodType;
import java.dyn.MutableCallSite;
import java.dyn.NoAccessException;
import java.util.HashMap;
import java.util.Set;

import org.codehaus.groovy2.dyn.Switcher;

public class MOPLinker {
  public enum MOPKind {
    MOP_GET_PROPERTY("getProperty"),
    MOP_SET_PROPERTY("setProperty"),
    MOP_INVOKE("invoke"),
    MOP_NEW_INSTANCE("newInstance"),
    MOP_OPERATOR("operator"),
    MOP_CONVERTER("convert");
    
    final String protocol;

    private MOPKind(String textKind) {
      this.protocol = textKind;
    }
  }
  
  private static final HashMap<String, MOPKind> indexMap;
  static {
    HashMap<String, MOPKind> map = new HashMap<String, MOPKind>();
    for(MOPKind kind: MOPKind.values()) {
      map.put(kind.protocol, kind);
    }
    indexMap = map;
  }
  
  public static String mangle(MOPKind metaProtocolKind, String name) {
    return metaProtocolKind.protocol+'$'+name;
  }
  
  public static CallSite bootstrap(Lookup lookup, String indyName, MethodType type) {
    try {

      int index = indyName.indexOf('$');
      if (index == -1) {
        throw new LinkageError("no meta protocol "+indyName);
      }

      String metaProtocol = indyName.substring(0, index);
      String name = indyName.substring(index + 1);

      MOPKind metaProtocolKind = indexMap.get(metaProtocol);
      if (metaProtocolKind == null) {
        throw new LinkageError("unknown meta protocol "+metaProtocol);
      }

      MOPCallSite callSite = new MOPCallSite(type, lookup.lookupClass(), metaProtocolKind, name);
      //Class<?> receiverType = type.parameterType(0);
      /*if (Modifier.isFinal(receiverType.getModifiers())) { // static receiver

      }*/ 

      MethodHandle target = MethodHandles.insertArguments(FALLBACK, 0, callSite);
      target = target.asCollector(Object[].class, type.parameterCount());
      target = MethodHandles.convertArguments(target, type);

      MethodHandle reset = MethodHandles.insertArguments(RESET, 0, callSite, target);
      reset = reset.asCollector(Object[].class, type.parameterCount());
      reset = MethodHandles.convertArguments(reset, type);
      callSite.reset = reset;
      
      callSite.lazy = true;
      
      callSite.setTarget(target);
      return callSite;

    } catch(RuntimeException e) {
      e.printStackTrace();
      throw e;
    } catch(Error e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  
  
  static class MOPCallSite extends MutableCallSite {
    final Class<?> declaringClass;
    final MOPKind mopKind;
    final String name;
    MethodHandle reset;
    volatile boolean lazy;
    
    public MOPCallSite(MethodType type, Class<?> declaringClass, MOPKind mopKind, String name) {
      super(type);
      this.declaringClass = declaringClass;
      this.mopKind = mopKind;
      this.name = name;
    }
    
    @Override
    public String toString() {
      return "CS: "+declaringClass.getName()+'.'+mopKind+'$'+name;
    }
  }
  
  public static Object fallback(MOPCallSite callSite, Object[] args) throws Throwable {
    //System.out.println("fallback "+callSite+" args " + java.util.Arrays.toString(args));
    
    try {
      MethodType type = callSite.getTarget().type();
      
      boolean isStatic = false;
      MetaClass metaClass;
      Class<?> receiverClass = type.parameterType(0);
      boolean isReceiverClassPrimitive = receiverClass.isPrimitive();
      if (!isReceiverClassPrimitive) {
        Object receiver = args[0];
        receiverClass = receiver.getClass();                // object -> class -> metaclass
        if (receiverClass == Class.class) {                 // is a static call ? 
          metaClass = RT.getMetaClass((Class<?>)receiver);  // class -> metaclass
          isStatic = true;
        } else {
          metaClass = RT.getMetaClass(receiver);
        }
      } else { // primitive type
        metaClass = RT.getMetaClass(receiverClass);
      }
      FunctionType dynamicType = dynamicType(metaClass, type, args);
      
      //System.out.println("MOP linker "+callSite.mopKind+"$"+callSite.name+" "+clazz+" "+metaClass+" "+isStatic);
      
      // special case: StaticClass.metaClass is hardcoded
      // because it will be never change, no need to hold the mutation lock
      if (isStatic && callSite.mopKind == MOPKind.MOP_GET_PROPERTY && "metaClass".equals(callSite.name)){
        MethodHandle target = MethodHandles.convertArguments(getMetaClass, type);
        callSite.setTarget(target);
        return target.invokeWithArguments(args);
      }
      
      MethodHandle oldTarget = callSite.getTarget(); 
      MOPResult result = upcallMOP(callSite.mopKind, metaClass, callSite.lazy, oldTarget, callSite.reset, callSite.declaringClass, isStatic, callSite.name, dynamicType);

      //System.out.println("mop resolved "+result.getTarget());
      
      MethodHandle mh = result.getTarget().asMethodHandle();
      MethodType mhType = mh.type();
      int mhTypeCount = mhType.parameterCount();
      int typeCount = type.parameterCount();
      if (mhTypeCount > typeCount) { 
        throw new LinkageError("target type has more parameters than callsite arguments"+
            mh+" "+type);
      }
      // we allow a closure with less parameters than callsite arguments
      if (mhTypeCount < typeCount) {
        mh = MethodHandles.dropArguments(mh, mhTypeCount,
            type.parameterList().subList(mhTypeCount, typeCount));
      }
      mh = MethodHandles.convertArguments(mh, type);
      
      // prepends switcher guards
      MethodHandle target = prependSwitcherGuards(mh, result.getConditions(), callSite.reset);

      if (!isReceiverClassPrimitive) {
        // the receiver class is not a primitive, so it's a polymorphic call
        // install a guard to check the receiver class
        
        MethodHandle test = (isStatic)? INSTANCE_CHECK: CLASS_CHECK;
        test = MethodHandles.insertArguments(test, 0, receiverClass);
        test = MethodHandles.convertArguments(test, MethodType.methodType(boolean.class, type.parameterType(0)));

        target = MethodHandles.guardWithTest(test, target, oldTarget);
      }
      
      callSite.setTarget(target);
      
      //System.out.println("mh "+mh);
      //System.out.println("mh.type() "+mh.type());
      return mh.invokeWithArguments(args);
      
    } catch(Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
  
  private static MethodHandle prependSwitcherGuards(MethodHandle target, Set<Switcher> conditions, MethodHandle reset) {
    for(Switcher switcher: conditions) {
      target = switcher.guardWithTest(target, reset);
    }
    return target;
  }

  public static Object reset(MOPCallSite callSite, MethodHandle fallback, Object[] args) throws Throwable {
    //System.out.println("reset");
    
    // we don't hold any lock here, so perhaps the target will be written
    // before being read by the fallback, in that case, it will create a method handle tree
    // containing an invalidated method handle.
    // But because fallback install the guard before the fallback tree,
    // the semantics will be ok, the MH tree will just contains dead code.
    callSite.setTarget(fallback);
    
    // start with a fresh callsite, new generated target can be lazy
    callSite.lazy = true;     // volatile write, force target to be written
    
    return fallback(callSite, args);
  }
  
  private static final MethodHandle getMetaClass;
  static {
    try {
      Lookup lookup = MethodHandles.publicLookup();
      getMetaClass = lookup.findStatic(RT.class, "getMetaClass",
          MethodType.methodType(MetaClass.class, Class.class));
    } catch (NoAccessException e) {
      throw new LinkageError(e.getMessage(), e);
    }
  }
  
  private static FunctionType dynamicType(MetaClass receiverMetaClass, MethodType methodType, Object[] args) {
    MetaClass[] metaTypes = new MetaClass[methodType.parameterCount()];
    metaTypes[0] = receiverMetaClass;
    for(int i=1;i <metaTypes.length; i++) {
      MetaClass metaType;
      Class<?> type = methodType.parameterType(i);
      if (!type.isPrimitive()) {  // null check not needed for primitive
        Object arg = args[i];
        metaType = (arg == null)? RT.getMetaClass(type): RT.getMetaClass(arg);
      } else {
        metaType = RT.getMetaClass(type);
      }
      metaTypes[i] = metaType;
    }
    return new FunctionType(RT.getMetaClass(methodType.returnType()), metaTypes);
  }

  static MOPResult upcallMOP(MOPKind kind, MetaClass metaClass, boolean lazyAllowed, MethodHandle fallback, MethodHandle reset, Class<?> declaringClass, boolean isStatic, String name, FunctionType functionType) {
    switch(kind) {
    case MOP_GET_PROPERTY:
      return metaClass.mopGetProperty(new MOPPropertyEvent(declaringClass, lazyAllowed, fallback, reset, isStatic, name, functionType));
    case MOP_SET_PROPERTY:
      return metaClass.mopSetProperty(new MOPPropertyEvent(declaringClass, lazyAllowed, fallback, reset, isStatic, name, functionType));
    case MOP_INVOKE:
      return metaClass.mopInvoke(new MOPInvokeEvent(declaringClass, lazyAllowed, fallback, reset, isStatic, name, functionType));
    case MOP_NEW_INSTANCE:
      return metaClass.mopNewInstance(new MOPNewInstanceEvent(declaringClass, lazyAllowed, fallback, reset, functionType));
    case MOP_OPERATOR:
      return metaClass.mopOperator(new MOPOperatorEvent(declaringClass, lazyAllowed, fallback, reset, name, functionType));
    case MOP_CONVERTER:
      return metaClass.mopConverter(new MOPConverterEvent(declaringClass, lazyAllowed, fallback, reset, functionType));
    }
    return null;
  }

  public static boolean instanceCheck(Class<?> clazz, Object o) {
    return clazz == o;
  }
  
  public static boolean classCheck(Class<?> clazz, Object o) {
    return clazz == o.getClass();
  }
  
  private static final MethodHandle INSTANCE_CHECK;
  private static final MethodHandle CLASS_CHECK;
  private static final MethodHandle FALLBACK;
  private static final MethodHandle RESET;
  static {
    try {
      Lookup lookup = MethodHandles.publicLookup();
      INSTANCE_CHECK = lookup.findStatic(MOPLinker.class, "instanceCheck",
          MethodType.methodType(boolean.class, Class.class, Object.class));
      CLASS_CHECK = lookup.findStatic(MOPLinker.class, "classCheck",
          MethodType.methodType(boolean.class, Class.class, Object.class));
      FALLBACK = lookup.findStatic(MOPLinker.class, "fallback",
          MethodType.methodType(Object.class, MOPCallSite.class, Object[].class));
      RESET = lookup.findStatic(MOPLinker.class, "reset",
          MethodType.methodType(Object.class, MOPCallSite.class, MethodHandle.class, Object[].class));
    } catch (NoAccessException e) {
      throw new LinkageError(e.getMessage(), e);
    }
  }
}
