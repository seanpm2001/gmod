/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package groovy.swing.j2d.factory

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.operations.filters.AbstractLightFilterProvider

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class LightFactory extends GraphicsOperationBeanFactory {
   public LightFactory( Class operationClass ) {
      super( operationClass, true )
   }

   public boolean isLeaf(){
      return true
   }

   public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ){
      def color = attributes.remove("color")
      if( color != null ) node.color = ColorCache.getColor(color).getRGB()
      return true
   }

   public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
      if( parent instanceof AbstractLightFilterProvider ){
         parent.addLight( child )
      }else{
         throw new IllegalArgumentException("parent doesn't allow nesting of lights.")
      }
   }
}