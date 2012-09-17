package com.bitzeche.jpasskit.server;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

public final class PKLogResourceFactory extends Finder {

    private IPKRestletServerResourceFactory pkRestletServerResourceFactory;

    public PKLogResourceFactory(final IPKRestletServerResourceFactory pkRestletServerResourceFactory) {
        this.pkRestletServerResourceFactory = pkRestletServerResourceFactory;
    }

    @Override
    public ServerResource create(final Request request, final Response response) {
        return pkRestletServerResourceFactory.getPKLogResource();
    }

}
