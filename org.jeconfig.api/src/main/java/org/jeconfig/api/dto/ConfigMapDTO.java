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

package org.jeconfig.api.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jeconfig.api.scope.IScopePath;

/**
 * Holds all information about a map configuration object.
 */
public final class ConfigMapDTO extends AbstractConfigDTO {
	private static final long serialVersionUID = 1L;
	private Map<String, IConfigDTO> map;

	/**
	 * Returns the map which holds simple or coplex DTOs representing the original map of the configuration object.
	 * 
	 * @return the map; may be <code>null</code>
	 */
	public Map<String, IConfigDTO> getMap() {
		if (map != null) {
			return new HashMap<String, IConfigDTO>(map);
		}
		return null;
	}

	/**
	 * Sets the map.
	 * 
	 * @param map
	 */
	public void setMap(final Map<String, IConfigDTO> map) {
		this.map = map;
	}

	/**
	 * Creates a new flat copy without entries.
	 * 
	 * @return a new flat copy
	 */
	public ConfigMapDTO flatCopy() {
		final ConfigMapDTO config = new ConfigMapDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(getVersion());
		config.setParentVersion(getParentVersion());
		config.setParentScopeName(getParentScopeName());
		config.setMap(map == null ? null : new HashMap<String, IConfigDTO>());
		return config;
	}

	/**
	 * Creates a new flat copy with the given entries.
	 * 
	 * @param map
	 * @return a new flat copy
	 */
	public ConfigMapDTO flatCopy(final Map<String, IConfigDTO> map) {
		final ConfigMapDTO config = new ConfigMapDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(getVersion());
		config.setParentVersion(getParentVersion());
		config.setParentScopeName(getParentScopeName());
		config.setMap(map);
		return config;
	}

	/**
	 * Creates a new flat copy without entries.
	 * 
	 * @param version
	 * @param parentScopeName
	 * @param parentVersion
	 * @return a new flat copy
	 */
	public ConfigMapDTO flatCopy(final long version, final String parentScopeName, final long parentVersion) {
		final ConfigMapDTO config = new ConfigMapDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(version);
		config.setParentVersion(parentVersion);
		config.setParentScopeName(parentScopeName);
		config.setMap(map == null ? null : new HashMap<String, IConfigDTO>());
		return config;
	}

	@Override
	public ConfigMapDTO deepCopy() {
		return deepCopyToScopePath(getDefiningScopePath());
	}

	@Override
	public ConfigMapDTO deepCopyToScopePath(final IScopePath scopePath) {
		final ConfigMapDTO result = flatCopy();
		result.setDefiningScopePath(scopePath);

		if (map != null) {
			for (final Entry<String, IConfigDTO> entry : map.entrySet()) {
				result.map.put(entry.getKey(), entry.getValue().deepCopyToScopePath(scopePath));
			}
		}

		return result;
	}

	/**
	 * Adds a single entry to the map.
	 * 
	 * @param key
	 * @param value
	 */
	public void addEntry(final String key, final IConfigDTO value) {
		if (map == null) {
			map = new HashMap<String, IConfigDTO>();
		}
		map.put(key, value);
	}

	@Override
	public void visit(final IConfigDtoVisitor visitor) {
		visitor.visitMapDto(this);
		for (final IConfigDTO valueDto : map.values()) {
			valueDto.visit(visitor);
		}
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "ConfigMapDTO [map=" + map + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ConfigMapDTO other = (ConfigMapDTO) obj;
		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}
		return true;
	}

}
