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

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PKNFCBuilder.class)
public class PKNFC implements Cloneable, Serializable {

    private static final long serialVersionUID = -2017873167088954297L;

    protected String message;
    protected String encryptionPublicKey;
    protected Boolean requiresAuthentication;

    protected PKNFC() {
    }

    public String getMessage() {
        return message;
    }

    public String getEncryptionPublicKey() {
        return encryptionPublicKey;
    }

    public Boolean getRequiresAuthentication() {
        return requiresAuthentication;
    }

    @Override
    protected PKNFC clone() {
        try {
            return (PKNFC) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKNFC instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKNFCBuilder builder() {
        return new PKNFCBuilder();
    }

    public static PKNFCBuilder builder(PKNFC nfc) {
        return builder().of(nfc);
    }
}
