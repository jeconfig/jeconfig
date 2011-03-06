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

package org.jeconfig.common.scope;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jeconfig.api.scope.IScope;

/**
 * internal API do not use!!
 */
public final class ScopeImpl implements IScope {
	private static final long serialVersionUID = 1L;

	private final String scopeName;
	private final Map<String, String> properties;

	/**
	 * Creates a new Scope internal API do not use!!
	 * 
	 * @param scopeName
	 * @param properties
	 */
	public ScopeImpl(final String scopeName, final Map<String, String> properties) {
		this.scopeName = scopeName;
		this.properties = new HashMap<String, String>(properties);
	}

	@Override
	public String getProperty(final String key) {
		return properties.get(key);
	}

	@Override
	public Map<String, String> getProperties() {
		return new HashMap<String, String>(properties);
	}

	@Override
	public String getName() {
		return scopeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((scopeName == null) ? 0 : scopeName.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ScopeImpl other = (ScopeImpl) obj;
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!properties.equals(other.properties)) {
			return false;
		}
		if (scopeName == null) {
			if (other.scopeName != null) {
				return false;
			}
		} else if (!scopeName.equals(other.scopeName)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("Scope: '").append(scopeName).append("' ").append(properties);

		return sb.toString();
	}

	@Override
	public boolean containsAllProperties(final Map<String, String> otherProperties) {
		for (final Entry<String, String> entry : otherProperties.entrySet()) {
			if (!this.properties.containsKey(entry.getKey())) {
				return false;
			}
			if (!this.getProperty(entry.getKey()).equals(entry.getValue())) {
				return false;
			}
		}
		return true;
	}
}
