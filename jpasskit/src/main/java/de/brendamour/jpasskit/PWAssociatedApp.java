/**
 * Copyright (C) 2017 Patrice Brend'amour <patrice@brendamour.net>
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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PWAssociatedApp implements IPKValidateable {

    private static final long serialVersionUID = -9021012408747538251L;

    private String title;
    private String idGooglePlay;
    private String idAmazon;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getIdGooglePlay() {
        return idGooglePlay;
    }

    public void setIdGooglePlay(final String idGooglePlay) {
        this.idGooglePlay = idGooglePlay;
    }

    public String getIdAmazon() {
        return idAmazon;
    }

    public void setIdAmazon(final String idAmazon) {
        this.idAmazon = idAmazon;
    }

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    public List<String> getValidationErrors() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
