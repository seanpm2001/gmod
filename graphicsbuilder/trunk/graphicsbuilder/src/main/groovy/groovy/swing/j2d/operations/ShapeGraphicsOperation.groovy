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
import groovy.swing.j2d.ShapeProvider
import groovy.swing.j2d.Transformable
import groovy.swing.j2d.impl.AbstractGraphicsOperation
import groovy.swing.j2d.impl.TransformationGroup
import java.awt.Shape
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ShapeGraphicsOperation extends AbstractGraphicsOperation implements ShapeProvider, Transformable {
    protected static required = ['shape']

    def shape
    TransformationGroup transformationGroup

    ShapeGraphicsOperation() {
        super( "shape" )
    }

    public void setTransformationGroup( TransformationGroup transformationGroup ){
       if( transformationGroup ) {
          if( this.transformationGroup ){
             this.transformationGroup.removePropertyChangeListener( this )
          }
          this.transformationGroup = transformationGroup
          this.transformationGroup.addPropertyChangeListener( this )
       }
    }

    public TransformationGroup getTransformationGroup() {
       transformationGroup
    }

    void setProperty( String property, Object value ) {
       if( property == "shape" ){
          super.setProperty( property, value )
          if( value instanceof ShapeProvider ){
             value.addPropertyChangeListener( this )
          }
       }else if( this.@shape != null ){
          this.@shape.setProperty( property, value )
       }
    }

    Object getProperty( String property ) {
       if( property == "shape" ){
          return this.@shape
       }else if( this.@shape != null ){
          return this.@shape.getProperty( property )
       }
       throw new MissingPropertyException( property, ShapeGraphicsOperation )
    }

    public void execute( GraphicsContext context ){
        // empty
    }

    public Shape getShape( GraphicsContext context ){
       return getActualShape( context )
    }
    public Shape getLocallyTransformedShape( GraphicsContext context ){
       return getActualShape( context )
    }
    public Shape getGloballyTransformedShape( GraphicsContext context ){
       return getActualShape( context )
    }

    private Shape getActualShape( GraphicsContext context ){
       if( shape instanceof ShapeProvider ){
          // TODO apply local
          def s = shape.getLocallyTransformedShape(context)
          if( transformationGroup && !transformationGroup.isEmpty() ){
             s = transformationGroup.apply(s)
          }
          return s
       }else if( shape instanceof Shape ){
          return shape
       }
       throw new IllegalArgumentException("shape.shape must be one of [java.awt.Shape,ShapeProvider]")
    }
}