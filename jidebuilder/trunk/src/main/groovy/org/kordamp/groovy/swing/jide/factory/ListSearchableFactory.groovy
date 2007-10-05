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

package org.kordamp.groovy.swing.jide.factory

import javax.swing.JList
import groovy.util.AbstractFactory
import groovy.util.FactoryBuilderSupport
import org.kordamp.groovy.swing.jide.impl.ListSearchableWrapper

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ListSearchableFactory extends AbstractFactory {
   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      JList list = properties.remove("list")
      if( !list ){
         list =  new JList()
      }
      return new ListSearchableWrapper( list )
   }
}