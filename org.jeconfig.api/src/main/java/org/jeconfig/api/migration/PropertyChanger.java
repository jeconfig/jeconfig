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

package org.jeconfig.api.migration;

import org.jeconfig.api.dto.ComplexConfigDTO;

/**
 * Convenience class to change configuration DTO trees.
 */
public interface PropertyChanger {

	/**
	 * Renames the specified property of the given DTO if exists.
	 * 
	 * @param parentDto
	 * @param propertyName
	 * @param newPropertyName
	 */
	void renameProperty(ComplexConfigDTO parentDto, String propertyName, String newPropertyName);

	/**
	 * Renames all properties in the whole DTO tree which are hold by parents with the given type and are
	 * named as specified.
	 * 
	 * @param parentTypeName
	 * @param propertyName
	 * @param newPropertyName
	 */
	void renamePropertyRecursively(String parentTypeName, String propertyName, String newPropertyName);

	/**
	 * Deletes the specified property of the given DTO if exists.
	 * 
	 * @param parentDto
	 * @param propertyName
	 */
	void deleteProperty(ComplexConfigDTO parentDto, String propertyName);

	/**
	 * Deletes all properties in the whole DTO tree which are hold by parents with the given type and are
	 * named as specified.
	 * 
	 * @param parentTypeName
	 * @param propertyName
	 */
	void deletePropertyRecursively(String parentTypeName, String propertyName);

	/**
	 * Adds a new simple property as specified to the given DTO if not <code>null</code>.
	 * 
	 * @param parentDto
	 * @param propertyName
	 * @param value
	 * @param simpleValueType
	 */
	void addSimpleProperty(ComplexConfigDTO parentDto, String propertyName, String value, String simpleValueType);

	/**
	 * Adds new simple properties as specified to all complex types with the given type in the whole DTO tree.
	 * 
	 * @param parentTypeName
	 * @param propertyName
	 * @param value
	 * @param simpleValueType
	 */
	void addSimplePropertyRecursively(String parentTypeName, String propertyName, String value, String simpleValueType);

	/**
	 * Changes the value of the specified simple property of the given DTO if exists.
	 * 
	 * @param parentDto
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	void changeSimpleValue(ComplexConfigDTO parentDto, String propertyName, String oldValue, String newValue);

	/**
	 * Changes the values of all specified simple properties in the whole DTO tree.
	 * 
	 * @param parentTypeName
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	void changeSimpleValueRecursively(String parentTypeName, String propertyName, String oldValue, String newValue);
}
