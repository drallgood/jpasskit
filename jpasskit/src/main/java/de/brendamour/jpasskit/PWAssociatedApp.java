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

/**
 * There is powerful implementation of Apple Wallet API for Android, which is called PassWallet.
 * As of PassWallet v1.31, support for linking a pass to an Android app was added.
 * One or more apps can be associated with the pass.
 * Individual package names can be specified for Google Play and Amazon App Store to handle there being platform specific variations of the app.
 *
 * @author Igor Stepanov
 */
@JsonDeserialize(builder = PWAssociatedAppBuilder.class)
public class PWAssociatedApp implements Cloneable, Serializable {

    private static final long serialVersionUID = -9021012408747538251L;

    protected String title;
    protected String idGooglePlay;
    protected String idAmazon;

    protected PWAssociatedApp() {
    }

    public String getTitle() {
        return title;
    }

    public String getIdGooglePlay() {
        return idGooglePlay;
    }

    public String getIdAmazon() {
        return idAmazon;
    }

    @Override
    protected PWAssociatedApp clone() {
        try {
            return (PWAssociatedApp) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PWAssociatedApp instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PWAssociatedAppBuilder builder() {
        return new PWAssociatedAppBuilder();
    }

    public static PWAssociatedAppBuilder builder(PWAssociatedApp associatedApp) {
        return builder().of(associatedApp);
    }
}
