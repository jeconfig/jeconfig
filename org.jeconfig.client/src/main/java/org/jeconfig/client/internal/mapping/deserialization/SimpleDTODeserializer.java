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

package org.jeconfig.client.internal.mapping.deserialization;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;

import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.common.reflection.PropertyAccessor;

public class SimpleDTODeserializer extends AbstractDTODeserializer {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final SimpleTypeConverterRegistry simpleTypeConverterRegistry;

	public SimpleDTODeserializer(final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		this.simpleTypeConverterRegistry = simpleTypeConverterRegistry;
	}

	public void handleSimpleProperty(
		final ComplexConfigDTO configDTO,
		final Object config,
		final PropertyDescriptor propDesc,
		final Annotation annotation,
		final String propName) {
		final ConfigSimpleProperty anno = (ConfigSimpleProperty) annotation;
		final SimpleTypeConverter<?> customConverter = createCustomConverter(anno.customConverter());
		final ConfigSimpleValueDTO simpleValueProperty = configDTO.getSimpleValueProperty(propName);
		Object value = null;
		if (simpleValueProperty != null) {
			value = createSimpleValue(simpleValueProperty, propDesc.getPropertyType(), customConverter);
		}
		propertyAccessor.write(config, propName, value);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object createSimpleValue(
		final ConfigSimpleValueDTO configDTO,
		final Class propertyType,
		final SimpleTypeConverter<?> customConverter) {
		if (configDTO.getValue() != null) {
			if (customConverter != null) {
				return customConverter.convertToObject(propertyType, configDTO.getValue());
			} else {
				return simpleTypeConverterRegistry.convertToObject(propertyType, configDTO.getValue());
			}
		}
		return null;
	}
}
