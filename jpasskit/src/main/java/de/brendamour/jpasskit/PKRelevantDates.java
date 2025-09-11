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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.Instant;

@JsonDeserialize(builder = PKRelevantDatesBuilder.class)
public class PKRelevantDates implements Cloneable, Serializable {

    private static final long serialVersionUID = -8742901234567890123L;

    protected Instant date;
    protected Instant endDate;
    protected Instant startDate;

    protected PKRelevantDates() {
    }

    /**
     * The date and time when the pass becomes relevant.
     * Wallet automatically calculates a relevancy interval from this date.
     *
     * @return date as Instant
     */
    public Instant getDate() {
        return date;
    }

    /**
     * The date and time for the pass relevancy interval to end.
     * Required when providing startDate.
     *
     * @return end date as Instant
     */
    public Instant getEndDate() {
        return endDate;
    }

    /**
     * The date and time for the pass relevancy interval to begin.
     *
     * @return start date as Instant
     */
    public Instant getStartDate() {
        return startDate;
    }

    @Override
    protected PKRelevantDates clone() {
        try {
            return (PKRelevantDates) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKRelevantDates instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKRelevantDatesBuilder builder() {
        return new PKRelevantDatesBuilder();
    }

    public static PKRelevantDatesBuilder builder(PKRelevantDates relevantDates) {
        return builder().of(relevantDates);
    }
}
