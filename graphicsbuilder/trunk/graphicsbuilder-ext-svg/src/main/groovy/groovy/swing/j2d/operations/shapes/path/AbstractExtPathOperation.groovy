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

package groovy.swing.j2d.operations.shapes.path

import groovy.swing.j2d.impl.ObservableSupport

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public abstract class AbstractExtPathOperation extends ObservableSupport implements ExtPathOperation {
    public static required = []
    public static optional = []

    private String nodeName
    String name

    public AbstractExtPathOperation( String nodeName ) {
        super()
        this.nodeName = nodeName
    }

    public String getNodeName() {
        return nodeName
    }

    void setProperty( String property, Object value ) {
       def oldValue = getProperty( property )
       super.setProperty( property, value )
       if( value != oldValue ){
          firePropertyChange( property, oldValue, value )
       }
    }
    
    public String toString() {
       return name ? "${nodeName}[${name}]": nodeName
    }
}