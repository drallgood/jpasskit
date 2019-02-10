/**
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

import de.brendamour.jpasskit.IPKBuilder;
import de.brendamour.jpasskit.IPKValidateable;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKFieldBuilder;
import de.brendamour.jpasskit.enums.PKPassType;
import de.brendamour.jpasskit.enums.PKTransitType;
import de.brendamour.jpasskit.util.BuilderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Allows constructing and validating {@link PKGenericPass} entities.
 *
 * @author Igor Stepanov
 */
public class PKGenericPassBuilder implements IPKValidateable, IPKBuilder<PKGenericPass> {

    private PKPassType passType;

    private List<PKFieldBuilder> headerFields;
    private List<PKFieldBuilder> primaryFields;
    private List<PKFieldBuilder> secondaryFields;
    private List<PKFieldBuilder> auxiliaryFields;
    private List<PKFieldBuilder> backFields;

    private PKTransitType transitType;

    private PKGenericPassBuilder() {
        this.passType = PKPassType.PKGenericPass;
        this.headerFields = new CopyOnWriteArrayList<>();
        this.primaryFields = new CopyOnWriteArrayList<>();
        this.secondaryFields = new CopyOnWriteArrayList<>();
        this.auxiliaryFields = new CopyOnWriteArrayList<>();
        this.backFields = new CopyOnWriteArrayList<>();
    }

    public List<PKFieldBuilder> getHeaderFieldBuilders() {
        return this.headerFields;
    }

    public List<PKFieldBuilder> getPrimaryFieldBuilders() {
        return this.primaryFields;
    }

    public List<PKFieldBuilder> getSecondaryFieldBuilders() {
        return this.secondaryFields;
    }

    public List<PKFieldBuilder> getAuxiliaryFieldBuilders() {
        return this.auxiliaryFields;
    }

    public List<PKFieldBuilder> getBackFieldBuilders() {
        return this.backFields;
    }

    @Override
    public PKGenericPassBuilder of(final PKGenericPass source) {
        this.passType = toPassType(source);
        if (source != null) {
            this.headerFields = BuilderUtils.toFieldBuilderList(source.headerFields);
            this.primaryFields = BuilderUtils.toFieldBuilderList(source.primaryFields);
            this.secondaryFields = BuilderUtils.toFieldBuilderList(source.secondaryFields);
            this.auxiliaryFields = BuilderUtils.toFieldBuilderList(source.auxiliaryFields);
            this.backFields = BuilderUtils.toFieldBuilderList(source.backFields);
        }
        return this;
    }

    public PKGenericPassBuilder of(final PKBoardingPass source) {
        if (source != null) {
            this.transitType = source.transitType;
        }
        return of(source);
    }

    public PKPassType getPassType() {
        return this.passType;
    }

    public PKGenericPassBuilder passType(PKPassType passType) {
        this.passType = passType;
        return this;
    }

    public PKGenericPassBuilder headerFieldBuilder(PKFieldBuilder field) {
        this.headerFields.add(field);
        return this;
    }

    public PKGenericPassBuilder headerField(PKField field) {
        return headerFieldBuilder(toBuilder(field));
    }

    public PKGenericPassBuilder headerFields(final List<PKField> fields) {
        if (fields == null || fields.isEmpty()) {
            this.headerFields.clear();
            return this;
        }
        for (PKField field : fields) {
            headerField(field);
        }
        return this;
    }

    public PKGenericPassBuilder primaryFieldBuilder(PKFieldBuilder field) {
        this.primaryFields.add(field);
        return this;
    }

    public PKGenericPassBuilder primaryField(PKField field) {
        return primaryFieldBuilder(toBuilder(field));
    }

    public PKGenericPassBuilder primaryFields(final List<PKField> fields) {
        if (fields == null || fields.isEmpty()) {
            this.primaryFields.clear();
            return this;
        }
        for (PKField field : fields) {
            primaryField(field);
        }
        return this;
    }

    public PKGenericPassBuilder secondaryFieldBuilder(PKFieldBuilder field) {
        this.secondaryFields.add(field);
        return this;
    }

    public PKGenericPassBuilder secondaryField(PKField field) {
        return secondaryFieldBuilder(toBuilder(field));
    }

    public PKGenericPassBuilder secondaryFields(final List<PKField> fields) {
        if (fields == null || fields.isEmpty()) {
            this.secondaryFields.clear();
            return this;
        }
        for (PKField field : fields) {
            secondaryField(field);
        }
        return this;
    }

    public PKGenericPassBuilder auxiliaryFieldBuilder(PKFieldBuilder field) {
        this.auxiliaryFields.add(field);
        return this;
    }

    public PKGenericPassBuilder auxiliaryField(PKField field) {
        return auxiliaryFieldBuilder(toBuilder(field));
    }

    public PKGenericPassBuilder auxiliaryFields(final List<PKField> fields) {
        if (fields == null || fields.isEmpty()) {
            this.auxiliaryFields.clear();
            return this;
        }
        for (PKField field : fields) {
            auxiliaryField(field);
        }
        return this;
    }

    public PKGenericPassBuilder backFieldBuilder(PKFieldBuilder field) {
        this.backFields.add(field);
        return this;
    }

    public PKGenericPassBuilder backField(PKField field) {
        return backFieldBuilder(toBuilder(field));
    }

    public PKGenericPassBuilder backFields(final List<PKField> fields) {
        if (fields == null || fields.isEmpty()) {
            this.backFields.clear();
            return this;
        }
        for (PKField pkField : fields) {
            backField(pkField);
        }
        return this;
    }

    public PKGenericPassBuilder transitType(PKTransitType transitType) {
        this.transitType = transitType;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {

        List<String> validationErrors = new ArrayList<>();

        List<PKFieldBuilder> allFields = new ArrayList<>();
        allFields.addAll(this.primaryFields);
        allFields.addAll(this.secondaryFields);
        allFields.addAll(this.headerFields);
        allFields.addAll(this.backFields);
        allFields.addAll(this.auxiliaryFields);

        for (PKFieldBuilder pkField : allFields) {
            if (!pkField.isValid()) {
                validationErrors.addAll(pkField.getValidationErrors());
            }
        }

        if (this.passType == PKPassType.PKBoardingPass) {
            if (this.transitType == null) {
                validationErrors.add("TransitType is not set");
            }
        }

        return validationErrors;
    }

    private <T extends PKGenericPass> T buildPass(T dest) {
        dest.headerFields = BuilderUtils.buildAll(this.headerFields);
        dest.primaryFields = BuilderUtils.buildAll(this.primaryFields);
        dest.secondaryFields = BuilderUtils.buildAll(this.secondaryFields);
        dest.auxiliaryFields = BuilderUtils.buildAll(this.auxiliaryFields);
        dest.backFields = BuilderUtils.buildAll(this.backFields);
        return dest;
    }

    @Override
    public PKGenericPass build() {
        return buildPass(new PKGenericPass());
    }

    public PKBoardingPass buildBoardingPass() {
        PKBoardingPass pass = buildPass(new PKBoardingPass());
        pass.transitType = this.transitType;
        return pass;
    }

    public PKCoupon buildCoupon() {
        return buildPass(new PKCoupon());
    }

    public PKEventTicket buildEventTicket() {
        return buildPass(new PKEventTicket());
    }

    public PKStoreCard buildStoreCard() {
        return buildPass(new PKStoreCard());
    }

    public static PKGenericPassBuilder builder() {
        return new PKGenericPassBuilder();
    }

    public static PKGenericPassBuilder builder(PKGenericPass pass) {
        PKGenericPassBuilder passBuilder = builder();
        passBuilder.headerFields(pass.headerFields);
        passBuilder.primaryFields(pass.primaryFields);
        passBuilder.secondaryFields(pass.secondaryFields);
        passBuilder.auxiliaryFields(pass.auxiliaryFields);
        passBuilder.backFields(pass.backFields);
        passBuilder.passType = PKPassType.PKGenericPass;
        return passBuilder;
    }

    public static PKGenericPassBuilder builder(PKCoupon pass) {
        PKGenericPassBuilder passBuilder = builder((PKGenericPass) pass);
        passBuilder.passType(PKPassType.PKCoupon);
        return passBuilder;
    }

    public static PKGenericPassBuilder builder(PKEventTicket pass) {
        PKGenericPassBuilder passBuilder = builder((PKGenericPass) pass);
        passBuilder.passType(PKPassType.PKEventTicket);
        return passBuilder;
    }

    public static PKGenericPassBuilder builder(PKStoreCard pass) {
        PKGenericPassBuilder passBuilder = builder((PKGenericPass) pass);
        passBuilder.passType(PKPassType.PKStoreCard);
        return passBuilder;
    }

    public static PKGenericPassBuilder builder(PKBoardingPass pass) {
        PKGenericPassBuilder passBuilder = builder((PKGenericPass) pass);
        passBuilder.transitType(pass.transitType);
        passBuilder.passType(PKPassType.PKBoardingPass);
        return passBuilder;
    }

    private static PKFieldBuilder toBuilder(PKField field) {
        return PKField.builder(field);
    }

    private static PKPassType toPassType(PKGenericPass pass) {
        if (pass instanceof PKBoardingPass) {
            return PKPassType.PKBoardingPass;
        } else if (pass instanceof PKCoupon) {
            return PKPassType.PKCoupon;
        } else if (pass instanceof PKEventTicket) {
            return PKPassType.PKEventTicket;
        } else if (pass instanceof PKStoreCard) {
            return PKPassType.PKStoreCard;
        }
        return PKPassType.PKGenericPass;
    }
}
