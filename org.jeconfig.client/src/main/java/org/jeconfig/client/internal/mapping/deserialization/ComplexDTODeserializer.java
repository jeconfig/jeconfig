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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.proxy.IConfigObjectFactory;
import org.jeconfig.client.proxy.IConfigProxy;
import org.jeconfig.client.proxy.ProxyUpdater;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ComplexDTODeserializer extends AbstractDTODeserializer {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final IConfigObjectFactory proxyFactory;
	private final SimpleDTODeserializer simpleDTODeserializer;
	private final ArrayDTODeserializer arrayDTODeserializer;
	private final ListDTODeserializer listDTODeserializer;
	private final SetDTODeserializer setDTODeserializer;
	private final MapDTODeserializer mapDTODeserializer;

	public ComplexDTODeserializer(
		final IConfigObjectFactory proxyFactory,
		final ISimpleTypeConverterRegistry converterRegistry,
		final ProxyUpdater proxyUpdater) {
		this.proxyFactory = proxyFactory;
		simpleDTODeserializer = new SimpleDTODeserializer(converterRegistry);
		arrayDTODeserializer = new ArrayDTODeserializer(simpleDTODeserializer, this);
		listDTODeserializer = new ListDTODeserializer(simpleDTODeserializer, this);
		setDTODeserializer = new SetDTODeserializer(simpleDTODeserializer, this, proxyUpdater);
		mapDTODeserializer = new MapDTODeserializer(converterRegistry, simpleDTODeserializer, this, proxyUpdater);
	}

	@SuppressWarnings("unchecked")
	public void processConfig(
		final Class<?> configClass,
		final ComplexConfigDTO configDTO,
		final Object config,
		final List<ComplexConfigDTO> dtos,
		final IScopePath scopePath) {

		final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) config;
		proxy.setInitializingWhile(new Runnable() {
			@Override
			public void run() {
				doProcessConfig(configClass, configDTO, proxy, dtos, scopePath);
			}
		});
	}

	private void doProcessConfig(
		final Class<?> configClass,
		final ComplexConfigDTO configDTO,
		final IConfigProxy<ComplexConfigDTO> config,
		final List<ComplexConfigDTO> dtos,
		final IScopePath scopePath) {

		final List<ComplexConfigDTO> dtosToSet = removeNullObjects(dtos);
		config.setConfigDTOs(dtosToSet);
		config.setPropertiesWithDiff(calculateDeclaredProperties(dtosToSet, scopePath));

		for (final PropertyDescriptor propDesc : propertyAccessor.getPropertyDescriptors(ProxyUtil.getConfigClass(configClass))) {
			if (propDesc.getReadMethod() != null) {
				for (final Annotation annotation : propDesc.getReadMethod().getAnnotations()) {
					final String propName = propDesc.getName();
					if (ConfigSimpleProperty.class.equals(annotation.annotationType())) {
						simpleDTODeserializer.handleSimpleProperty(configDTO, config, propDesc, annotation, propName);

					} else if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
						handleComplexProperty(configDTO, config, dtos, scopePath, propName);

					} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
						listDTODeserializer.handleListProperty(configDTO, config, dtos, scopePath, propDesc, annotation, propName);

					} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
						arrayDTODeserializer.handleArrayProperty(configDTO, config, scopePath, propDesc, annotation, propName);

					} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
						setDTODeserializer.handleSetProperty(configDTO, config, dtos, scopePath, propDesc, annotation, propName);

					} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
						mapDTODeserializer.handleMapProperty(configDTO, config, dtos, scopePath, propDesc, annotation, propName);
					}
				}
			}
		}
	}

	public Object createComplexConfigObject(
		final ComplexConfigDTO complexDTO,
		final List<ComplexConfigDTO> dtos,
		final IScopePath scopePath) {
		final String propertyType = complexDTO.getPropertyType();
		if (propertyType != null) {
			final Class<?> itemType = getTypeLoader().getPolymorphType(propertyType);
			final Object complexProperty = proxyFactory.createComplexProperty(itemType);
			final Class<? extends Object> complexClass = ProxyUtil.getConfigClass(complexProperty.getClass());
			processConfig(complexClass, complexDTO, complexProperty, dtos, scopePath);
			return complexProperty;
		}
		return null;
	}

	private void handleComplexProperty(
		final ComplexConfigDTO configDTO,
		final Object config,
		final List<ComplexConfigDTO> dtos,
		final IScopePath scopePath,
		final String propName) {
		final ComplexConfigDTO complexDTO = configDTO.getComplexProperty(propName);

		Object complexProperty = null;
		if (complexDTO != null && !complexDTO.isNulled()) {
			complexProperty = createComplexConfigObject(complexDTO, getComplexPropertyDtos(dtos, propName), scopePath);
		}
		propertyAccessor.write(config, propName, complexProperty);
	}

	private List<ComplexConfigDTO> getComplexPropertyDtos(final List<ComplexConfigDTO> configs, final String propertyName) {
		final List<ComplexConfigDTO> ret = new ArrayList<ComplexConfigDTO>();
		for (final ComplexConfigDTO config : configs) {
			if (config != null) {
				final ComplexConfigDTO complexProperty = config.getComplexProperty(propertyName);
				if (complexProperty != null) {
					ret.add(complexProperty);
				}
			}
		}
		return ret;
	}

	private <T> List<T> removeNullObjects(final List<T> list) {
		List<T> result = null;
		if (list != null) {
			result = new ArrayList<T>();
			for (final T dto : list) {
				if (dto != null) {
					result.add(dto);
				}
			}
		}
		return result;
	}

	private Set<String> calculateDeclaredProperties(final List<ComplexConfigDTO> configDTOs, final IScopePath scopePath) {
		final ComplexConfigDTO dto = (configDTOs != null && !configDTOs.isEmpty()) ? configDTOs.get(configDTOs.size() - 1) : null;
		if (dto != null && dto.getDefiningScopePath().equals(scopePath)) {
			return dto.getDeclaredProperties();
		}
		return Collections.emptySet();
	}

}
