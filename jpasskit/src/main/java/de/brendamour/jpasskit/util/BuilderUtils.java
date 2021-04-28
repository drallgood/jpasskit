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
package de.brendamour.jpasskit.util;


import de.brendamour.jpasskit.IPKBuilder;
import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKBarcodeBuilder;
import de.brendamour.jpasskit.PKBeacon;
import de.brendamour.jpasskit.PKBeaconBuilder;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKFieldBuilder;
import de.brendamour.jpasskit.PKLocation;
import de.brendamour.jpasskit.PKLocationBuilder;
import de.brendamour.jpasskit.PWAssociatedApp;
import de.brendamour.jpasskit.PWAssociatedAppBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Allows constructing {@link List} of Wallet API entities from  {@link List} of the appropriate builders and vice versa.
 *
 * @author Igor Stepanov
 */
public class BuilderUtils {

    public static <T, B extends IPKBuilder<T>> List<T> buildAll(List<B> builders) {
        if (isEmpty(builders)) {
            return Collections.emptyList();
        }
        return builders.stream()
                .map(IPKBuilder::build)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static List<PKFieldBuilder> toFieldBuilderList(List<PKField> fields) {
        return toBuilderList(fields, PKField::builder);
    }

    public static List<PKBeaconBuilder> toBeaconBuilderList(List<PKBeacon> beacons) {
        return toBuilderList(beacons, PKBeacon::builder);
    }

    public static List<PKBarcodeBuilder> toBarcodeBuilderList(List<PKBarcode> barcodes) {
        return toBuilderList(barcodes, PKBarcode::builder);
    }

    public static List<PKLocationBuilder> toLocationBuilderList(List<PKLocation> locations) {
        return toBuilderList(locations, PKLocation::builder);
    }

    public static List<PWAssociatedAppBuilder> toAssociatedAppBuilderList(List<PWAssociatedApp> associatedApps) {
        return toBuilderList(associatedApps, PWAssociatedApp::builder);
    }

    public static <T, B extends IPKBuilder<T>> List<B> toBuilderList(List<T> entities, Function<T,B> toBuilder) {
        if (isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(toBuilder)
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
}
