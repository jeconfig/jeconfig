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

import java.util.HashSet;
import java.util.Set;

import org.jeconfig.api.scope.ScopePath;

/**
 * Holds all information about a set configuration object.
 */
public final class ConfigSetDTO extends AbstractConfigDTO {
	private static final long serialVersionUID = 1L;

	private Set<ConfigDTO> items = null;

	/**
	 * Creates a new set DTO.
	 */
	public ConfigSetDTO() {}

	/**
	 * Creates a new set DTO with the given properties.
	 * 
	 * @param propType
	 * @param propName
	 * @param polymorph
	 * @param scopePath
	 */
	public ConfigSetDTO(final String propType, final String propName, final boolean polymorph, final ScopePath scopePath) {
		setPropertyType(propType);
		setPropertyName(propName);
		setPolymorph(polymorph);
		setDefiningScopePath(scopePath);
	}

	/**
	 * Returns the items of the set. May be simple or complex DTOs or <code>null</code>.
	 * 
	 * @return the items; may be <code>null</code>
	 */
	public Set<ConfigDTO> getItems() {
		if (items != null) {
			return new HashSet<ConfigDTO>(items);
		}
		return null;
	}

	/**
	 * Sets the items.
	 * 
	 * @param items
	 */
	public void setItems(final Set<ConfigDTO> items) {
		this.items = items;
	}

	/**
	 * Creates a new flat copy without items.
	 * 
	 * @return a new flat copy
	 */
	public ConfigSetDTO flatCopy() {
		final ConfigSetDTO config = new ConfigSetDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(getVersion());
		config.setParentVersion(getParentVersion());
		config.setParentScopeName(getParentScopeName());
		config.setItems(items == null ? null : new HashSet<ConfigDTO>());
		return config;
	}

	/**
	 * Creates a new flat copy with the given items.
	 * 
	 * @param items
	 * @return a new flat copy
	 */
	public ConfigSetDTO flatCopy(final Set<ConfigDTO> items) {
		final ConfigSetDTO config = new ConfigSetDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(getVersion());
		config.setParentVersion(getParentVersion());
		config.setParentScopeName(getParentScopeName());
		config.setItems(items);
		return config;
	}

	/**
	 * Creates a new flat copy without items.
	 * 
	 * @param version
	 * @param parentScopeName
	 * @param parentVersion
	 * @return a new flat copy
	 */
	public ConfigSetDTO flatCopy(final long version, final String parentScopeName, final long parentVersion) {
		final ConfigSetDTO config = new ConfigSetDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(version);
		config.setParentVersion(parentVersion);
		config.setParentScopeName(parentScopeName);
		config.setItems(items == null ? null : new HashSet<ConfigDTO>());
		return config;
	}

	@Override
	public ConfigSetDTO deepCopy() {
		return deepCopyToScopePath(getDefiningScopePath());
	}

	@Override
	public ConfigSetDTO deepCopyToScopePath(final ScopePath scopePath) {
		final ConfigSetDTO result = flatCopy();
		result.setDefiningScopePath(scopePath);

		if (items != null) {
			for (final ConfigDTO item : items) {
				result.items.add(item.deepCopyToScopePath(scopePath));
			}
		}

		return result;
	}

	/**
	 * Adds a single item.
	 * 
	 * @param item
	 */
	public void addItem(final ConfigDTO item) {
		if (items == null) {
			items = new HashSet<ConfigDTO>();
		}
		items.add(item);
	}

	@Override
	public void visit(final ConfigDtoVisitor visitor) {
		visitor.visitSetDto(this);
		for (final ConfigDTO item : items) {
			item.visit(visitor);
		}
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "ConfigSetDTO [items=" + items + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		final ConfigSetDTO other = (ConfigSetDTO) obj;
		if (items == null) {
			if (other.items != null) {
				return false;
			}
		} else if (!items.equals(other.items)) {
			return false;
		}
		return true;
	}

}
