/**
 * Copyright (C) 2018 Patrice Brend'amour <patrice@brendamour.net>
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

public class PKPassTemplateInMemoryTest {

    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    private static final String PASS_TEMPLATE_FOLDER = PKPassTemplateFolderTest.class.getClassLoader().getResource("StoreCard.raw").getPath();

    private PKPassTemplateInMemory pkPassTemplateInMemory;

    @BeforeMethod
    public void prepare() {
        pkPassTemplateInMemory = new PKPassTemplateInMemory();
    }

    @Test
    public void addFile_asStream() throws IOException {
        byte[] expectedArray = "Hello".getBytes();
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_BACKGROUND, new ByteArrayInputStream(expectedArray));
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get(PKPassTemplateInMemory.PK_BACKGROUND), expectedArray);
    }

    @Test
    public void addFile_asStream_withLocale() throws IOException {
        byte[] expectedArray = "Hello".getBytes();
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_BACKGROUND, Locale.ENGLISH, new ByteArrayInputStream(expectedArray));
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get("en.lproj" + SEPARATOR + PKPassTemplateInMemory.PK_BACKGROUND), expectedArray);
    }

    @Test
    public void addFile_asFile() throws IOException {
        URL iconFileURL = PKPassTemplateInMemoryTest.class.getClassLoader().getResource("StoreCard.raw/icon@2x.png");
        File iconFile = new File(iconFileURL.getFile());
        byte[] expectedArray = IOUtils.toByteArray(iconFileURL);
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, iconFile);
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get(PKPassTemplateInMemory.PK_ICON_RETINA), expectedArray);
    }

    @Test
    public void addFile_asFile_withLocale() throws IOException {
        URL iconFileURL = PKPassTemplateInMemoryTest.class.getClassLoader().getResource("StoreCard.raw/icon@2x.png");
        File iconFile = new File(iconFileURL.getFile());
        byte[] expectedArray = IOUtils.toByteArray(iconFileURL);
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, iconFile);
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get("en.lproj" + SEPARATOR + PKPassTemplateInMemory.PK_ICON_RETINA), expectedArray);
    }

    @Test
    public void addFile_asString() throws IOException {
        StringBuffer stringBuffer = new StringBuffer("Hi");
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, stringBuffer);
        byte[] expectedArray = stringBuffer.toString().getBytes();
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get(PKPassTemplateInMemory.PK_ICON_RETINA), expectedArray);
    }

    @Test
    public void addFile_asString_withLocale() throws IOException {
        StringBuffer stringBuffer = new StringBuffer("Hi");
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, stringBuffer);
        byte[] expectedArray = stringBuffer.toString().getBytes();
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get("en.lproj" + SEPARATOR + PKPassTemplateInMemory.PK_ICON_RETINA), expectedArray);
    }

    @Test
    public void addFile_fromURL() throws IOException {
        URL url = new URL("https://upload.wikimedia.org/wikipedia/commons/2/22/Big.Buck.Bunny.-.Bunny.Portrait.png");
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, url);
        byte[] expectedArray = IOUtils.toByteArray(url);
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get(PKPassTemplateInMemory.PK_ICON_RETINA), expectedArray);
    }

    @Test
    public void addFile_fromURL_withLocale() throws IOException {
        URL url = new URL("https://upload.wikimedia.org/wikipedia/commons/2/22/Big.Buck.Bunny.-.Bunny.Portrait.png");
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, url);
        byte[] expectedArray = IOUtils.toByteArray(url);
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(files.get("en.lproj" + SEPARATOR + PKPassTemplateInMemory.PK_ICON_RETINA), expectedArray);
    }

    @Test
    public void addAllFiles() throws IOException {

        pkPassTemplateInMemory.addAllFiles(PASS_TEMPLATE_FOLDER);
        Map<String, byte[]> files = pkPassTemplateInMemory.getFiles();
        Assert.assertEquals(files.size(), 8);
    }

    @Test
    public void provisionPass() throws IOException {

        prepareTemplate();

        File tempPassDir = Files.createTempDir();
        pkPassTemplateInMemory.provisionPassAtDirectory(tempPassDir);

        Collection<File> createdFiles = FileUtils.listFiles(tempPassDir, new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        Assert.assertEquals(createdFiles.size(), 2);
    }

    @Test
    public void test_getAllFiles() throws IOException, URISyntaxException {
        prepareTemplate();

        Map<String, ByteBuffer> allFiles = pkPassTemplateInMemory.getAllFiles();
        Assert.assertNotNull(allFiles);
        Assert.assertEquals(allFiles.size(), 2);

        for (Entry<String, ByteBuffer> entry : allFiles.entrySet()) {
            Assert.assertTrue(entry.getValue().remaining() > 0);
        }
    }

    @Test
    public void test_getAllFilesShouldReturnTheExpectedResultAtSecondUse() throws IOException, URISyntaxException {
        prepareTemplate();
        pkPassTemplateInMemory.getAllFiles();
        Map<String, ByteBuffer> allFiles = pkPassTemplateInMemory.getAllFiles();
        Assert.assertNotNull(allFiles);
        Assert.assertEquals(allFiles.size(), 2);

        for (Entry<String, ByteBuffer> entry : allFiles.entrySet()) {
            Assert.assertTrue(entry.getValue().remaining() > 0);
        }
    }

    private void prepareTemplate() throws IOException {
        // icon
        URL iconFileURL = PKPassTemplateInMemoryTest.class.getClassLoader().getResource("StoreCard.raw/icon@2x.png");
        File iconFile = new File(iconFileURL.getFile());
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, iconFile);

        // icon for language
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, iconFile);
    }

}
