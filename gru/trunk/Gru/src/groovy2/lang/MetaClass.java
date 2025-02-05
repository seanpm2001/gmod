package groovy2.lang;

import groovy2.lang.mop.MOPConverterEvent;
import groovy2.lang.mop.MOPNewInstanceEvent;
import groovy2.lang.mop.MOPPropertyEvent;
import groovy2.lang.mop.MOPInvokeEvent;
import groovy2.lang.mop.MOPOperatorEvent;
import groovy2.lang.mop.MOPResult;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public interface MetaClass {
  public Type getType();
  
  //public MetaClass getArrayType();
  //public MetaClass getComponentType();
  
  public MetaClass getRawMetaClass();
  public List<MetaClass> getTypeArguments();
  
  public List<MetaClass> getSuperTypes();
  public List<MetaClass> getMixins();
  
  public MetaClassMutator mutator();
  
  public Collection<Attribute> getAttributes();
  public Collection<Property> getProperties();
  public Collection<Method> getContructors();
  public Collection<Method> getMethods();
  public Collection<Method> getMethodsByName(String name);
  
  public Attribute findAttribute(String name);
  public Property findProperty(String name);
  public Collection<Method> findMethods(String name, MetaClass... compatibleTypes);
  public Collection<Method> findConstructors(MetaClass... compatibleTypes);
  
  public MOPResult mopGetProperty(MOPPropertyEvent mopEvent);
  public MOPResult mopSetProperty(MOPPropertyEvent mopEvent);
  public MOPResult mopInvoke(MOPInvokeEvent mopEvent);
  public MOPResult mopNewInstance(MOPNewInstanceEvent mopEvent);
  public MOPResult mopOperator(MOPOperatorEvent mopEvent);
  public MOPResult mopConverter(MOPConverterEvent mopEvent);
}
