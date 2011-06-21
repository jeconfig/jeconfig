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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ConfigMapDecorator;
import org.jeconfig.client.proxy.ProxyUpdater;
import org.jeconfig.common.reflection.PropertyAccessor;

public class MapDTODeserializer extends AbstractDTODeserializer {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final SimpleTypeConverterRegistry simpleTypeConverterRegistry;
	private final SimpleDTODeserializer simpleDTODeserializer;
	private final ComplexDTODeserializer complexDTODeserializer;
	private final ProxyUpdater proxyUpdater;

	public MapDTODeserializer(
		final SimpleTypeConverterRegistry simpleTypeConverterRegistry,
		final SimpleDTODeserializer simpleDTODeserializer,
		final ComplexDTODeserializer complexDTODeserializer,
		final ProxyUpdater proxyUpdater) {
		this.simpleTypeConverterRegistry = simpleTypeConverterRegistry;
		this.simpleDTODeserializer = simpleDTODeserializer;
		this.complexDTODeserializer = complexDTODeserializer;
		this.proxyUpdater = proxyUpdater;
	}

	@SuppressWarnings("unchecked")
	public void handleMapProperty(
		final ComplexConfigDTO configDTO,
		final Object config,
		final List<ComplexConfigDTO> dtos,
		final ScopePath scopePath,
		final PropertyDescriptor propDesc,
		final Annotation annotation,
		final String propName) {
		final ConfigMapProperty mapAnno = (ConfigMapProperty) annotation;
		final SimpleTypeConverter<?> customValueConverter = createCustomConverter(mapAnno.customValueConverter());
		final SimpleTypeConverter<?> customKeyConverter = createCustomConverter(mapAnno.customKeyConverter());
		final boolean polymorph = mapAnno.polymorph();
		final boolean complex = AnnotationUtil.getAnnotation(mapAnno.valueType(), ConfigComplexType.class) != null;
		final ConfigMapDTO mapDTO = configDTO.getMapProperty(propName);
		Map<Object, Object> mapToSet = null;
		mapToSet = processMap(
				config,
				propDesc,
				mapAnno,
				polymorph,
				complex,
				mapDTO,
				getMapPropertyDtos(dtos, propName),
				scopePath,
				customValueConverter,
				(SimpleTypeConverter<Object>) customKeyConverter);
		propertyAccessor.write(config, propName, mapToSet);
	}

	private List<ConfigMapDTO> getMapPropertyDtos(final List<ComplexConfigDTO> dtos, final String propName) {
		final List<ConfigMapDTO> ret = new ArrayList<ConfigMapDTO>();
		for (final ComplexConfigDTO config : dtos) {
			if (config != null) {
				final ConfigMapDTO setProperty = config.getMapProperty(propName);
				if (setProperty != null) {
					ret.add(setProperty);
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> processMap(
		final Object config,
		final PropertyDescriptor propDesc,
		final ConfigMapProperty mapAnno,
		final boolean polymorph,
		final boolean complex,
		final ConfigMapDTO mapDTO,
		final List<ConfigMapDTO> dtos,
		final ScopePath scopePath,
		final SimpleTypeConverter<?> customConverter,
		final SimpleTypeConverter<Object> customKeyConverter) {
		final ConfigMapDecorator<Object, Object> mapToSet = getMapToSet(config, propDesc, mapDTO);

		if (mapToSet != null) {
			mapToSet.setInitializingWhile(new Runnable() {
				@Override
				public void run() {
					mapToSet.setConfigDTOs(dtos);
					mapToSet.clear();
					for (final Entry<String, ConfigDTO> entry : mapDTO.getMap().entrySet()) {
						final String keyString = entry.getKey();
						Object key = null;
						if (keyString != null) {
							if (customKeyConverter != null) {
								key = customKeyConverter.convertToObject((Class<Object>) mapAnno.keyType(), keyString);
							} else {
								key = simpleTypeConverterRegistry.convertToObject(mapAnno.keyType(), keyString);
							}
						}
						if (complex || polymorph) {
							//complex values
							final ComplexConfigDTO complexDTO = (ComplexConfigDTO) entry.getValue();
							final Object cfg = complexDTODeserializer.createComplexConfigObject(
									complexDTO,
									getComplexMapDtos(keyString, dtos),
									scopePath);
							mapToSet.put(key, cfg);
						} else {
							//simple values
							final ConfigSimpleValueDTO simpleDTO = (ConfigSimpleValueDTO) entry.getValue();
							final Object value = simpleDTODeserializer.createSimpleValue(
									simpleDTO,
									mapAnno.valueType(),
									customConverter);
							mapToSet.put(key, value);
						}
					}
				}
			});
		}
		return mapToSet;
	}

	private List<ComplexConfigDTO> getComplexMapDtos(final String key, final List<ConfigMapDTO> dtos) {
		final List<ComplexConfigDTO> ret = new ArrayList<ComplexConfigDTO>();
		for (final ConfigMapDTO mapDto : dtos) {
			final ComplexConfigDTO complexDto = (mapDto.getMap() != null) ? (ComplexConfigDTO) mapDto.getMap().get(key) : null;
			if (complexDto != null) {
				ret.add(complexDto);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private ConfigMapDecorator<Object, Object> getMapToSet(
		final Object parent,
		final PropertyDescriptor propertyDescriptor,
		final ConfigMapDTO mapDTO) {

		ConfigMapDecorator<Object, Object> result;

		if (mapDTO.getMap() == null) {
			result = null;
		} else {
			Map<Object, Object> targetMap = (Map<Object, Object>) propertyAccessor.read(parent, propertyDescriptor.getName());
			if (targetMap instanceof ConfigMapDecorator) {
				result = (ConfigMapDecorator<Object, Object>) targetMap;
			} else {
				if (targetMap == null) {
					targetMap = new HashMap<Object, Object>();
				}
				result = new ConfigMapDecorator<Object, Object>(targetMap, proxyUpdater);
			}
		}
		return result;
	}

}
