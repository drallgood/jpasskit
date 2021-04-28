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

import java.util.Map;

public class PKPersonalizePassPayload {

	private String personalizationToken;
	
	private Map<String, String> requiredPersonalizationInfo;

	public String getPersonalizationToken() {
		return personalizationToken;
	}

	public void setPersonalizationToken(String personalizationToken) {
		this.personalizationToken = personalizationToken;
	}

	public Map<String, String> getRequiredPersonalizationInfo() {
		return requiredPersonalizationInfo;
	}

	public void setRequiredPersonalizationInfo(Map<String, String> requiredPersonalizationInfo) {
		this.requiredPersonalizationInfo = requiredPersonalizationInfo;
	}
}
