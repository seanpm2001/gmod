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

package groovy.swing.j2d.operations.filters.stylize

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.filters.ColormapAware
import groovy.swing.j2d.operations.filters.PropertiesBasedFilterProvider

import com.jhlabs.image.Colormap
import com.jhlabs.image.ShapeFilter

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapeBurstFilterProvider extends PropertiesBasedFilterProvider implements ColormapAware {
   public static required = ['factor','colormap','useAlpha','invert','merge','type']

   def factor
   Colormap colormap
   def useAlpha
   def invert
   def merge
   def type

   ShapeBurstFilterProvider() {
      super( "shapeBurst" )
      filter = new ShapeFilter()
   }

   protected def convertValue( property, value ){
      switch( property ){
         case "type":
            return getTypeValue(value)
         default:
            return super.convertValue(property,value)
      }
   }

   private int getTypeValue( value ){
      if( value instanceof Number ){
         return value as int
      }
      if( value instanceof String ){
         switch( value ){
            case "linear":
            case "burstLinear":
               return ShapeFilter.LINEAR
            case "up":
            case "circleup":
            case "circle up":
            case "circle_up":
            case "burstCircleUp":
            case "burstUp":
               return ShapeFilter.CIRCLE_UP
            case "down":
            case "circledown":
            case "circle down":
            case "circle_down":
            case "burstCircleDown":
            case "burstDown":
               return ShapeFilter.CIRCLE_DOWN
            case "smooth":
            case "burstSmooth":
               return ShapeFilter.SMOOTH
         }
         throw new IllegalArgumentException("shapeBurst.type has an invalid value of '${value}'")
      }
   }
}