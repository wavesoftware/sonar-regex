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

package pl.wavesoftware.sonar.regex.plugin.internal;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.squidbridge.annotations.AnnotationBasedRulesDefinition;
import pl.wavesoftware.sonar.regex.CheckList;

import javax.annotation.Nonnull;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class RegexRulesDefinitions implements RulesDefinition {

    private static final String JAVA_KEY = "java";
    private static final String REPO_NAME = "Regular Expressions";

    @Override
    public void define(@Nonnull Context context) {
        NewRepository repo = context
                .createRepository(CheckList.REPOSITORY_KEY, JAVA_KEY)
                .setName(REPO_NAME);

        AnnotationBasedRulesDefinition.load(repo, JAVA_KEY, CheckList.getChecks());

        repo.done();
    }
}
