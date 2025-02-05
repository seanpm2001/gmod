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

package groovy.swing.j2d.operations.outlines

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.operations.OutlineProvider
import groovy.swing.j2d.operations.AbstractDrawingGraphicsOperation

import java.awt.Shape

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractOutlineGraphicsOperation extends AbstractDrawingGraphicsOperation implements OutlineProvider {
    public static optional = AbstractDrawingGraphicsOperation.optional - ['fill']

    public AbstractOutlineGraphicsOperation( String name ) {
        super( name )
    }

    //public Shape getShape( GraphicsContext context ){ null }

    protected void fill( GraphicsContext context, Shape shape ) {
       // empty
    }
}