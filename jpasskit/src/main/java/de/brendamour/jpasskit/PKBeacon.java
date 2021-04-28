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

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonDeserialize(builder = PKBeaconBuilder.class)
public class PKBeacon implements Cloneable, Serializable {

    private static final long serialVersionUID = -2017884167088954297L;

    protected Integer major;
    protected Integer minor;
    protected String proximityUUID;
    protected String relevantText;

    protected PKBeacon() {
    }

    public Integer getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public String getProximityUUID() {
        return proximityUUID;
    }

    public String getRelevantText() {
        return relevantText;
    }

    @Override
    protected PKBeacon clone() {
        try {
            return (PKBeacon) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKBeacon instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKBeaconBuilder builder() {
        return new PKBeaconBuilder();
    }

    public static PKBeaconBuilder builder(PKBeacon beacon) {
        return builder().of(beacon);
    }
}
