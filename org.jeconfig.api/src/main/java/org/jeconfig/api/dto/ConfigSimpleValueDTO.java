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

import org.jeconfig.api.scope.ScopePath;

/**
 * Holds all information about a simple configuration object.
 */
public final class ConfigSimpleValueDTO extends AbstractConfigDTO {
	private static final long serialVersionUID = 1L;

	private String value;

	/**
	 * Creates a new simple value DTO.
	 */
	public ConfigSimpleValueDTO() {}

	/**
	 * Creates a new simple value DTO with the given properties.
	 * 
	 * @param type
	 * @param propName
	 * @param scopePath
	 * @param value
	 */
	public ConfigSimpleValueDTO(final String type, final String propName, final ScopePath scopePath, final String value) {

		setPropertyName(propName);
		setPropertyType(type);
		setPolymorph(false);
		setDefiningScopePath(scopePath);
		this.value = value;
	}

	/**
	 * Changes the value of the simple property.
	 * 
	 * @param value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Creates a flat copy of this DTO.
	 * 
	 * @return a flat copy
	 */
	public ConfigSimpleValueDTO flatCopy() {
		final ConfigSimpleValueDTO config = new ConfigSimpleValueDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(getVersion());
		config.setParentVersion(getParentVersion());
		config.setParentScopeName(getParentScopeName());
		config.setValue(getValue());
		return config;
	}

	/**
	 * Creates a flat copy of this DTO with the given value.
	 * 
	 * @param value
	 * @return a flat copy
	 */
	public ConfigSimpleValueDTO flatCopy(final String value) {
		final ConfigSimpleValueDTO config = new ConfigSimpleValueDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(getVersion());
		config.setParentVersion(getParentVersion());
		config.setParentScopeName(getParentScopeName());
		config.setValue(value);
		return config;
	}

	/**
	 * Creates a flat copy of the DTO.
	 * 
	 * @param version
	 * @param parentScopeName
	 * @param parentVersion
	 * @return a flat copy
	 */
	public ConfigSimpleValueDTO flatCopy(final long version, final String parentScopeName, final long parentVersion) {
		final ConfigSimpleValueDTO config = new ConfigSimpleValueDTO();
		config.setPropertyType(getPropertyType());
		config.setPropertyName(getPropertyName());
		config.setDefiningScopePath(getDefiningScopePath());
		config.setPolymorph(isPolymorph());
		config.setVersion(version);
		config.setParentVersion(parentVersion);
		config.setParentScopeName(parentScopeName);
		config.setValue(getValue());
		return config;
	}

	@Override
	public ConfigSimpleValueDTO deepCopy() {
		return deepCopyToScopePath(getDefiningScopePath());
	}

	@Override
	public ConfigSimpleValueDTO deepCopyToScopePath(final ScopePath scopePath) {
		final ConfigSimpleValueDTO result = flatCopy();
		result.setDefiningScopePath(scopePath);
		return result;
	}

	/**
	 * Returns the value of this DTO.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public void visit(final ConfigDtoVisitor visitor) {
		visitor.visitSimpleDto(this);
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "ConfigSimpleValueDTO [value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		final ConfigSimpleValueDTO other = (ConfigSimpleValueDTO) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
