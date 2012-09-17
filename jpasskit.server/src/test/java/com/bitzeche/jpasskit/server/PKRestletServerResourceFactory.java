package com.bitzeche.jpasskit.server;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.bitzeche.jpasskit.PKBarcode;
import com.bitzeche.jpasskit.PKBarcodeFormat;
import com.bitzeche.jpasskit.PKPass;
import com.bitzeche.jpasskit.PKSerialNumbersOfPassesForDeviceResponse;
import com.bitzeche.jpasskit.server.IPKRestletServerResourceFactory;
import com.bitzeche.jpasskit.server.PKAuthTokenNotValidException;
import com.bitzeche.jpasskit.server.PKDeviceResource;
import com.bitzeche.jpasskit.server.PKLogResource;
import com.bitzeche.jpasskit.server.PKPassResource;

public class PKRestletServerResourceFactory implements IPKRestletServerResourceFactory {

    public PKDeviceResource getPKDeviceResource() {
        return new PKDeviceResource() {

            @Override
            protected Status handleRegisterDeviceRequest(final String deviceLibraryIdentifier, final String passTypeIdentifier,
                    final String serialNumber, final String authString) throws PKAuthTokenNotValidException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected Status handleUnregisterDeviceRequest(final String deviceLibraryIdentifier, final String passTypeIdentifier,
                    final String serialNumber, final ChallengeResponse authString) throws PKAuthTokenNotValidException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected PKSerialNumbersOfPassesForDeviceResponse getSerialNumberOfPassesForDevice(final String deviceLibraryIdentifier,
                    final String passTypeIdentifier, final String passesUpdatedSince) {
                PKSerialNumbersOfPassesForDeviceResponse serialNumbersOfPassesForDeviceResponse = new PKSerialNumbersOfPassesForDeviceResponse();
                serialNumbersOfPassesForDeviceResponse.setLastUpdate("" + System.currentTimeMillis());
                String[] serialNumbers = new String[] { "p69f2J" };
                serialNumbersOfPassesForDeviceResponse.setSerialNumbers(serialNumbers);
                return serialNumbersOfPassesForDeviceResponse;
            }

        };
    }

    public PKPassResource getPKPassResource() {
        return new PKPassResource() {

            @Override
            protected PKPass handleGetLatestVersionOfPass(final String passTypeIdentifier, final String serialNumber,
                    final String authString) throws PKAuthTokenNotValidException {

                PKPass pass = new PKPass();
                pass.setLogoText("logo");
                PKBarcode barcode = new PKBarcode();
                barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
                barcode.setMessage("ABCDEFGH");
                barcode.setMessageEncoding("UTF-8");

                pass.setBarcode(barcode);
                return pass;
            }

        };
    }

    public PKLogResource getPKLogResource() {
        return new PKLogResource() {

            @Override
            public Representation handleLogRequest(final Representation entity) {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }

}
