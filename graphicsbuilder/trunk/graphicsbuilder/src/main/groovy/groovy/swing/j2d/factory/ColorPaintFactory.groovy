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
import groovy.swing.j2d.operations.paints.ColorPaintGraphicsOperation

import java.awt.Color

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorPaintFactory extends AbstractGraphicsOperationFactory {
    public Object newInstance( FactoryBuilderSupport builder, Object name, Object value,
          Map properties ) throws InstantiationException, IllegalAccessException {
       ColorPaintGraphicsOperation go = new ColorPaintGraphicsOperation();
        if( value != null && value instanceof Color || value instanceof String ){
            // workaround because properties may be read-only
            builder.context.color = ColorCache.getColor( value )
        }
        return go
    }

    public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ) {
       if( attributes.containsKey( "red" ) ||
          attributes.containsKey( "green" ) ||
          attributes.containsKey( "blue" ) ||
          attributes.containsKey( "alpha") ){

          def red = attributes.remove( "red" )
          def green = attributes.remove( "green" )
          def blue = attributes.remove( "blue" )
          def alpha = attributes.remove( "alpha" )

          red = red != null ? red : 0
          green = green != null ? green : 0
          blue = blue != null ? blue : 0
          alpha = alpha != null ? alpha : 1

          red = red > 1 ? red/255 : red
          green = green > 1 ? green/255 : green
          blue = blue > 1 ? blue/255 : blue
          alpha = alpha > 1 ? alpha/255 : alpha

          attributes.color = new Color( red as float, green as float, blue as float, alpha as float )
       }

       Object color = attributes.get( "color" )

       if( color != null && color instanceof String ){
          attributes.put( "color", ColorCache.getColor( color ) )
       }

       color = attributes.get("color")
       node.color = color == null ? builder.context.color : color

       return false
    }

    public boolean isLeaf(){
        return true
    }
}