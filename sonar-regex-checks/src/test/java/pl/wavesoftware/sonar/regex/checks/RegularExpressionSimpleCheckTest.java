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

package pl.wavesoftware.sonar.regex.checks;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.model.VisitorsBridge;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.squidbridge.api.AnalysisException;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;
import pl.wavesoftware.sonar.regex.checks.internal.MatchResultIterator;
import pl.wavesoftware.sonar.regex.internal.Resources;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class RegularExpressionSimpleCheckTest {

    private final RegularExpressionSimpleCheck check = new RegularExpressionSimpleCheck();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JavaFileScannerContext context;

    @Test
    public void testNodesToVisit() {
        assertThat(check.nodesToVisit()).isNotNull();
        assertThat(check.nodesToVisit()).isEmpty();
    }

    @Test
    public void testSetCharset() {
        check.setCharset(Charset.defaultCharset());
        assertThat(check).isNotNull();
    }

    @Test
    public void testInvalidPattern() {
        // given
        check.pattern = null;

        // then
        expectExceptionCauseContains("Regular expression given must not be empty!");

        // when
        run();
    }

    @Test
    public void testInvalidPattern2() {
        // given
        check.pattern = "";

        // then
        expectExceptionCauseContains("Regular expression given must not be empty!");

        // when
        run();
    }

    @Test
    public void testInvalidPattern3() {
        // given
        check.pattern = "Alice has a cat (qwerty";

        // then
        expectExceptionCauseContains("Unclosed group near", check.pattern);

        // when
        run();
    }

    @Test
    public void testPositive() {
        check.pattern = "(\\.(?:log|trace|debug|info|warn|error)\\((?!.*(?:Eid|Cin)).*\\))";
        SourceFile file = run();
        CheckMessagesVerifier.verify(file.getCheckMessages())
                .next()
                .atLine(16)
                .withMessage("Text \".warn(\"A message\")\" matches given regular expression " +
                        "\"(\\.(?:log|trace|debug|info|warn|error)\\((?!.*(?:Eid|Cin)).*\\))\"")
                .next()
                .atLine(19)
                .withMessage("Text \".error(\"ddd\")\" matches given regular expression " +
                        "\"(\\.(?:log|trace|debug|info|warn|error)\\((?!.*(?:Eid|Cin)).*\\))\"")
                .next()
                .atLine(20)
                .withMessage("Text \".error(ex)\" matches given regular expression " +
                        "\"(\\.(?:log|trace|debug|info|warn|error)\\((?!.*(?:Eid|Cin)).*\\))\"")
                .noMore();
    }

    @Test
    public void testNegative() {
        check.pattern = "not found regex";
        check.invertMode = true;
        SourceFile file = run();
        CheckMessagesVerifier.verify(file.getCheckMessages())
                .next()
                .atLine(null)
                .withMessage("File do not contain requested text, that should match given regular expression " +
                        "\"not found regex\"")
                .noMore();
    }

    @Test
    public void testNegativeWithoutInvert() {
        check.pattern = "not found regex";
        check.invertMode = false;
        SourceFile file = run();
        CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
    }

    @Test
    public void testNegative2() {
        check.pattern = "@author\\s+[a-zA-Z]+\\s+[a-zA-Z]+\\s+\\<[a-z@.]+\\>";
        check.invertMode = true;
        SourceFile file = run();
        CheckMessagesVerifier.verify(file.getCheckMessages())
                .next()
                .noMore();
    }

    @Test
    public void testIterator() {
        expectedException.expect(UnsupportedOperationException.class);
        Matcher matcher = Pattern.compile(".*").matcher("");
        MatchResultIterator iter = new MatchResultIterator(matcher);
        iter.remove();
    }

    private SourceFile run() {
        String fileName = this.getClass().getSimpleName() + "File.java";
        File file = Resources.forObject(this).getFile(fileName);
        return JavaAstScanner.scanSingleFile(file, new VisitorsBridge(check));
    }

    private void expectExceptionCauseContains(String... messages) {
        expectedException.expect(AnalysisException.class);
        org.hamcrest.Matcher<? extends Throwable> matcher = instanceOf(IllegalArgumentException.class);
        expectedException.expectCause(matcher);
        for (String message : messages) {
            expectedException.expectCause(hasMessage(containsString(message)));
        }
    }

}