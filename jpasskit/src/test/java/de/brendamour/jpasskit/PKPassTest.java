package de.brendamour.jpasskit;

import static org.mockito.Mockito.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.passes.PKGenericPass;

public class PKPassTest {
    private static final String COLOR_STRING = "rgb(1,2,3)";
    private static final Color COLOR_OBJECT = new Color(1, 2, 3);
    private PKPass pkPass;

    @BeforeMethod
    public void prepareTest() {
        pkPass = new PKPass();
    }

    @Test
    public void test_colorConversionFromString() {

        pkPass.setBackgroundColor(COLOR_STRING);

        Assert.assertEquals(pkPass.getBackgroundColor(), COLOR_STRING);
        Assert.assertEquals(pkPass.getBackgroundColorAsObject(), COLOR_OBJECT);

    }

    @Test
    public void test_colorConversionFromObject() {

        pkPass.setBackgroundColorAsObject(COLOR_OBJECT);

        Assert.assertEquals(pkPass.getBackgroundColor(), COLOR_STRING);
        Assert.assertEquals(pkPass.getBackgroundColorAsObject(), COLOR_OBJECT);

    }

    @Test
    public void test_includesPassErrors() {
        PKGenericPass subPass = mock(PKGenericPass.class);
        List<String> subArrayListWithErrors = new ArrayList<String>();
        String someValidationMessage = "Some error";
        subArrayListWithErrors.add(someValidationMessage);

        pkPass.setGeneric(subPass);

        when(subPass.isValid()).thenReturn(false);
        when(subPass.getValidationErrors()).thenReturn(subArrayListWithErrors);

        List<String> validationErrors = pkPass.getValidationErrors();

        Assert.assertTrue(validationErrors.size() > 0);
        Assert.assertTrue(validationErrors.contains(someValidationMessage));
        

    }
}
