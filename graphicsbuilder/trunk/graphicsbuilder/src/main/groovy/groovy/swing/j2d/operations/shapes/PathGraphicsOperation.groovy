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

package groovy.swing.j2d.operations.shapes

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.shapes.path.PathOperation
import groovy.swing.j2d.operations.shapes.path.MoveToPathOperation
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

import java.awt.Shape
import java.awt.geom.GeneralPath
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class PathGraphicsOperation extends AbstractShapeGraphicsOperation  {
   public static optional = AbstractShapeGraphicsOperation.optional + ['winding','close']

   private List pathOperations = []
   private GeneralPath path

   def winding
   def close

   PathGraphicsOperation() {
      super( "path" )
   }

   public List paths() {
	   Collections.unmodifiableList(pathOperations)
   }
   
   public void addPathOperation( PathOperation operation ) {
      if( !operation ) return
      pathOperations << operation
      operation.addPropertyChangeListener( this )
   }

   public void propertyChange( PropertyChangeEvent event ){
      if( event.source instanceof PathOperation ){
         firePropertyChange( new ExtPropertyChangeEvent(this,event) )
      }else{
         super.propertyChange( event )
      }
   }

   protected void localPropertyChange( PropertyChangeEvent event ){
      super.localPropertyChange( event )
      path = null
   }

   public Shape getShape( GraphicsContext context ) {
      if( path == null ){
         calculatePath()
      }
      path
   }

   private void calculatePath( GraphicsContext context ) {
      if( pathOperations.size() > 0 && !(pathOperations[0] instanceof MoveToPathOperation) ){
         throw new IllegalStateException("You must call 'moveTo' as the first operation of a path")
      }
      path = new GeneralPath( getWindingRule() )
      pathOperations.each { pathOperation ->
         pathOperation.apply( path, context )
      }
      if( close ){
         path.closePath()
      }
   }

   private int getWindingRule() {
      if( winding == null ){
         return GeneralPath.WIND_NON_ZERO
      }

      if( winding instanceof Integer ){
         return winding
      }else if( winding instanceof String ){
         if( "non_zero".compareToIgnoreCase( winding ) == 0 ||
             "nonzero".compareToIgnoreCase( winding ) == 0 ){
            return GeneralPath.WIND_NON_ZERO
         }else if( "even_odd".compareToIgnoreCase( winding ) == 0 ||
                   "evenodd".compareToIgnoreCase( winding ) == 0){
            return GeneralPath.WIND_EVEN_ODD
         }else{
            throw new IllegalStateException( "'winding=" + winding
                  + "' is not one of [non_zero,even_odd]" )
         }
      }
      throw new IllegalStateException( "'winding' value is not a String nor an Integer" )
   }
}