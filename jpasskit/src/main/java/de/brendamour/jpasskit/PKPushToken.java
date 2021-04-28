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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PKPushToken implements Serializable {

    private static final long serialVersionUID = 8729524057466381447L;

    private String pushToken;

    protected PKPushToken() {
    }

    public String getPushToken() {
        return pushToken;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKPushToken of(String pushToken) {
        PKPushToken pkPushToken = new PKPushToken();
        pkPushToken.pushToken = pushToken;
        return pkPushToken;
    }
}
