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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.Instant;

/**
 * Allows constructing and validating {@link PKRelevantDates} entities.
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKRelevantDatesBuilder implements IPKBuilder<PKRelevantDates> {

    private PKRelevantDates relevantDates;

    protected PKRelevantDatesBuilder() {
        this.relevantDates = new PKRelevantDates();
    }

    @Override
    public PKRelevantDatesBuilder of(final PKRelevantDates source) {
        if (source == null) {
            return this;
        }

        this.relevantDates.date = source.date;
        this.relevantDates.endDate = source.endDate;
        this.relevantDates.startDate = source.startDate;

        return this;
    }

    /**
     * The date and time when the pass becomes relevant.
     * Wallet automatically calculates a relevancy interval from this date.
     *
     * @param date date as Instant
     * @return current builder instance for chaining
     */
    @JsonProperty("date")
    public PKRelevantDatesBuilder date(Instant date) {
        this.relevantDates.date = date;
        return this;
    }

    /**
     * The date and time for the pass relevancy interval to end.
     * Required when providing startDate.
     *
     * @param endDate end date as Instant
     * @return current builder instance for chaining
     */
    @JsonProperty("endDate")
    public PKRelevantDatesBuilder endDate(Instant endDate) {
        this.relevantDates.endDate = endDate;
        return this;
    }

    /**
     * The date and time for the pass relevancy interval to begin.
     *
     * @param startDate start date as Instant
     * @return current builder instance for chaining
     */
    @JsonProperty("startDate")
    public PKRelevantDatesBuilder startDate(Instant startDate) {
        this.relevantDates.startDate = startDate;
        return this;
    }

    @Override
    public PKRelevantDates build() {
        return this.relevantDates;
    }
}
