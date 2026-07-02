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
 * Allows constructing and validating {@link PKRelevantDate} entities.
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKRelevantDateBuilder implements IPKBuilder<PKRelevantDate> {

    private PKRelevantDate relevantDate;

    protected PKRelevantDateBuilder() {
        this.relevantDate = new PKRelevantDate();
    }

    @Override
    public PKRelevantDateBuilder of(final PKRelevantDate source) {
        if (source == null) {
            return this;
        }

        this.relevantDate.date = source.date;
        this.relevantDate.endDate = source.endDate;
        this.relevantDate.startDate = source.startDate;

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
    public PKRelevantDateBuilder date(Instant date) {
        this.relevantDate.date = date;
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
    public PKRelevantDateBuilder endDate(Instant endDate) {
        this.relevantDate.endDate = endDate;
        return this;
    }

    /**
     * The date and time for the pass relevancy interval to begin.
     *
     * @param startDate start date as Instant
     * @return current builder instance for chaining
     */
    @JsonProperty("startDate")
    public PKRelevantDateBuilder startDate(Instant startDate) {
        this.relevantDate.startDate = startDate;
        return this;
    }

    @Override
    public PKRelevantDate build() {
        return this.relevantDate;
    }
}
