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
import java.awt.geom.Ellipse2D
import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class EllipseGraphicsOperation extends AbstractShapeGraphicsOperation {
    public static required = ['cx','cy','radiusx','radiusy']

    def cx = 10
    def cy = 5
    def radiusx = 10
    def radiusy = 5

    public EllipseGraphicsOperation() {
        super( "ellipse" )
    }

    public Shape getShape( GraphicsContext context ) {
       return new Ellipse2D.Double( (cx - radiusx) as double,
                                    (cy - radiusy) as double,
                                    (radiusx * 2) as double,
                                    (radiusy * 2) as double )
    }
    
    public boolean hasCenter() {
       true
    }
}