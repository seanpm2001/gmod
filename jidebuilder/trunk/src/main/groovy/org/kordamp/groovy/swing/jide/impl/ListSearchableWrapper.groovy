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
 * limitations under the License.
 */

package org.kordamp.groovy.swing.jide.impl

import javax.swing.JList
import com.jidesoft.swing.ListSearchable

import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ListSearchableWrapper extends ListSearchable implements SearchableWrapper {
   private JList list

   ListSearchableWrapper( JList list ){
      super( list )
      this.list = list
   }

   def getDelegateWidget(){ list }

   public String toString(){ "${super.toString()} -> ${list.toString()}" }

   public Object invokeMethod( String name, Object args ){
      return InvokerHelper.invokeMethod( list, name, args )
   }

   public void setProperty( String name, Object value ){
      if( ListSearchable.metaClass.hasProperty(this,name) ){
         super.setProperty( name, value )
      }else{
         InvokerHelper.setProperty( list, name, value )
      }
   }

   public Object getProperty( String name ){
      if( name == "delegateWidget") {
         return list
      }else if( ListSearchable.metaClass.hasProperty(this,name) ){
         return super.getProperty( name )
      }else{
         return InvokerHelper.getProperty( list, name );
      }
   }
}