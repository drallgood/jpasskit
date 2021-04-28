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
 * Allows constructing and validating {@link PWAssociatedApp} entities.
 *
 * @author Igor Stepanov
 */
@JsonPOJOBuilder(withPrefix = "")
public class PWAssociatedAppBuilder implements IPKBuilder<PWAssociatedApp> {

    private PWAssociatedApp associatedApp;

    protected PWAssociatedAppBuilder() {
        this.associatedApp = new PWAssociatedApp();
    }

    @Override
    public PWAssociatedAppBuilder of(final PWAssociatedApp source) {
        if (source != null) {
            this.associatedApp = source.clone();
        }
        return this;
    }

    public PWAssociatedAppBuilder title(final String title) {
        this.associatedApp.title = title;
        return this;
    }

    public PWAssociatedAppBuilder idGooglePlay(final String idGooglePlay) {
        this.associatedApp.idGooglePlay = idGooglePlay;
        return this;
    }

    public PWAssociatedAppBuilder idAmazon(final String idAmazon) {
        this.associatedApp.idAmazon = idAmazon;
        return this;
    }

    @Override
    public PWAssociatedApp build() {
        return this.associatedApp;
    }
}
