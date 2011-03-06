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
import java.util.ArrayList;
import java.util.List;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.conversion.ISimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ConfigListDecorator;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ListDTODeserializer extends AbstractDTODeserializer {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final SimpleDTODeserializer simpleDTODeserializer;
	private final ComplexDTODeserializer complexDTODeserializer;

	public ListDTODeserializer(
		final SimpleDTODeserializer simpleDTODeserializer,
		final ComplexDTODeserializer complexDTODeserializer) {
		this.simpleDTODeserializer = simpleDTODeserializer;
		this.complexDTODeserializer = complexDTODeserializer;
	}

	public void handleListProperty(
		final ComplexConfigDTO configDTO,
		final Object config,
		final List<ComplexConfigDTO> dtos,
		final IScopePath scopePath,
		final PropertyDescriptor propDesc,
		final Annotation annotation,
		final String propName) {
		final ConfigListProperty listAnno = (ConfigListProperty) annotation;
		final ISimpleTypeConverter<?> customConverter = createCustomConverter(listAnno.customConverter());
		final boolean polymorph = listAnno.polymorph();
		final boolean complex = AnnotationUtil.getAnnotation(listAnno.itemType(), ConfigComplexType.class) != null;
		final ConfigListDTO listDTO = configDTO.getListProperty(propName);
		final List<Object> listToSet = processList(
				config,
				propDesc,
				polymorph,
				complex,
				listDTO,
				getListPropertyDtos(dtos, propName),
				scopePath,
				customConverter);
		propertyAccessor.write(config, propName, listToSet);
	}

	private List<Object> processList(
		final Object config,
		final PropertyDescriptor propDesc,
		final boolean polymorph,
		final boolean complex,
		final ConfigListDTO listDTO,
		final List<ConfigListDTO> dtos,
		final IScopePath scopePath,
		final ISimpleTypeConverter<?> customConverter) {

		final ConfigListDecorator<Object> listToSet = getListToSet(config, propDesc, listDTO);
		if (listToSet != null) {
			listToSet.setInitializingWhile(new Runnable() {
				@Override
				public void run() {
					listToSet.setConfigDTOs(dtos);
					listToSet.clear();
					if (complex || polymorph) {
						//complex items
						for (final IConfigDTO temp : listDTO.getItems()) {
							final ComplexConfigDTO complexDTO = (ComplexConfigDTO) temp;
							final Object cfg = complexDTODeserializer.createComplexConfigObject(complexDTO, null, scopePath);
							listToSet.add(cfg);
						}
					} else {
						//simple items
						for (final IConfigDTO temp : listDTO.getItems()) {
							final ConfigSimpleValueDTO simpleDTO = (ConfigSimpleValueDTO) temp;
							final Object value = simpleDTODeserializer.createSimpleValue(
									simpleDTO,
									getTypeLoader().getPolymorphType(simpleDTO.getPropertyType()),
									customConverter);
							listToSet.add(value);
						}
					}
				}
			});
		}
		return listToSet;
	}

	private List<ConfigListDTO> getListPropertyDtos(final List<ComplexConfigDTO> dtos, final String propName) {
		final List<ConfigListDTO> ret = new ArrayList<ConfigListDTO>();
		if (dtos != null) {
			for (final ComplexConfigDTO config : dtos) {
				if (config != null) {
					final ConfigListDTO listProperty = config.getListProperty(propName);
					if (listProperty != null) {
						ret.add(listProperty);
					}
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private ConfigListDecorator<Object> getListToSet(
		final Object parent,
		final PropertyDescriptor propertyDescriptor,
		final ConfigListDTO listDTO) {

		ConfigListDecorator<Object> result;

		if (listDTO.getItems() == null) {
			result = null;
		} else {
			List<Object> targetList = (List<Object>) propertyAccessor.read(parent, propertyDescriptor.getName());
			if (targetList instanceof ConfigListDecorator) {
				result = (ConfigListDecorator<Object>) targetList;
			} else {
				if (targetList == null) {
					targetList = new ArrayList<Object>();
				}
				result = new ConfigListDecorator<Object>(targetList);
			}
		}
		return result;
	}
}
