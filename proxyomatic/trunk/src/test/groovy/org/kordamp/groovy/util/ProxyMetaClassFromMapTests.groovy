/*
 * Copyright 2008 the original author or authors.
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

import static org.kordamp.groovy.util.ProxyOMatic.proxy
import static org.kordamp.groovy.util.ProxyOMatic.methodKey

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ProxyMetaClassFromMapTests extends AbstractProxyMetaClassTestCase {
   def proxyFromFooWithPropertiesNode() {
      proxy( Foo, [
         properties: {
            foo("Foo")
         }
      ])
   }
   
   def proxyFromFooWithGetPropertyMethod() {
      proxy( Foo, [
         getProperty: { String name -> "Foo" }
      ])
   }
   
   def proxyFromFooWithPropertyMissing_get() {
      proxy( Foo, [
         propertyMissing: { String name -> "Foo" }
      ])
   }
   
   def proxyFromFooWithSetPropertyMethod() {
      def map = [foo:"Foo"]
      proxy( Foo, [
         getProperty: { String name -> map[name] },
         setProperty: { String name, value -> map[name] = value }
      ])
   }
   
   def proxyFromFooWithPropertyMissing_set() {
      def map = [foo:"Foo"]
      def impl = [:]
      impl[methodKey('propertyMissing',[String])] = { String name -> map[name] }
      impl[methodKey('propertyMissing',[String,Object])] = { String name, value -> map[name] = value }
      proxy( Foo, impl )
   }
   
   void testGetPropertyDefinedWithMapKey() {
      def foo = proxy( Foo, [
         foo: "Foo"
      ])
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
   }
   
   void testSetPropertyDefinedWithMapKey() {
      def foo = proxy( Foo, [
         foo: "Foo"
      ])
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo did not return 'Foo'", "Foo", foo.foo )
      foo.foo = "Bar"
      assertEquals( "proxy.foo did not return 'Bar'", "Bar", foo.foo )
   }
}