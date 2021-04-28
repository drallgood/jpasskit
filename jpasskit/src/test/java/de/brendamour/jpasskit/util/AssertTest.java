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

import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.Test;

import java.util.MissingFormatArgumentException;

import static de.brendamour.jpasskit.util.Assert.hasLength;
import static de.brendamour.jpasskit.util.Assert.message;
import static de.brendamour.jpasskit.util.Assert.notNull;
import static de.brendamour.jpasskit.util.Assert.state;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AssertTest {

    @Test
    public void notNullWithDummyValues() {
        notNull(Integer.valueOf(42), "Test comment");
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                notNull(null, "Instance <%s> is empty", null);
            }
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Instance <null> is empty");
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                notNull(null, "Instance <%s> is empty because of <%s>", null, null);
            }
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Instance <null> is empty because of <null>");
    }

    @Test
    public void hasLengthWithDummyValues() {
        hasLength("42", "Test comment");
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                hasLength("", "Instance <%s> is empty", "");
            }
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Instance <> is empty");
    }

    @Test
    public void stateWithDummyValues() {
        state(1 != 2, "Test comment");
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                state(3 == 4, "State is not true");
            }
        }).isInstanceOf(IllegalStateException.class).hasMessage("State is not true");
    }

    @Test
    public void messageWithNoParams() {
        assertThat(message(null)).isEqualTo(null);
        assertThat(message("")).isEqualTo("");
        assertThat(message("Qwerty123")).isEqualTo("Qwerty123");
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                message("Text %s");
            }
        }).isInstanceOf(MissingFormatArgumentException.class).hasMessage("Format specifier '%s'");
    }

    @Test
    public void messageWithParams() {
        assertThat(message("Qwerty123", "value")).isEqualTo("Qwerty123");
        assertThat(message("Text %s", "value")).isEqualTo("Text value");
        assertThat(message("Text %s %s", "value1", "value2")).isEqualTo("Text value1 value2");
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                message("Text %s %s", "value");
            }
        }).isInstanceOf(MissingFormatArgumentException.class).hasMessage("Format specifier '%s'");
    }

}
