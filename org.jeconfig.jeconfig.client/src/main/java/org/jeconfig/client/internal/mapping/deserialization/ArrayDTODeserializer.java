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
import java.lang.reflect.Array;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.conversion.ISimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ArrayDTODeserializer extends AbstractDTODeserializer {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final SimpleDTODeserializer simpleDTODeserializer;
	private final ComplexDTODeserializer complexDTODeserializer;

	public ArrayDTODeserializer(
		final SimpleDTODeserializer simpleDTODeserializer,
		final ComplexDTODeserializer complexDTODeserializer) {
		this.simpleDTODeserializer = simpleDTODeserializer;
		this.complexDTODeserializer = complexDTODeserializer;
	}

	public void handleArrayProperty(
		final ComplexConfigDTO configDTO,
		final Object config,
		final IScopePath scopePath,
		final PropertyDescriptor propDesc,
		final Annotation annotation,
		final String propName) {
		final ConfigArrayProperty arrayAnno = (ConfigArrayProperty) annotation;
		final ISimpleTypeConverter<?> customConverter = createCustomConverter(arrayAnno.customConverter());
		final boolean polymorph = arrayAnno.polymorph();
		final boolean complex = AnnotationUtil.getAnnotation(
				propDesc.getPropertyType().getComponentType(),
				ConfigComplexType.class) != null;
		final ConfigListDTO listDTO = configDTO.getListProperty(propName);

		final Object arrayToSet = processArray(propDesc, polymorph, complex, listDTO, scopePath, customConverter);
		propertyAccessor.write(config, propName, arrayToSet);
	}

	private Object processArray(
		final PropertyDescriptor propDesc,
		final boolean polymorph,
		final boolean complex,
		final ConfigListDTO listDTO,
		final IScopePath scopePath,
		final ISimpleTypeConverter<?> customConverter) {
		Object arrayToSet = null;
		if (listDTO.getItems() != null) {
			final Class<?> arrayItemType = propDesc.getPropertyType().getComponentType();
			arrayToSet = Array.newInstance(arrayItemType, listDTO.getItems().size());
			if (complex || polymorph) {
				//complex items
				for (int i = 0; i < listDTO.getItems().size(); i++) {
					final ComplexConfigDTO complexDTO = (ComplexConfigDTO) listDTO.getItems().get(i);
					final Object cfg = complexDTODeserializer.createComplexConfigObject(complexDTO, null, scopePath);
					Array.set(arrayToSet, i, cfg);
				}
			} else {
				//simple items
				for (int i = 0; i < listDTO.getItems().size(); i++) {
					final ConfigSimpleValueDTO simpleDTO = (ConfigSimpleValueDTO) listDTO.getItems().get(i);
					final Object value = simpleDTODeserializer.createSimpleValue(
							simpleDTO,
							getTypeLoader().getPolymorphType(simpleDTO.getPropertyType()),
							customConverter);
					Array.set(arrayToSet, i, value);
				}
			}
		}
		return arrayToSet;
	}
}
