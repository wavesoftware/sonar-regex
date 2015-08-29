/*
 * Copyright (C) 2015 Wave Software
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
package pl.wavesoftware.sonar.regex;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.model.VisitorsBridge;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.squidbridge.api.CodeVisitor;
import pl.wavesoftware.sonar.regex.internal.Resources;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckListTest {

    /**
     * Enforces that each check declared in list.
     */
    @Test
    public void count() {
        int count = 0;
        File testChecksDirectory = Resources.forObject(this).getFile("checks");
        File checksDirectory = new File(testChecksDirectory.getPath().replace("test-classes", "classes"));
        assertThat(checksDirectory).canRead();
        assertThat(checksDirectory).isDirectory();
        Collection<File> files = FileUtils.listFiles(checksDirectory, new String[]{"class"}, false);
        for (File file : files) {
            if (file.getName().endsWith("Check.class")) {
                count++;
            }
        }
        assertThat(CheckList.getChecks().size()).isEqualTo(count);
    }

    /**
     * Enforces that each check has test, name and description.
     */
    @Test
    public void test() {
        List<Class> checks = CheckList.getChecks();
        for (Class cls : checks) {
            String testName = '/' + cls.getName().replace('.', '/') + "Test.class";
            assertThat(getClass().getResource(testName))
                    .overridingErrorMessage("No test for " + cls.getSimpleName())
                    .isNotNull();
        }

        Set<String> keys = Sets.newHashSet();
        Set<String> names = Sets.newHashSet();
        @SuppressWarnings("deprecation")
        List<Rule> rules = new AnnotationRuleParser().parse("repositoryKey", checks);
        for (Rule rule : rules) {
            assertThat(keys).as("Duplicate key " + rule.getKey()).doesNotContain(rule.getKey());
            assertThat(names).as("Duplicate name " + rule.getKey() + " : " + rule.getName()).doesNotContain(rule.getName());
            keys.add(rule.getKey());
            names.add(rule.getName());

            assertThat(getClass().getResource("/org/sonar/l10n/java/rules/" + CheckList.REPOSITORY_KEY + "/" + rule.getKey() + ".html"))
                    .overridingErrorMessage("No description for " + rule.getKey())
                    .isNotNull();

            assertThat(rule.getDescription())
                    .overridingErrorMessage("Description of " + rule.getKey() + " should be in separate file")
                    .isNull();

            for (RuleParam param : rule.getParams()) {
                assertThat(param.getDescription()).overridingErrorMessage(rule.getKey() + " missing description for param " + param.getKey()).isNotEmpty();
            }
        }
    }

    @Test
    public void private_constructor() throws Exception {
        Constructor constructor = CheckList.class.getDeclaredConstructor();
        assertThat(constructor.isAccessible()).isFalse();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Ensures that all checks are able to deal with unparsable files
     */
    @Test
    public void should_not_fail_on_invalid_file() throws Exception {

        for (Class check : CheckList.getChecks()) {
            CodeVisitor visitor = (CodeVisitor) check.newInstance();
            if (visitor instanceof JavaFileScanner) {
                File file = Resources.forClass(this.getClass()).getFile("CheckListParseErrorTest.java");
                JavaAstScanner.scanSingleFile(file, new VisitorsBridge((JavaFileScanner) visitor));
            }
        }
    }

}
