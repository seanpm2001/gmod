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

import groovy.swing.j2d.GraphicsOperation
import groovy.swing.j2d.operations.Grouping
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.MultiPaintProvider
import groovy.swing.j2d.operations.BorderPaintProvider
import groovy.swing.j2d.operations.PaintProvider
import groovy.swing.j2d.operations.ShapeProvider
import groovy.swing.j2d.operations.Filterable
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.misc.GroupGraphicsOperation
import groovy.swing.j2d.operations.shapes.AreaGraphicsOperation
import groovy.swing.j2d.operations.strokes.ShapeStrokeGraphicsOperation

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractGraphicsOperationFactory extends AbstractFactory {
    public void setParent( FactoryBuilderSupport builder, Object parent, Object child ){
       /*
       if( child instanceof FilterProvider && parent instanceof Filterable ){
          parent.addFilter( child )
          return
       }
       */

       if( child instanceof ShapeProvider && parent instanceof ShapeStrokeGraphicsOperation ){
          parent.addShape( child )
          return
       }

       if( child instanceof ShapeProvider && parent instanceof AreaGraphicsOperation ){
          parent.addOperation( child )
          return
       }

       if( (child instanceof PaintProvider || child instanceof MultiPaintProvider) &&
             parent instanceof BorderPaintProvider ){
          parent.setPaint( child )
          return
       }

       if( (child instanceof PaintProvider || child instanceof MultiPaintProvider) &&
           (parent instanceof ShapeProvider || parent instanceof Grouping ) ){
          parent.addOperation( child )
          return
       }

       if( child instanceof PaintProvider && parent instanceof MultiPaintProvider ){
          parent.addPaint( child )
          return
       }

       if( parent instanceof Grouping ){
             parent.addOperation( child )
          return
       }

       if( !(child instanceof GraphicsOperation) ) return
       throw new IllegalArgumentException("$parent does not support nesting of other operations")
   }
}