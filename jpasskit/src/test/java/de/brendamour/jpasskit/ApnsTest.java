package de.brendamour.jpasskit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ApnsTest {

    @Test
    public void execute() throws FileNotFoundException {
        InputStream certificateStream = getStream("/Users/patrice/Documents/bitzeche/Projects/passkit/Certificates.p12");
        Assert.assertNotNull(certificateStream);
    }

    @Test
    public void execute2() throws FileNotFoundException {
        String pathToP12 = "passbook/Certificates.p12";
        InputStream certificateStream = getStream(pathToP12);
        Assert.assertNotNull(certificateStream);
    }

    @Test
    public void execute_NE() throws FileNotFoundException {
        InputStream certificateStream = getStream("/Users/patrice/Documents/bitzeche/Projects/passkit/wrong.p12");
        Assert.assertNotNull(certificateStream);
    }

    @Test
    public void execute2_NE() throws FileNotFoundException {
        String pathToP12 = "passbook/wrong.p12";
        InputStream certificateStream = getStream(pathToP12);
        Assert.assertNotNull(certificateStream);
    }

    private InputStream getStream(String pathToP12) throws FileNotFoundException {
        File p12File = new File(pathToP12);
        if (!p12File.exists()) {
            // try loading it from the classpath
            URL localP12File = this.getClass().getClassLoader().getResource(pathToP12);
            if (localP12File == null) {
                throw new FileNotFoundException("File at " + pathToP12 + " not found");
            }
            p12File = new File(localP12File.getFile());
        }
        return new FileInputStream(p12File);
    }

}
