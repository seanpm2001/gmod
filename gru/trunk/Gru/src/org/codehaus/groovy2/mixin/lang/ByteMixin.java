package org.codehaus.groovy2.mixin.lang;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class ByteMixin {
  public static int add(byte i1, byte i2) {
    return i1 + i2;
  }
  
  public static void __init__(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(short.class));
      mutator.addMixin(RT.getMetaClass(ByteMixin.class));
    } finally {
      mutator.close();
    }
  }
}
