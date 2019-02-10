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
package de.brendamour.jpasskit.util;

import de.brendamour.jpasskit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Allows constructing {@link List} of Wallet API entities from  {@link List} of the appropriate builders and vice versa.
 * TODO: use Java 8 API
 *
 * @author Igor Stepanov
 */
public class BuilderUtils {

    public static <T, B extends IPKBuilder<T>> List<T> buildAll(List<B> builders) {
        if (builders == null || builders.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> fields = new ArrayList<>();
        for (B builder : builders) {
            fields.add(builder.build());
        }
        return Collections.unmodifiableList(fields);
    }

    public static List<PKFieldBuilder> toFieldBuilderList(List<PKField> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyList();
        }
        List<PKFieldBuilder> builders = new CopyOnWriteArrayList<>();
        for (PKField field : fields) {
            builders.add(PKField.builder(field));
        }
        return builders;
    }

    public static List<PKBeaconBuilder> toBeaconBuilderList(List<PKBeacon> beacons) {
        if (beacons == null || beacons.isEmpty()) {
            return Collections.emptyList();
        }
        List<PKBeaconBuilder> builders = new CopyOnWriteArrayList<>();
        for (PKBeacon beacon : beacons) {
            builders.add(PKBeacon.builder(beacon));
        }
        return builders;
    }

    public static List<PKBarcodeBuilder> toBarcodeBuilderList(List<PKBarcode> barcodes) {
        if (barcodes == null || barcodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<PKBarcodeBuilder> builders = new CopyOnWriteArrayList<>();
        for (PKBarcode barcode : barcodes) {
            builders.add(PKBarcode.builder(barcode));
        }
        return builders;
    }

    public static List<PKLocationBuilder> toLocationBuilderList(List<PKLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            return Collections.emptyList();
        }
        List<PKLocationBuilder> builders = new CopyOnWriteArrayList<>();
        for (PKLocation location : locations) {
            builders.add(PKLocation.builder(location));
        }
        return builders;
    }

    public static List<PWAssociatedAppBuilder> toAssociatedAppBuilderList(List<PWAssociatedApp> associatedApps) {
        if (associatedApps == null || associatedApps.isEmpty()) {
            return Collections.emptyList();
        }
        List<PWAssociatedAppBuilder> builders = new CopyOnWriteArrayList<>();
        for (PWAssociatedApp associatedApp : associatedApps) {
            builders.add(PWAssociatedApp.builder(associatedApp));
        }
        return builders;
    }
}
