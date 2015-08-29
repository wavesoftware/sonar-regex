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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class MatchResultIteratorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNext() throws Exception {
        // given
        Matcher matcher = Pattern.compile("not exists").matcher("");
        MatchResultIterator iterator = new MatchResultIterator(matcher);

        // then
        expectedException.expect(NoSuchElementException.class);

        // when
        iterator.next();
    }
}