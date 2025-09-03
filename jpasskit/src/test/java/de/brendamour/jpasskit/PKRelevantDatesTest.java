/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.brendamour.jpasskit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class PKRelevantDatesTest {

    private static final Instant DATE = Instant.parse("2024-01-15T10:00:00Z");
    private static final Instant START_DATE = Instant.parse("2024-01-10T09:00:00Z");
    private static final Instant END_DATE = Instant.parse("2024-01-20T18:00:00Z");

    private PKRelevantDatesBuilder builder;

    private void fillProperties() {
        builder.date(DATE)
                .startDate(START_DATE)
                .endDate(END_DATE);
    }

    @BeforeMethod
    public void prepareTest() {
        builder = PKRelevantDates.builder();
        fillProperties();
    }

    @Test
    public void test_builder() {
        assertThat(builder.build())
                .hasFieldOrPropertyWithValue("date", DATE)
                .hasFieldOrPropertyWithValue("startDate", START_DATE)
                .hasFieldOrPropertyWithValue("endDate", END_DATE);
    }

    @Test
    public void test_getters() {
        PKRelevantDates relevantDates = builder.build();

        assertThat(relevantDates.getDate()).isEqualTo(DATE);
        assertThat(relevantDates.getStartDate()).isEqualTo(START_DATE);
        assertThat(relevantDates.getEndDate()).isEqualTo(END_DATE);
    }

    @Test
    public void test_clone() {
        PKRelevantDates relevantDates = builder.build();
        PKRelevantDates copy = PKRelevantDates.builder(relevantDates).build();

        assertThat(copy)
                .isNotSameAs(relevantDates)
                .usingRecursiveComparison()
                .isEqualTo(relevantDates);
        assertThat(copy.getDate()).isEqualTo(DATE);
        assertThat(copy.getStartDate()).isEqualTo(START_DATE);
        assertThat(copy.getEndDate()).isEqualTo(END_DATE);
    }

    @Test
    public void test_toString() {
        PKRelevantDates relevantDates = builder.build();
        assertThat(relevantDates.toString())
                .contains(DATE.toString())
                .contains(START_DATE.toString())
                .contains(END_DATE.toString());
    }

    @Test
    public void test_builder_partial() {
        PKRelevantDates relevantDates = PKRelevantDates.builder()
                .date(DATE)
                .build();

        assertThat(relevantDates.getDate()).isEqualTo(DATE);
        assertThat(relevantDates.getStartDate()).isNull();
        assertThat(relevantDates.getEndDate()).isNull();
    }

    @Test
    public void test_builder_of_null() {
        PKRelevantDates relevantDates = PKRelevantDates.builder()
                .of(null)
                .date(DATE)
                .build();

        assertThat(relevantDates.getDate()).isEqualTo(DATE);
        assertThat(relevantDates.getStartDate()).isNull();
        assertThat(relevantDates.getEndDate()).isNull();
    }
}
