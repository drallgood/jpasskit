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
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

public class PKPassTemplateFolderTest {
    private static final String PASS_TEMPLATE_FOLDER = PKPassTemplateFolderTest.class.getClassLoader().getResource("StoreCard.raw").getPath();
    private PKPassTemplateFolder pkPassTemplateFolder;

    @BeforeMethod
    public void prepare() {
        pkPassTemplateFolder = new PKPassTemplateFolder(PASS_TEMPLATE_FOLDER);
    }

    @Test
    public void provisionTest() throws IOException, URISyntaxException {
        File tempPassDir = Files.createTempDir();
        pkPassTemplateFolder.provisionPassAtDirectory(tempPassDir);

        RegexFileFilter regexFileFilter = new RegexFileFilter("^(.*?)");
        Collection<File> templateFiles = FileUtils.listFiles(new File(PASS_TEMPLATE_FOLDER), regexFileFilter, DirectoryFileFilter.DIRECTORY);
        Collection<File> createdFiles = FileUtils.listFiles(tempPassDir, regexFileFilter, DirectoryFileFilter.DIRECTORY);
        Assert.assertEquals(createdFiles.size(), templateFiles.size());
    }

    @Test
    public void test_getAllFiles() throws IOException, URISyntaxException {
        Map<String, ByteBuffer> allFiles = pkPassTemplateFolder.getAllFiles();
        Assert.assertNotNull(allFiles);
        Assert.assertEquals(allFiles.size(), 8);

        File templateFolder = new File(PASS_TEMPLATE_FOLDER);
        for (Entry<String, ByteBuffer> entry : allFiles.entrySet()) {
            File file = new File(templateFolder, entry.getKey());
            Assert.assertTrue(file.exists());
            Assert.assertTrue(entry.getValue().remaining() > 0);
        }
    }

}
