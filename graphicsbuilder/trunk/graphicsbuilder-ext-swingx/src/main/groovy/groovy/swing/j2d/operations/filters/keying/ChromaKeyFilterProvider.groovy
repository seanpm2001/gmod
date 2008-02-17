/*
 * Copyright 2007 the original author or authors.
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

package groovy.swing.j2d.operations.filters.keying

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.FilterUtils
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.ChromaKeyFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ChromaKeyFilterProvider extends PropertiesBasedFilterProvider {
   public static required = ['hTolerance','sTolerance','bTolerance','color']

   def hTolerance
   def sTolerance
   def bTolerance
   def color

   ChromaKeyFilterProvider() {
      super( "chromaKey" )
      filter = new ChromaKeyFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "color":
            return FilterUtils.getColor(value)
         case "hTolerance":
         case "sTolerance":
         case "bTolerance":
            return value as float
         default:
            return super.convertValue(property,value)
      }
   }
}