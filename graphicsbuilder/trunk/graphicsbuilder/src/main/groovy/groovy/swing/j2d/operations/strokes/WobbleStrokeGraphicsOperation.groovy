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

package groovy.swing.j2d.operations.strokes

import java.awt.Stroke
import com.jhlabs.awt.WobbleStroke

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class WobbleStrokeGraphicsOperation extends AbstractStrokeGraphicsOperation {
    public static required = AbstractStrokeGraphicsOperation.optional + ['detail','amplitude']

	def detail = 2
	def amplitude = 2

	WobbleStrokeGraphicsOperation() {
       super( "wobbleStroke" )
    }

    protected Stroke createStroke() {
        return new WobbleStroke( detail as float, amplitude as float )
    }
}