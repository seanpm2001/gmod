package org.codehaus.groovy2.lang;

import groovy2.lang.Closure;
import groovy2.lang.Failures;
import groovy2.lang.FunctionType;
import groovy2.lang.MetaClass;
import groovy2.lang.Method;
import groovy2.lang.mop.MOPConverterEvent;
import groovy2.lang.mop.MOPResult;

import java.dyn.MethodHandle;
import java.dyn.MethodHandles;
import java.dyn.MethodType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy2.dyn.Switcher;
import org.codehaus.groovy2.lang.mop.ReflectClosure;

public class MethodResolver {
  static class MethodEntry {
    final FunctionType methodType;
    final Method target;
    private final Set<Switcher> switcherGuards;
    private final MethodHandle returnConverter;
    private final MethodHandle[] parameterConverters;
    
    MethodEntry(FunctionType methodType, Method target, Set<Switcher> switcherGuards, MethodHandle returnConverter, MethodHandle[] parameterConverters) {
      this.methodType = methodType;
      this.target = target;
      this.switcherGuards = switcherGuards;
      this.returnConverter = returnConverter;
      this.parameterConverters = parameterConverters;
    }
    
    @Override
    public String toString() {
      return target.toString();
    }
    
    MOPResult asResult() {
      if (parameterConverters == null && returnConverter == null) {
        return new MOPResult(target, switcherGuards);
      }
      
      MethodHandle mh = this.target.asMethodHandle();
      
      if (parameterConverters != null) {  
        //System.out.println("converters "+Arrays.toString(parameterConverters));
        
        // add identity where needed
        for(int i=0; i< parameterConverters.length; i++) { 
          if (parameterConverters[i] == null) {
            parameterConverters[i] = MethodHandles.identity(mh.type().parameterType(i));
          }
        }
        
        System.out.println("mh.type "+mh.type());
        System.out.println("parameterConverters.length "+Arrays.asList(parameterConverters));
        
        mh = MethodHandles.filterArguments(mh, 0, parameterConverters);
      }
      
      if (returnConverter != null) {
        mh = MethodHandles.filterReturnValue(mh, returnConverter);
      }
      return new MOPResult(new ReflectClosure(false, mh), switcherGuards);
    }
  }
  
  // this code use the same algorithm as resolve(), should be updated accordingly
  public static List<Method> getMostSpecificMethods(MetaClass metaClass, Map<FunctionType, Method> methodMap, FunctionType signature, boolean allowConversions) {
    Method method = methodMap.get(signature);  // short-cut
    if (method != null) {
      return Collections.singletonList(method);
    }
    
    ArrayList<MethodEntry> entries = applicable(metaClass, methodMap.values(), signature, allowConversions);
    int size = entries.size();
    if (size == 0) {
      return Collections.emptyList();
    }
    if (size == 1) {
      return Collections.singletonList(entries.get(0).target);
    }
    
    MethodEntry mostSpecific = mostSpecific(entries, new boolean[signature.getParameterCount()]);
    if (mostSpecific != null) {
      return Collections.singletonList(mostSpecific.target);
    }
    
    Method[] methods = new Method[size];
    for(int i=0; i<size; i++) {
      methods[i] = entries.get(i).target;
    }
    return Arrays.asList(methods);
  }
  
  // this code use the same algorithm as getMostSpecificMethods(), should be updated accordingly
  public static MOPResult resolve(MetaClass metaClass, Map<FunctionType, Method> methodMap, boolean isStatic, FunctionType signature, boolean allowConversions, MethodHandle reset) {
    Method method = methodMap.get(signature);  // short-cut
    if (method != null) {
      if (isStatic && !Modifier.isStatic(method.getModifiers())) {
        return asMOPResult(Failures.fail("most specific method "+method+" is not static "));
      }
      return asMOPResult(method);
    }
    
    //System.out.println("MOP method resolver: values :" + methodMap.values());
    
    ArrayList<MethodEntry> entries = applicable(metaClass, methodMap.values(), signature, allowConversions);
    
    //System.out.println("MOP method resolver: applicable "+entries+" isStatic "+isStatic+" "+signature);
    
    int size = entries.size();
    if (size == 0) {
      return asMOPResult(Failures.fail("no applicable method among "+entries));
    }
    MethodEntry mostSpecific = entries.get(0);
    if (size == 1) {
      if (isStatic && !Modifier.isStatic(mostSpecific.target.getModifiers())) {
        return asMOPResult(Failures.fail("most specific method "+mostSpecific+" is not static "));
      }
      return mostSpecific.asResult();
    }
    
    int count = mostSpecific.methodType.getParameterCount();
    boolean[] guardNeeded = new boolean[count];
    
    mostSpecific = mostSpecific(entries, guardNeeded);
    if (mostSpecific == null) {
      return asMOPResult(Failures.fail("no most specific method among "+entries+" when calling with "+signature));
    }
    if (isStatic && !Modifier.isStatic(mostSpecific.target.getModifiers())) {
      return asMOPResult(Failures.fail("most specific method "+mostSpecific+" is not static "));
    }
    
    return mostSpecific.asResult();
  }
  
  private static MOPResult asMOPResult(Closure target) {
    return new MOPResult(target, Collections.<Switcher>emptySet());
  }

  public static ArrayList<MethodEntry> applicable(MetaClass metaClass, Collection<Method> methods, FunctionType functionType, boolean allowConversions) {
    //System.out.println("method resolver: potentially applicable methods "+methods);
    
    ArrayList<MethodEntry> entries = new ArrayList<MethodEntry>();
    loop: for(Method method: methods) {
      int functionTypeCount = functionType.getParameterCount();
        
      FunctionType methodType = method.getMethodType();
      int methodTypeCount = methodType.getParameterCount();
      if (method.isVarargs()) {
        if (methodTypeCount > functionTypeCount) {
          continue;
        }
        methodType = spread(methodType, functionTypeCount);  
      } else {
        if (methodTypeCount != functionTypeCount) {
          continue;
        }
      }
      
      assert methodType.getParameterCount() == functionTypeCount;
      //System.out.println("method resolver: applicable "+method);
      
      boolean isParameterConverted = false;
      int staticShift = Modifier.isStatic(method.getModifiers())? 0: 1;
      HashSet<Switcher> switchers = new HashSet<Switcher>(2);
      MethodHandle[] parameterConverters = new MethodHandle[functionTypeCount + staticShift];
      for(int i=0; i<functionTypeCount; i++) {
        if (!isAssignable(methodType.getParameterType(i), functionType.getParameterType(i), 
            switchers, parameterConverters, i + staticShift, allowConversions)) {
          continue loop;  
        }
        isParameterConverted |= (parameterConverters[i + staticShift] != null);
      }
      
      MethodHandle[] returnConverters = new MethodHandle[1];
      /* FIXME: should we use the return type ? // also don't forget void case
      if (!isAssignable(functionType.getReturnType(), methodType.getReturnType(),
          returnConverters, 0,
          allowConversions)) {
        continue loop;
      }*/
      
      // check receiver class
      if (staticShift == 1) { // if is not static
        MetaClass declaringMetaClass = method.getDeclaringMetaClass();
        if (metaClass != declaringMetaClass &&
            needSubTypeConverter(declaringMetaClass, metaClass)) {
          MOPResult result = declaringMetaClass.mopConverter(new MOPConverterEvent(null, false, null, null,
              new FunctionType(metaClass, declaringMetaClass)));

          System.out.println("subtype converter found "+result.getTarget());

          Closure target = result.getTarget();  // this may be a failure, because the user change primitive conversions
          MethodHandle mh = target.asMethodHandle();
          if (Failures.isFailure(target)) {
            mh = MethodHandles.dropArguments(mh, 0, RT.getRawClass(metaClass));
            mh = MethodHandles.convertArguments(mh,
                MethodType.methodType(RT.getRawClass(declaringMetaClass), mh.type().parameterType(0)));
          }
          parameterConverters[0] = mh;
          switchers.addAll(result.getConditions());
        }
        isParameterConverted |= (parameterConverters[0] != null);
      }
      
      
      entries.add(new MethodEntry(methodType, method,
          switchers, returnConverters[0],
          (isParameterConverted)? parameterConverters: null));
    }
    return entries;
  }
  
  

  private static FunctionType spread(FunctionType methodType, int count) {
    throw new AssertionError("NYI: varargs spreading no implemented");
  }

  private static boolean isAssignable(MetaClass metaClass1, MetaClass metaClass2, HashSet<Switcher> switchers, MethodHandle[] converters, int index, boolean allowConversions) {
    //System.out.println("isAssignable "+metaClass1+" "+metaClass2);
    
    if (isSuperType(metaClass1, metaClass2)) {
      // check if we need converter to implement subtyping relation
      if (needSubTypeConverter(metaClass1, metaClass2)) {
        
        //System.out.println("need subtype conversion between "+metaClass1+" <- "+metaClass2);
        
        MOPResult result = metaClass1.mopConverter(new MOPConverterEvent(null, false, null, null,
            new FunctionType(metaClass2, metaClass1)));
        
        //System.out.println("converter found "+result.getTarget());
        
        Closure target = result.getTarget();  // this may be a failure, because the user change primitive conversions
        MethodHandle mh = target.asMethodHandle();
        if (Failures.isFailure(target)) {
          mh = MethodHandles.dropArguments(mh, 0, RT.getRawClass(metaClass2));
          mh = MethodHandles.convertArguments(mh,
              MethodType.methodType(RT.getRawClass(metaClass1), mh.type().parameterType(0)));
        }
        converters[index] = mh;
        switchers.addAll(result.getConditions());
      }
      return true;
    }
    
    if (!allowConversions)
      return false;
    
    
    return false; //FIXME
    /*
    Closure converter = metaClass1.mopConverter(metaClass2);
    if (Failures.isFailure(converter)) {
      return false;
    }
    converters[index] = converter.asMethodHandle();
    return true;
    */
  }
  
  static boolean needSubTypeConverter(MetaClass metaClass1, MetaClass metaClass2) {
    Class<?> clazz1 = RT.getRawClass(metaClass1);
    Class<?> clazz2 = RT.getRawClass(metaClass2);
    
    if (clazz1 == Object.class) {
      return false;
    }
    
    if (clazz1.isPrimitive() && (clazz2.isPrimitive() || Utils.getPrimitive(clazz2).isPrimitive())) {
        return false;
    }
    if (Utils.getPrimitive(clazz1).isPrimitive() && clazz2.isPrimitive()) {
      return false;
    }
    
    if (clazz1.isAssignableFrom(clazz2)) {
      return false;
    }
    return true;
  }

  public static MethodEntry mostSpecific(ArrayList<MethodEntry> entries, /*out*/ boolean[] guardNeeded) {
    int count = guardNeeded.length;
    MethodEntry mostSpecific = entries.get(0);
    
    int size = entries.size();
    for(int i=1; i<size; i++) {
      MethodEntry entry = entries.get(i);
      
      FunctionType mostSpecificType = mostSpecific.methodType;
      FunctionType entryType = entry.methodType;
      
      int bias = 0;  // 0: no idea, 1:mostSpecific subtype, -1: entry subtype 
      for(int p=0; p<count; p++) {
        MetaClass mostSpecificParameterType = mostSpecificType.getParameterType(p);
        MetaClass entryParameterType = entryType.getParameterType(p);
        if (mostSpecificParameterType != entryParameterType) {
          guardNeeded[p] = true;
          
          switch(bias) {
          case 1:
            if (!isSuperType(entryParameterType, mostSpecificParameterType)) {
              return null;
            }
            continue;
          case -1:
            if (!isSuperType(mostSpecificParameterType, entryParameterType)) {
              return null;
            }
            continue;
          case 0:
            if (isSuperType(entryParameterType, mostSpecificParameterType)) {
              bias = 1;
              continue;
            }
            if (isSuperType(mostSpecificParameterType, entryParameterType)) {
              bias = -1;
              continue;
            }
            return null;
          default:
            throw new AssertionError();
          }
        }
      }
      
      if (bias == 0) { // covariant return type, take the most specific
        mostSpecific = mostSpecificCovariantReturnType(mostSpecific, entry, mostSpecificType, entryType);
        continue;
      }
      if (bias == -1) {
        mostSpecific = entry;
        continue;
      }
    }
    
    return mostSpecific;
  }

  private static MethodEntry mostSpecificCovariantReturnType(MethodEntry mostSpecific, MethodEntry entry, FunctionType mostSpecificType, FunctionType entryType) {
    MetaClass entryReturnType = entryType.getReturnType();
    MetaClass mostSpecificReturnType = mostSpecificType.getReturnType();
    
    if (isSuperType(entryReturnType, mostSpecificReturnType)) {
      return mostSpecific;
    }
    if (isSuperType(mostSpecificReturnType, entryReturnType)) {
      return entry;
    }
    throw new AssertionError();
  }

  private static boolean isSuperType(MetaClass metaClass1, MetaClass metaClass2) {
    if (metaClass1 == metaClass2) {
      return true;
    }
    
    //System.out.println("isSuperType: supertypes("+metaClass2+") "+metaClass2.getSuperTypes());
    
    //FIXME: don't works with parameterized types
    for(MetaClass superType: metaClass2.getSuperTypes()) {
      if (isSuperType(metaClass1, superType)) {
        return true;
      }
    }
    return false;
  }
}
