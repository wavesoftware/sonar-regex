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

package pl.wavesoftware.sonar.regex.checks.internal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class CurlyBraceFormatterTest {

    private String format = "A format with ${MATH} curly replacement";

    @Test
    public void testFormat_NoReplacement() throws Exception {
        // given
        String expected = "A format with  curly replacement";
        CurlyBraceFormatter formatter = new CurlyBraceFormatter(format);

        // when
        String formated = formatter.format();

        // then
        assertThat(formated).isEqualTo(expected);
    }

    @Test
    public void testFormat_Replacement() throws Exception {
        // given
        String expected = "A format with awesome curly replacement";
        CurlyBraceFormatter formatter = new CurlyBraceFormatter(format);

        // when
        formatter.setValue("math", "awesome");
        String formated = formatter.format();

        // then
        assertThat(formated).isEqualTo(expected);
    }
}