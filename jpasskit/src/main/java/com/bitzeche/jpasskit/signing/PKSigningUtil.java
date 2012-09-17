package com.bitzeche.jpasskit.signing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.bitzeche.jpasskit.PKPass;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public final class PKSigningUtil {
    
    private PKSigningUtil() {
    }

    public static byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final String pathToTemplateDirectory) throws IOException {
        File tempPassDir = Files.createTempDir();
        FileUtils.copyDirectory(new File(pathToTemplateDirectory), tempPassDir);
        File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + "pass.json");

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        jsonObjectMapper.writeValue(passJSONFile, pass);

        Map<String, String> fileWithHashMap = new HashMap<String, String>();

        HashFunction hashFunction = Hashing.sha1();
        File[] filesInTempDir = tempPassDir.listFiles();
        hashFilesInDirectory(filesInTempDir, fileWithHashMap, tempPassDir.getAbsolutePath() + File.separator, hashFunction);
        File manifestJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + "manifest.json");
        jsonObjectMapper.writeValue(manifestJSONFile, fileWithHashMap);

        signManifestFile(manifestJSONFile);

        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass);
        zip(tempPassDir, tempPassDir, zipOutputStream);
        zipOutputStream.close();

        FileUtils.deleteDirectory(tempPassDir);
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }

    private static void signManifestFile(final File manifestJSONFile) {
        // TODO Auto-generated method stub

    }

    private static void hashFilesInDirectory(final File[] files, final Map<String, String> fileWithHashMap, final String workingDirectory,
            final HashFunction hashFunction) throws IOException {
        for (File passResourceFile : files) {
            if (passResourceFile.isFile()) {
                HashCode hash = Files.hash(passResourceFile, hashFunction);
                String name = passResourceFile.getAbsolutePath().replace(workingDirectory, "");
                fileWithHashMap.put(name, Hex.encodeHexString(hash.asBytes()));
            } else if (passResourceFile.isDirectory()) {
                hashFilesInDirectory(passResourceFile.listFiles(), fileWithHashMap, workingDirectory, hashFunction);
            }
        }
    }

    private static final void zip(final File directory, final File base, final ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zipOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
                zipOutputStream.putNextEntry(entry);
                while (-1 != (read = fileInputStream.read(buffer))) {
                    zipOutputStream.write(buffer, 0, read);
                }
                fileInputStream.close();
            }
        }
    }

}
