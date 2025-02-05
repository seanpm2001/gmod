/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package groovyx.transform;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Generates method signatures that are compatible with Scala's conventions.</p>
 * It adds Scala-friendly property accessors for each property
 * found in the annotated class. It also generates Scala compliant
 * operator overloading methods for the following operators:
 * +, -, *, /, %, ^, &amp, |,**, &lt;&lt;, &gt;&gt;, - (unary),
 * + (unary), ~ (unary); whenever their Groovy counterparts are
 * present.
 *
 * @author Andres Almiray
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE})
@GroovyASTTransformationClass("org.codehaus.groovy.transform.ScalifyASTTransformation")
public @interface Scalify {
}
