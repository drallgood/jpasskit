package com.bitzeche.jpasskit.server;

public interface IPKRestletServerResourceFactory {

    public PKDeviceResource getPKDeviceResource();

    public PKPassResource getPKPassResource();

    public PKLogResource getPKLogResource();
}
