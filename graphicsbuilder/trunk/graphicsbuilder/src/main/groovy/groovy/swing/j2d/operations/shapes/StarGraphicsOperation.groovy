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

import java.awt.Shape
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext
import org.kordamp.jsilhouette.geom.Star

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class StarGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = AbstractShapeGraphicsOperation.required + ['cx','cy','or','ir']
    public static optional = AbstractShapeGraphicsOperation.optional + ['angle','count']

    private Star star

    def cx = 5
    def cy = 5
    def or = 8
    def ir = 3
    def count = 5
    def angle = 0

    public StarGraphicsOperation() {
        super( "star" )
    }

    protected void localPropertyChange( PropertyChangeEvent event ){
       super.localPropertyChange( event )
       star = null
    }

    public Shape getShape( GraphicsContext context ) {
       if( star == null ){
          calculateStar()
       }
       return star
    }

    public boolean hasCenter() {
       true
    }
    
    private void calculateStar() {
       star = new Star( cx as float,
                        cy as float,
                        or as float,
                        ir as float,
                        count as int,
                        angle as float )
    }
}