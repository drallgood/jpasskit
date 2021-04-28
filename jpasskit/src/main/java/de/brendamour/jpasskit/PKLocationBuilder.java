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
package de.brendamour.jpasskit;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Allows constructing and validating {@link PKLocation} entities.
 *
 * @author Igor Stepanov
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKLocationBuilder implements IPKValidateable, IPKBuilder<PKLocation> {

    private PKLocation location;

    protected PKLocationBuilder() {
        this.location = new PKLocation();
    }

    @Override
    public PKLocationBuilder of(final PKLocation source) {
        if (source != null) {
            this.location = source.clone();
        }
        return this;
    }

    public PKLocationBuilder latitude(final double latitude) {
        this.location.latitude = latitude;
        return this;
    }

    public PKLocationBuilder longitude(final double longitude) {
        this.location.longitude = longitude;
        return this;
    }

    public PKLocationBuilder altitude(final Double altitude) {
        this.location.altitude = altitude;
        return this;
    }

    public PKLocationBuilder relevantText(final String relevantText) {
        this.location.relevantText = relevantText;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {

        if (this.location.longitude == 0 || this.location.latitude == 0) {
            return Collections.singletonList("Not all required Fields are set: longitude, latitude");
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public PKLocation build() {
        return this.location;
    }
}
