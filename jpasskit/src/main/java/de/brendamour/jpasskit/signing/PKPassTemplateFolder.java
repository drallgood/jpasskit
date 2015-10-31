/**
 * Copyright (C) 2015 Patrice Brend'amour <patrice@brendamour.net>
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class PKPassTemplateFolder implements IPKPassTemplate {

    private String pathToTemplateDirectory;

    public PKPassTemplateFolder(URL fileUrlOfTemplateDirectory) throws UnsupportedEncodingException {
        pathToTemplateDirectory = URLDecoder.decode(fileUrlOfTemplateDirectory.getFile(), "UTF-8");
    }

    public PKPassTemplateFolder(String pathToTemplateDirectory) {
        this.pathToTemplateDirectory = pathToTemplateDirectory;
    }

    @Override
    public void provisionPassAtDirectory(File tempPassDir) throws IOException {
        FileUtils.copyDirectory(new File(pathToTemplateDirectory), tempPassDir);
    }

    @Override
    public Map<String, ByteBuffer> getAllFiles() throws IOException {
        Map<String, ByteBuffer> allFiles = new HashMap<>();
        for (File file : new File(pathToTemplateDirectory).listFiles()) {
            byte[] byteArray = IOUtils.toByteArray(new FileInputStream(file));
            String filePath = file.getAbsolutePath().replace(pathToTemplateDirectory, "");
            allFiles.put(filePath, ByteBuffer.wrap(byteArray));
        }
        return allFiles;
    }

}
