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

package groovy.swing.j2d.operations

import groovy.swing.j2d.GraphicsContext

import java.awt.Shape
import java.awt.geom.GeneralPath
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.impl.AbstractShapeGraphicsOperation
import groovy.swing.j2d.impl.ExtPathOperation
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ExtPathGraphicsOperation extends AbstractShapeGraphicsOperation {
    protected static optional = super.optional + ['winding']

    private List pathOperations = []
    private ExtendedGeneralPath path

    def winding

    ExtPathGraphicsOperation() {
      super( "path" )
    }

   public void addPathOperation( ExtPathOperation operation ) {
      if( !operation ) return
      pathOperations << operation
      operation.addPropertyChangeListener( this )
   }

   public void propertyChange( PropertyChangeEvent event ){
      //if( pathOperations.contains(event.source) ){
         path = null
      //}
   }

   public Shape getShape( GraphicsContext context ) {
      if( path == null ){
         calculatePath()
      }
      path
   }

   private void calculatePath( GraphicsContext context ) {
      if( pathOperations.size() > 0 && !(pathOperations[0] instanceof MoveToExtPathOperation) ){
         throw new IllegalStateException("You must call 'moveTo' as the first operation of a path")
      }
      path = new ExtendedGeneralPath( getWindingRule() )
      pathOperations.each { pathOperation ->
         pathOperation.apply( path, context )
      }
      path.closePath()
   }

   private int getWindingRule() {
      if( winding == null ){
         return GeneralPath.WIND_NON_ZERO
      }

      if( winding instanceof Integer ){
         return winding
      }else if( winding instanceof String ){
         if( "non_zero".compareToIgnoreCase( windingValue ) == 0 ){
            return GeneralPath.WIND_NON_ZERO
         }else if( "even_odd".compareToIgnoreCase( windingValue ) == 0 ){
            return GeneralPath.WIND_EVEN_ODD
         }else{
            throw new IllegalStateException( "'winding=" + windingValue
                  + "' is not one of [non_zero,even_odd]" )
         }
      }
      throw new IllegalStateException( "'winding' value is not a String nor an Integer" )
   }
}