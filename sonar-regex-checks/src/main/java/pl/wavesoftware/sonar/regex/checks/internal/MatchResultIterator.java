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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 * @since 2015-08-29
 */
public class MatchResultIterator implements Iterator<MatchResult> {
    // Keep a match around that supports any interleaving of hasNext/next calls.
    private MatchResult pending;

    private final Matcher matcher;

    public MatchResultIterator(final Matcher matcher) {
        this.matcher = matcher;
    }

    public static Iterable<MatchResult> iterate(final Matcher matcher) {
        return new Iterable<MatchResult>() {
            public Iterator<MatchResult> iterator() {
                return new MatchResultIterator(matcher);
            }
        };
    }

    public boolean hasNext() {
        // Lazily fill pending, and avoid calling find() multiple times if the
        // clients call hasNext() repeatedly before sampling via next().
        if (pending == null && matcher.find()) {
            pending = matcher.toMatchResult();
        }
        return pending != null;
    }

    public MatchResult next() {
        // Fill pending if necessary (as when clients call next() without
        // checking hasNext()), throw if not possible.
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        // Consume pending so next call to hasNext() does a find().
        MatchResult next = pending;
        pending = null;
        return next;
    }

    /**
     * Required to satisfy the interface, but unsupported.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
