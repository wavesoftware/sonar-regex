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

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class CurlyBraceFormatter implements Formatter {

    private final String format;
    private final Map<String, Object> map = Maps.newHashMap();

    CurlyBraceFormatter(String format) {
        this.format = checkNotNull(format);
    }

    @Override
    public Formatter setValue(String key, Object value) {
        this.map.put(checkNotNull(key).toUpperCase(), checkNotNull(value));
        return this;
    }

    @Override
    public String format() {
        String message = format;
        Matcher matcher = Pattern.compile("\\$\\{([^\\}]+)\\}").matcher(format);
        Iterable<MatchResult> matches = MatchResultIterator.iterate(matcher);
        for (MatchResult match : matches) {
            String key = match.group(1).toUpperCase();
            String all = match.group(0);
            String value = map.containsKey(key) ? map.get(key).toString() : "";
            message = message.replace(all, value);
        }
        return message;
    }
}
