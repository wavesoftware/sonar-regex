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

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import pl.wavesoftware.sonar.regex.CheckList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class RegexRulesDefinitionsTest {

    private RegexRulesDefinitions regexRulesDefinitions = new RegexRulesDefinitions();

    private RulesDefinition.Context context = new RulesDefinition.Context();

    @Test
    public void testDefine() throws Exception {
        // when
        regexRulesDefinitions.define(context);

        // then
        assertThat(context.repositories()).isNotEmpty();
        RulesDefinition.Repository repo = context.repository("regex");
        assertThat(repo).isNotNull();
        assertThat(repo.language()).isEqualTo("java");
        assertThat(repo.name()).isEqualTo("Regular Expressions");
        assertThat(repo.rules()).hasSameSizeAs(CheckList.getChecks());
    }
}