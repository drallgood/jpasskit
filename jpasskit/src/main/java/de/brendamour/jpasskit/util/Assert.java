/*
 * Copyright (C) 2019 Patrice Brend'amour <patrice@brendamour.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.brendamour.jpasskit.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Implements basic validations, similar to {@code org.springframework.util.Assert}.
 */
public class Assert {

    private Assert() {
    }

    public static void notNull(Object value, String comment, Object... params) {
        isTrue(value != null, comment, params);
    }

    public static void hasLength(CharSequence value, String comment, Object... params) {
        isTrue(StringUtils.isNotEmpty(value), comment, params);
    }

    public static void isTrue(boolean flag, String comment, Object... params) {
        if (!flag) {
            throw new IllegalArgumentException(message(comment, params));
        }
    }

    public static void state(boolean flag, String comment, Object... params) {
        if (!flag) {
            throw new IllegalStateException(message(comment, params));
        }
    }

    public static String message(String message, Object... params) {
        String result = message;
        if (StringUtils.isNotEmpty(message)) {
            result = String.format(message, params);
        }
        return result;
    }
}
