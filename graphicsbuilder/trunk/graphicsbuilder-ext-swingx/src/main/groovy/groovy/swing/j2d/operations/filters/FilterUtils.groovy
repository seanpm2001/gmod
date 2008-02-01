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

package groovy.swing.j2d.operations.filters

import groovy.swing.j2d.ColorCache
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
final class FilterUtils  {
   static def getColor( value ){
      if( value instanceof String || value instanceof Color ){
         return ColorCache.getInstance().getColor(value).rgb()
      }
      return value as int
   }

   static def getAngle( value ){
      return Math.toRadians(value)
   }

   static def getImage( value ){
      // TODO handle image and shape operations
      return value
   }
}