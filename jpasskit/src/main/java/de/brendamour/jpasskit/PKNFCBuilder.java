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

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Allows constructing and validating {@link PKNFC} entities.
 *
 * @author Igor Stepanov
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKNFCBuilder implements IPKBuilder<PKNFC> {

    private PKNFC nfc;

    protected PKNFCBuilder() {
        this.nfc = new PKNFC();
    }

    @Override
    public PKNFCBuilder of(final PKNFC source) {
        if (source != null) {
            this.nfc = source.clone();
        }
        return this;
    }

    public PKNFCBuilder message(String message) {
        this.nfc.message = message;
        return this;
    }

    public PKNFCBuilder encryptionPublicKey(String encryptionPublicKey) {
        this.nfc.encryptionPublicKey = encryptionPublicKey;
        return this;
    }

    public PKNFCBuilder requiresAuthentication(Boolean requiresAuthentication) {
        this.nfc.requiresAuthentication = requiresAuthentication;
        return this;
    }

    @Override
    public PKNFC build() {
        return this.nfc;
    }
}
