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

@JsonDeserialize(builder = PKLocationBuilder.class)
public class PKLocation implements Cloneable, Serializable {

    private static final long serialVersionUID = -2017884967088954297L;

    protected double latitude;
    protected double longitude;
    protected Double altitude;
    protected String relevantText;

    protected PKLocation() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public String getRelevantText() {
        return relevantText;
    }

    @Override
    protected PKLocation clone() {
        try {
            return (PKLocation) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKLocation instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKLocationBuilder builder() {
        return new PKLocationBuilder();
    }

    public static PKLocationBuilder builder(PKLocation location) {
        return builder().of(location);
    }
}
