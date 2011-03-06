/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the jeconfig nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jeconfig.client.annotation.map;

import java.util.HashMap;
import java.util.Map;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class ComplexTypeMapPropertyTestConfiguration {

	private Map<String, MapPropertyTestConfiguration> filterConfigMap = new HashMap<String, MapPropertyTestConfiguration>();
	private Map<Integer, SimpleTypeMapPropertyTestConfiguration> userIDConfigMap = new HashMap<Integer, SimpleTypeMapPropertyTestConfiguration>();

	@ConfigMapProperty(keyType = String.class, valueType = MapPropertyTestConfiguration.class)
	public Map<String, MapPropertyTestConfiguration> getFilterConfigMap() {
		return filterConfigMap;
	}

	public void setFilterConfigMap(final Map<String, MapPropertyTestConfiguration> filterConfigMap) {
		this.filterConfigMap = filterConfigMap;
	}

	@ConfigMapProperty(keyType = Integer.class, valueType = SimpleTypeMapPropertyTestConfiguration.class)
	public Map<Integer, SimpleTypeMapPropertyTestConfiguration> getUserIDConfigMap() {
		return userIDConfigMap;
	}

	public void setUserIDConfigMap(final Map<Integer, SimpleTypeMapPropertyTestConfiguration> userIDConfigMap) {
		this.userIDConfigMap = userIDConfigMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterConfigMap == null) ? 0 : filterConfigMap.hashCode());
		result = prime * result + ((userIDConfigMap == null) ? 0 : userIDConfigMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ComplexTypeMapPropertyTestConfiguration)) {
			return false;
		}
		final ComplexTypeMapPropertyTestConfiguration other = (ComplexTypeMapPropertyTestConfiguration) obj;
		if (filterConfigMap == null) {
			if (other.filterConfigMap != null) {
				return false;
			}
		} else if (!filterConfigMap.equals(other.filterConfigMap)) {
			return false;
		}
		if (userIDConfigMap == null) {
			if (other.userIDConfigMap != null) {
				return false;
			}
		} else if (!userIDConfigMap.equals(other.userIDConfigMap)) {
			return false;
		}
		return true;
	}

}
