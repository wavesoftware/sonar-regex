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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.java.CharsetAwareVisitor;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.RuleTemplate;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import pl.wavesoftware.sonar.regex.checks.internal.CulryBraceFormatterFactory;
import pl.wavesoftware.sonar.regex.checks.internal.FormatterFactory;
import pl.wavesoftware.sonar.regex.checks.internal.MatchResultIterator;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-28
 */
@Rule(
        key = "RegularExpressionSimpleCheck",
        tags = { "custom", "architectural-constraints" },
        name = "Architectural constraints imposed by the regular expression should be fulfilled",
        priority = Priority.MAJOR)
@RuleTemplate
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_RELIABILITY)
@SqaleConstantRemediation("10min")
public class RegularExpressionSimpleCheck extends IssuableSubscriptionVisitor implements CharsetAwareVisitor {

    @VisibleForTesting
    @RuleProperty(description = "Mandatory. The regular expession pattern to be search")
    protected String pattern = "";

    @VisibleForTesting
    @RuleProperty(description = "Optional. If set rule will pass check if metch is not found. By default " +
            "this is false and possitive search is performed. Default: false")
    protected boolean invertMode = false;

    @VisibleForTesting
    @RuleProperty(description = "Optional. The message that will be registered as issue if positive check fails. By default: " +
            "'" + DEFAULT_ISSUE_MESSAGE + "'")
    protected String issueMessage = DEFAULT_ISSUE_MESSAGE;

    @VisibleForTesting
    @RuleProperty(description = "Optional. The message that will be registered as issue if invert mode check fails. " +
            "By default: '" + DEFAULT_INVERT_MODE_ISSUE_MESSAGE + "'")
    protected String invertModeIssueMessage = DEFAULT_INVERT_MODE_ISSUE_MESSAGE;

    private Charset charset;

    private static final String REGEX_KEY = "REGEX";
    private static final String MATCH_KEY = "MATCH";
    private static final String DEFAULT_ISSUE_MESSAGE = "Text \"${" + MATCH_KEY + "}\" matches given regular expression \"${"
            + REGEX_KEY + "}\"";
    private static final String DEFAULT_INVERT_MODE_ISSUE_MESSAGE = "File do not contain requested text, that should match " +
            "given regular expression \"${" + REGEX_KEY + "}\"";

    private static final FormatterFactory FORMATTER_FACTORY = new CulryBraceFormatterFactory();

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.emptyList();
    }

    @Override
    public void scanFile(JavaFileScannerContext context) {
        try {
            super.context = context;
            super.scanFile(context);
            if (context.getSemanticModel() != null) {
                visitContext(context);
            }
        } catch (IOException | PatternSyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public void setCharset(@Nonnull Charset charset) {
        this.charset = checkNotNull(charset);
    }

    private void visitContext(JavaFileScannerContext context) throws IOException, PatternSyntaxException {
        validatePattern();
        String content = readFile(context.getFile());
        Pattern patternObj = Pattern.compile(pattern);
        Matcher matcher = patternObj.matcher(content);
        Iterable<MatchResult> resultIterable = MatchResultIterator.iterate(matcher);
        List<MatchResult> results = Lists.newArrayList(resultIterable);
        boolean atLeastOne = results.iterator().hasNext();
        if (atLeastOne) {
            addIssuesOnFoundMatches(content, results);
        } else if (invertMode) {
            String issue = FORMATTER_FACTORY.createFrom(invertModeIssueMessage)
                    .setValue(REGEX_KEY, pattern)
                    .format();
            addIssueOnFile(issue);
        }
    }

    private void validatePattern() {
        if (pattern == null || pattern.isEmpty()) {
            throw new PatternSyntaxException("Regular expression given must not be empty!", pattern, 0);
        }
    }

    private void addIssuesOnFoundMatches(String content, Iterable<MatchResult> resultIterable) {
        for (MatchResult matchResult : resultIterable) {
            int start = matchResult.start();
            String match = matchResult.group();
            int lineNo = getLineNumer(start, content);
            String message = FORMATTER_FACTORY.createFrom(issueMessage)
                    .setValue(MATCH_KEY, match)
                    .setValue(REGEX_KEY, pattern)
                    .format();
            addIssue(lineNo, message);
        }
    }

    private int getLineNumer(int start, String content) {
        String sub = content.substring(0, start);
        return sub.split("\n").length;
    }

    private String readFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, charset);
    }

}
