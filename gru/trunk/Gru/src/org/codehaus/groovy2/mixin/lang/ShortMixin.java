package org.codehaus.groovy2.mixin.lang;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;
import org.codehaus.groovy2.lang.RT;

public class ShortMixin {
  public static int add(short i1, short i2) {
    return i1 + i2;
  }
  
  public static void __init__(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addSuperType(RT.getMetaClass(int.class));
      mutator.addMixin(RT.getMetaClass(ShortMixin.class));
    } finally {
      mutator.close();
    }
  }
}
