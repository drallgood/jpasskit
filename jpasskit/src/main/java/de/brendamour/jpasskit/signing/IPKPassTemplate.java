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
package de.brendamour.jpasskit.signing;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public interface IPKPassTemplate {

    /**
     * Prepare pass at directory with files included in this template
     * 
     * @param tempPassDir
     *            path to directory where temporary pass will be created
     * @throws IOException
     *             if anything goes wrong while copying the files
     */
    void provisionPassAtDirectory(File tempPassDir) throws IOException;

    Map<String, ByteBuffer> getAllFiles() throws IOException;
}
