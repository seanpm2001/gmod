/*
 * Copyright 2002-2007 the original author or authors.
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
package org.kordamp.groovy.util

import junit.framework.TestSuite

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxyOMaticTestSuite extends GroovyTestCase {
   public static TestSuite suite() throws Exception {
      TestSuite suite = new TestSuite()
      suite.setName( "proxy-o-matic" )

      suite.addTest( new TestSuite( ProxiesFromClosureTests ) )
      suite.addTest( new TestSuite( ProxiesFromMapTests ) )
      suite.addTest( new TestSuite( ProxiesFromExpandoTests ) )
      
      suite.addTest( new TestSuite( ProxyMetaClassFromClosureTests ) )
      suite.addTest( new TestSuite( ProxyMetaClassFromMapTests ) )
      suite.addTest( new TestSuite( ProxyMetaClassFromExpandoTests ) )

      suite.addTest( new TestSuite( ObjectMethodsFromClosureTests ) )
      suite.addTest( new TestSuite( ObjectMethodsFromMapTests ) )
      suite.addTest( new TestSuite( ObjectMethodsFromExpandoTests ) )
      
      return suite
   }
}
