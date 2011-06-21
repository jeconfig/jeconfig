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

import java.io.Serializable;

import org.jeconfig.api.scope.ScopePath;

/**
 * Super-interface of all configuration-DTOs.<br>
 * Configurations are serialized into DTOs before they are sent to the persistence service to get persisted.<br>
 * When configurations are loaded, the DTOs are requested from the persistence service and de-serialized to object form.<br>
 * If migration between configuration versions is needed, the migration is done on the DTOs. Merging is also performed
 * on the DTOs before the configuration object is created.
 */
public interface ConfigDTO extends Serializable {

	/**
	 * Returns the scope path of the configuration this DTO was created from.
	 * 
	 * @return the scope path of the DTO
	 */
	ScopePath getDefiningScopePath();

	/**
	 * Changes the scope path of the DTO.<br>
	 * <br>
	 * <b>Must not be invoked by clients!</b>
	 * 
	 * @param definingScopePath
	 */
	void setDefiningScopePath(final ScopePath definingScopePath);

	/**
	 * Returns the version of the DTO. The version is incremented on save when its value or
	 * one of its children's values have changed.
	 * 
	 * @return the version of the DTO
	 */
	long getVersion();

	/**
	 * Changes the version of the DTO.<br>
	 * <br>
	 * <b>Must not be invoked by clients!</b>
	 * 
	 * @param version
	 */
	void setVersion(long version);

	/**
	 * Returns the name of the property this DTO was created for.<br>
	 * The root DTO and DTOs which are stored in collections have no property name.
	 * 
	 * @return the property name; may be <code>null</code>
	 */
	String getPropertyName();

	/**
	 * Changes the property name of the DTO.
	 * 
	 * @param propertyName
	 */
	void setPropertyName(final String propertyName);

	/**
	 * Returns the type of the property this DTO was created for.<br>
	 * 
	 * @return the property type
	 */
	String getPropertyType();

	/**
	 * Changes the property type of the DTO.
	 * 
	 * @param propertyType
	 */
	void setPropertyType(final String propertyType);

	/**
	 * Indicates whether the configuration object is polymorph.
	 * 
	 * @return <code>true</code> if polymorph
	 */
	boolean isPolymorph();

	/**
	 * Changes the polymorph property of the DTO.
	 * 
	 * @param polymorph
	 */
	void setPolymorph(final boolean polymorph);

	/**
	 * Returns the scope name of the DTO which was the next in the DTO-chain when this DTO was saved at last.<br>
	 * This property is used while merging to detect staleness of the DTO. If this parent scope name differs
	 * from the real parent scope name the DTO is stale.
	 * 
	 * @return the last scope name of the parent DTO
	 */
	String getParentScopeName();

	/**
	 * Changes the parent scope name.<br>
	 * <br>
	 * <b>Must not be invoked by clients!</b>
	 * 
	 * @param parentScopeName
	 */
	void setParentScopeName(String parentScopeName);

	/**
	 * Returns the version of the DTO which was the next in the DTO-chain when this DTO was saved at last.<br>
	 * This property is used while merging to detect staleness of the DTO. If this parent version differs
	 * from the real parent version the DTO is stale.
	 * 
	 * @return the parent version
	 */
	long getParentVersion();

	/**
	 * Changes the parent version.<br>
	 * <br>
	 * <b>Must not be invoked by clients!</b>
	 * 
	 * @param parentVersion
	 */
	void setParentVersion(long parentVersion);

	/**
	 * Creates a deep copy of this DTO.
	 * 
	 * @return a new configuration DTO which is a copy of this DTO and all of its children
	 */
	ConfigDTO deepCopy();

	/**
	 * Creates a deep copy of this DTO and changes the scope path of the DTO and all of its children.
	 * 
	 * @param scopePath
	 * @return a new configuration DTO which is a copy of this DTO and all of its children with changed scope path
	 */
	ConfigDTO deepCopyToScopePath(ScopePath scopePath);

	/**
	 * Visits this DTO the whole subtree of it recursively.
	 * 
	 * @param visitor
	 */
	void visit(ConfigDtoVisitor visitor);
}
