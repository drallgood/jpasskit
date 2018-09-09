/**
 * Copyright (C) 2018 Patrice Brend'amour <patrice@brendamour.net>
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PKBeacon implements IPKValidateable {

    private static final long serialVersionUID = -2017884167088954297L;

    private Integer major;
    private Integer minor;
    private String proximityUUID;
    private String relevantText;

    public Integer getMajor() {
        return major;
    }

    public void setMajor(final Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(final Integer minor) {
        this.minor = minor;
    }

    public String getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(final String proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public String getRelevantText() {
        return relevantText;
    }

    public void setRelevantText(final String relevantText) {
        this.relevantText = relevantText;
    }

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    public List<String> getValidationErrors() {

        List<String> validationErrors = new ArrayList<String>();
        if (StringUtils.isEmpty(proximityUUID)) {
            validationErrors.add("Not all required Fields are set: proximityUUID");
        }
        return validationErrors;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
