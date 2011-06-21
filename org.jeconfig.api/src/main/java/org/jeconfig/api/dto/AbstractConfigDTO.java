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
 * Abstract base class for all configuration DTOs.
 */
public abstract class AbstractConfigDTO implements ConfigDTO {
	private static final long serialVersionUID = 1L;

	private ScopePath definingScopePath;
	private String propertyType;
	private String propertyName;
	private boolean polymorph;
	private String parentScopeName;
	private long parentVersion;
	private long version;

	@Override
	public void setDefiningScopePath(final ScopePath definingScopePath) {
		this.definingScopePath = definingScopePath;
	}

	@Override
	public void setPropertyType(final String propertyType) {
		this.propertyType = propertyType;
	}

	@Override
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public void setPolymorph(final boolean polymorph) {
		this.polymorph = polymorph;
	}

	@Override
	public void setParentScopeName(final String parentScopeName) {
		this.parentScopeName = parentScopeName;
	}

	@Override
	public void setParentVersion(final long parentVersion) {
		this.parentVersion = parentVersion;
	}

	@Override
	public void setVersion(final long version) {
		this.version = version;
	}

	@Override
	public String getParentScopeName() {
		return parentScopeName;
	}

	@Override
	public long getParentVersion() {
		return parentVersion;
	}

	@Override
	public long getVersion() {
		return version;
	}

	@Override
	public String getPropertyType() {
		return propertyType;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public boolean isPolymorph() {
		return polymorph;
	}

	@Override
	public ScopePath getDefiningScopePath() {
		return definingScopePath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentScopeName == null) ? 0 : parentScopeName.hashCode());
		result = prime * result + (int) (parentVersion ^ (parentVersion >>> 32));
		result = prime * result + (polymorph ? 1231 : 1237);
		result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result + ((propertyType == null) ? 0 : propertyType.hashCode());
		result = prime * result + (int) (version ^ (version >>> 32));
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
		if (!(obj instanceof AbstractConfigDTO)) {
			return false;
		}
		final AbstractConfigDTO other = (AbstractConfigDTO) obj;
		if (parentScopeName == null) {
			if (other.parentScopeName != null) {
				return false;
			}
		} else if (!parentScopeName.equals(other.parentScopeName)) {
			return false;
		}
		if (parentVersion != other.parentVersion) {
			return false;
		}
		if (polymorph != other.polymorph) {
			return false;
		}
		if (propertyName == null) {
			if (other.propertyName != null) {
				return false;
			}
		} else if (!propertyName.equals(other.propertyName)) {
			return false;
		}
		if (propertyType == null) {
			if (other.propertyType != null) {
				return false;
			}
		} else if (!propertyType.equals(other.propertyType)) {
			return false;
		}
		if (version != other.version) {
			return false;
		}
		return true;
	}

}
