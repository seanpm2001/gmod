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

package groovy.swing.j2d.operations.filters

import groovy.swing.j2d.ColorCache
import groovy.swing.j2d.impl.ObservableSupport
import groovy.swing.j2d.operations.FilterProvider
import groovy.swing.j2d.operations.FilterGroup

import java.awt.Color
import java.beans.PropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractFilterProvider extends ObservableSupport implements FilterProvider {
    private String nodeName
    private Map props = new ObservableMap()
    
    //public static required = []
    public static optional = ['enabled']
    protected FilterGroup parent
    
    String name
    def enabled = true

    AbstractFilterProvider( String nodeName ) {
        super()
        this.nodeName = nodeName
        addPropertyChangeListener( this )
    }

    public String getNodeName() {
        return nodeName
    }

    public String toString() {
        return name ? "${nodeName}[${name}]": nodeName
    }
    
    public final Map getProps() {
        props  
    }

    public void propertyChange( PropertyChangeEvent event ) {
       if( event.source == this ){
          localPropertyChange( event )
       }
    }

    protected void localPropertyChange( PropertyChangeEvent event ) {

    }

    void setProperty( String property, Object value ) {
        def oldValue = getProperty( property )
        super.setProperty( property, value )
        if( isParameter(property) && compare(oldValue,value) ){
           firePropertyChange( property, oldValue, value )
        }
    }

    protected boolean isParameter( String property ) {
        if( hasProperty(this,'required') && required.contains(property) ) return true
        if( hasProperty(this,'optional') && optional.contains(property) ) return true
        false
    }

    private boolean compare( oldvalue, newvalue ){
       if( oldvalue == null && newvalue == null ) return false
       if( oldvalue == null && newvalue != null ) return true
       if( oldvalue != null && newvalue == null ) return true

       switch( oldvalue.class ){
          case Boolean:
             if( newvalue instanceof String ) return (oldvalue as String) != newvalue
             if( newvalue instanceof Boolean ) return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             return true
             break;
          case String:
             if( newvalue instanceof Boolean ) return oldvalue != (newvalue as String)
             if( newvalue instanceof Color ) return ColorCache.getColor(oldvalue) != newvalue
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
          case Color:
             if( newvalue instanceof Boolean ) return true
             if( newvalue instanceof String ) return oldvalue != ColorCache.getColor(newvalue)
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
       }
       switch( newvalue.class ){
          case Boolean:
             if( oldvalue instanceof String ) return (newvalue as String) != oldvalue
             if( oldvalue instanceof Boolean ) return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             return true
             break;
          case String:
             if( oldvalue instanceof Boolean ) return newvalue != (oldvalue as String)
             if( oldvalue instanceof Color ) return ColorCache.getColor(newvalue) != oldvalue
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
          case Color:
             if( oldvalue instanceof Boolean ) return true
             if( oldvalue instanceof String ) return newvalue != ColorCache.getColor(oldvalue)
             return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
             break;
       }

       return /*oldvalue != newvalue*/ !oldvalue.equals(newvalue)
    }

    protected boolean hasProperty( Object target, String property ){
       try{
          def v = target."$property"
       }catch( MissingPropertyException e ){
          return false
       }
       return true
    }
}
