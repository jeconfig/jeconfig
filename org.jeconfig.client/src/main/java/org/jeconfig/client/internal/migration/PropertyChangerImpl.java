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

package org.jeconfig.client.internal.migration;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigDtoVisitorAdapter;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.api.migration.PropertyChanger;

import com.google.common.base.Objects;

public class PropertyChangerImpl implements PropertyChanger {

	private final ComplexConfigDTO rootDto;

	public PropertyChangerImpl(final ComplexConfigDTO rootDto) {
		this.rootDto = rootDto;
	}

	@Override
	public void renameProperty(final ComplexConfigDTO parentDto, final String propertyName, final String newPropertyName) {
		if (parentDto != null) {
			final ConfigDTO property = parentDto.getProperty(propertyName);
			if (property != null) {
				parentDto.removeProperty(propertyName);
				property.setPropertyName(newPropertyName);
				parentDto.addProperty(property);
			}
		}
	}

	@Override
	public void renamePropertyRecursively(final String parentTypeName, final String propertyName, final String newPropertyName) {
		rootDto.visit(new ConfigDtoVisitorAdapter() {
			@Override
			public void visitComplexDto(final ComplexConfigDTO complexDto) {
				if (complexDto.getPropertyType().equals(parentTypeName)) {
					renameProperty(complexDto, propertyName, newPropertyName);
				}
			}
		});
	}

	@Override
	public void deleteProperty(final ComplexConfigDTO parentDto, final String propertyName) {
		if (parentDto != null) {
			parentDto.removeProperty(propertyName);
		}
	}

	@Override
	public void deletePropertyRecursively(final String parentTypeName, final String propertyName) {
		rootDto.visit(new ConfigDtoVisitorAdapter() {
			@Override
			public void visitComplexDto(final ComplexConfigDTO complexDto) {
				if (complexDto.getPropertyType().equals(parentTypeName)) {
					deleteProperty(complexDto, propertyName);
				}
			}
		});
	}

	@Override
	public void addSimpleProperty(
		final ComplexConfigDTO parentDto,
		final String propertyName,
		final String value,
		final String simpleValueType) {

		if (parentDto != null) {
			final ConfigSimpleValueDTO configSimpleValueDTO = new ConfigSimpleValueDTO(
				simpleValueType,
				propertyName,
				parentDto.getDefiningScopePath(),
				value);
			parentDto.addSimpleValueProperty(configSimpleValueDTO);
		}
	}

	@Override
	public void addSimplePropertyRecursively(
		final String parentTypeName,
		final String propertyName,
		final String value,
		final String simpleValueType) {

		rootDto.visit(new ConfigDtoVisitorAdapter() {
			@Override
			public void visitComplexDto(final ComplexConfigDTO complexDto) {
				if (complexDto.getPropertyType().equals(parentTypeName)) {
					addSimpleProperty(complexDto, propertyName, value, simpleValueType);
				}
			}
		});
	}

	@Override
	public void changeSimpleValue(
		final ComplexConfigDTO parentDto,
		final String propertyName,
		final String oldValue,
		final String newValue) {
		if (parentDto != null) {
			final ConfigSimpleValueDTO simpleValueDTO = parentDto.getSimpleValueProperty(propertyName);
			if (Objects.equal(oldValue, simpleValueDTO.getValue())) {
				simpleValueDTO.setValue(newValue);
			}
		}
	}

	@Override
	public void changeSimpleValueRecursively(
		final String parentTypeName,
		final String propertyName,
		final String oldValue,
		final String newValue) {

		rootDto.visit(new ConfigDtoVisitorAdapter() {
			@Override
			public void visitComplexDto(final ComplexConfigDTO complexDto) {
				if (complexDto.getPropertyType().equals(parentTypeName)) {
					changeSimpleValue(complexDto, propertyName, oldValue, newValue);
				}
			}
		});
	}
}
