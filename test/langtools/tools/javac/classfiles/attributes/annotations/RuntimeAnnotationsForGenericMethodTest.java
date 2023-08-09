/*
 * Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8044411
 * @summary Tests the RuntimeVisibleAnnotations/RuntimeInvisibleAnnotations attribute.
 *          Checks that the attribute is generated for bridge method.
 * @modules java.base/jdk.internal.classfile
 *          java.base/jdk.internal.classfile.attribute
 *          java.base/jdk.internal.classfile.constantpool
 *          java.base/jdk.internal.classfile.instruction
 *          java.base/jdk.internal.classfile.components
 *          java.base/jdk.internal.classfile.impl
 *          jdk.compiler/com.sun.tools.javac.api
 *          jdk.compiler/com.sun.tools.javac.main
 * @library /tools/lib /tools/javac/lib ../lib
 * @build toolbox.ToolBox InMemoryFileManager TestResult TestBase
 * @build WorkAnnotations TestCase ClassType TestAnnotationInfo
 * @build RuntimeAnnotationsForGenericMethodTest AnnotationsTestBase RuntimeAnnotationsTestBase
 * @run main RuntimeAnnotationsForGenericMethodTest
 */

import java.util.ArrayList;
import java.util.List;

/**
 * RuntimeAnnotationsGenericMethodTest is a test which check that
 * RuntimeVisibleAnnotationsAttribute and RuntimeInvisibleAnnotationsAttribute
 * are generated for both generic and appropriate bridge methods.
 * All possible combinations of retention policies are tested.
 *
 * The test generates class which looks as follows:
 *
 * public class Test extends java.util.ArrayList&lt;Integer&gt; {
 *     here some annotations
 *     public boolean add(java.lang.Integer) {
 *         return false;
 *     }
 * }
 *
 * Thereafter, various of combinations of annotations are applied
 * to the add, the source is compiled and the generated byte code is checked.
 *
 * See README.txt for more information.
 */
public class RuntimeAnnotationsForGenericMethodTest extends RuntimeAnnotationsTestBase {

    @Override
    public List<TestCase> generateTestCases() {
        List<TestCase> testCases = new ArrayList<>();
        for (List<TestAnnotationInfos> groupedAnnotations : groupAnnotations(getAllCombinationsOfAnnotations())) {
            TestCase testCase = new TestCase();
            for (int i = 0; i < groupedAnnotations.size(); ++i) {
                TestAnnotationInfos annotations = groupedAnnotations.get(i);
                // generate: public class Test extends java.util.ArrayList<Integer>
                TestCase.TestClassInfo clazz = testCase.addClassInfo("java.util.ArrayList<Integer>", ClassType.CLASS, "Test" + i);
                TestCase.TestMethodInfo method = clazz.addMethodInfo("add(Integer)", "public");
                method.addParameter("Integer", "i");
                annotations.annotate(method);
                TestCase.TestMethodInfo synMethod = clazz.addMethodInfo("add(Object)", true, "public");
                annotations.annotate(synMethod);
            }
            testCases.add(testCase);
        }
        return testCases;
    }

    public static void main(String[] args) throws TestFailedException {
        new RuntimeAnnotationsForGenericMethodTest().test();
    }
}
