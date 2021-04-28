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
package de.brendamour.jpasskit.passes;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.PKField;

public class PKGenericPass implements Cloneable, Serializable {

    private static final long serialVersionUID = 3408389190364251557L;

    protected List<PKField> headerFields;
    protected List<PKField> primaryFields;
    protected List<PKField> secondaryFields;
    protected List<PKField> auxiliaryFields;
    protected List<PKField> backFields;

    protected PKGenericPass() {
    }

    public List<PKField> getPrimaryFields() {
        return primaryFields;
    }

    public List<PKField> getSecondaryFields() {
        return secondaryFields;
    }

    public List<PKField> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public List<PKField> getBackFields() {
        return backFields;
    }

    public List<PKField> getHeaderFields() {
        return headerFields;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKGenericPassBuilder builder() {
        return new PKGenericPassBuilder();
    }

    public static PKGenericPassBuilder builder(PKGenericPass pass) {
        PKGenericPassBuilder passBuilder = builder();
        return passBuilder.of(pass);
    }
}
