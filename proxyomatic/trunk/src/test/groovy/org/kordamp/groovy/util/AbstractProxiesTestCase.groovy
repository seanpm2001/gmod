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

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractProxiesTestCase extends GroovyTestCase {
   void testBuildProxyFromFoo() {
      def foo = proxyFromFoo()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
   }
   
   void testBuildProxyFromFooBar() {
      def foo = proxyFromFooBar()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertTrue( "proxy is not of type Bar", foo instanceof Bar )
      assertTrue( "proxy is not of type FooBar", foo instanceof FooBar )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
      assertEquals( "proxy.bar() did not return 'Bar'", "Bar", foo.bar() )
      assertEquals( "proxy.foobar() did not return 'FooBar'", "FooBar", foo.foobar() )  
   }
   
   void testBuildProxyFromFooBarWithSelfMethodCalls() {
      def foo = proxyFromFooBarWithSelfMethodCalls()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertTrue( "proxy is not of type Bar", foo instanceof Bar )
      assertTrue( "proxy is not of type FooBar", foo instanceof FooBar )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
      assertEquals( "proxy.bar() did not return 'Bar'", "Bar", foo.bar() )
      assertEquals( "proxy.foobar() did not return 'FooBar'", "FooBar", foo.foobar() )  
   }
   
   void testBuildProxyFromFooAndBar() {
      def foo = proxyFromFooAndBar()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertTrue( "proxy is not of type Bar", foo instanceof Bar )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
      assertEquals( "proxy.bar() did not return 'Bar'", "Bar", foo.bar() )
   }

   void testBuildProxyFromFooWithIncompleteImpl() {
      def foo = proxyFromFooImcompleteImpl()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      shouldFail( UnsupportedOperationException ) {
         foo.foo()
      }
   }

   void testBuildProxyFromFooWithInvokeMethod() {
      def foo = proxyFromFooWithInvokeMethod()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
   }

   void testBuildProxyFromFooWithMethodMissing() {
      def foo = proxyFromFooWithMethodMissing()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
   }
   
   void testBuildProxyFromFooAndList() {
      def foo = proxyFromFooAndList()
      
      assertNotNull( "proxy is null", foo )
      assertTrue( "proxy is not of type Foo", foo instanceof Foo )
      assertTrue( "proxy is not of type Foo", foo instanceof List )
      assertEquals( "proxy.foo() did not return 'Foo'", "Foo", foo.foo() )
   }
}