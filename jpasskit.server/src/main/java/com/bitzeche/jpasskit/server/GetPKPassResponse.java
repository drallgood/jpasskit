package com.bitzeche.jpasskit.server;

import java.util.Date;

import com.bitzeche.jpasskit.PKPass;

public class GetPKPassResponse {

	private final PKPass pass;
	private final Date lastUpdated;

	public GetPKPassResponse(final PKPass pass, final Date lastUpdated) {
		this.pass = pass;
		this.lastUpdated = lastUpdated;
	}

	public PKPass getPass() {
		return pass;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

}
