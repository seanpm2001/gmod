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
 * limitations under the License.
 */

package org.kordamp.groovy.swing.jide.factory

import com.jidesoft.swing.NullTristateCheckBox

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class NullTristateCheckBoxFactory extends AbstractJideComponentFactory {
   public NullTristateCheckBoxFactory() {
      super( NullTristateCheckBox )
   }

   public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
      FactoryBuilderSupport.checkValueIsNull(value, name)
      def text = properties.remove("text")
      def icon = properties.remove("icon")
      def initial = properties.remove("initial")
      if( initial == null ) initial = NullTristateCheckBox.DONT_CARE
      return new NullTristateCheckBox( text, icon, initial )
   }
}