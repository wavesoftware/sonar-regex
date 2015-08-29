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

import com.google.common.collect.ImmutableList;
import org.sonar.plugins.java.api.JavaCheck;
import pl.wavesoftware.sonar.regex.checks.RegularExpressionSimpleCheck;

import java.util.List;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public final class CheckList {

    public static final String REPOSITORY_KEY = "regex";

    private CheckList() {

    }

    public static List<Class> getChecks() {
        return ImmutableList.<Class>builder().addAll(getJavaChecks()).build();
    }

    private static List<Class<? extends JavaCheck>> getJavaChecks() {
        return ImmutableList.<Class<? extends JavaCheck>>builder()
                .add(RegularExpressionSimpleCheck.class)
                .build();
    }
}
