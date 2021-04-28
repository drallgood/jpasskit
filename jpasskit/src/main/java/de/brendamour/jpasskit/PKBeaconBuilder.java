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

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Allows constructing and validating {@link PKBeacon} entities.
 *
 * @author Igor Stepanov
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKBeaconBuilder implements IPKValidateable, IPKBuilder<PKBeacon> {

    private PKBeacon beacon;

    protected PKBeaconBuilder() {
        this.beacon = new PKBeacon();
    }

    @Override
    public PKBeaconBuilder of(final PKBeacon source) {
        if (source != null) {
            this.beacon = source.clone();
        }
        return this;
    }

    public PKBeaconBuilder major(Integer major) {
        this.beacon.major = major;
        return this;
    }

    public PKBeaconBuilder minor(Integer minor) {
        this.beacon.minor = minor;
        return this;
    }

    public PKBeaconBuilder proximityUUID(String proximityUUID) {
        this.beacon.proximityUUID = proximityUUID;
        return this;
    }

    public PKBeaconBuilder relevantText(String relevantText) {
        this.beacon.relevantText = relevantText;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {

        if (StringUtils.isEmpty(this.beacon.proximityUUID)) {
            return Collections.singletonList("Not all required Fields are set: proximityUUID");
        }
        return Collections.emptyList();
    }

    @Override
    public PKBeacon build() {
        return this.beacon;
    }
}
