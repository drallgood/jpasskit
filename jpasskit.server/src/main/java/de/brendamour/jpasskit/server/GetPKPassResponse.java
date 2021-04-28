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
package de.brendamour.jpasskit.server;

import java.util.Date;

import de.brendamour.jpasskit.PKPass;

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
