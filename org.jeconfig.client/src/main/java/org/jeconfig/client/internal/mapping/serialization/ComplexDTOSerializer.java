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

package org.jeconfig.client.internal.mapping.serialization;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.internal.ConfigAnnotations;
import org.jeconfig.client.internal.ConfigIdPropertyUtil;
import org.jeconfig.client.proxy.ConfigProxy;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.ClassInstantiation;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ComplexDTOSerializer extends AbstractDTOSerializer {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final ListDTOSerializer listDTOSerializer = new ListDTOSerializer();
	private final SetDTOSerializer setDTOSerializer;
	private final MapDTOSerializer mapDTOSerializer;
	private final SimpleDTOSerializer simpleDTOSerializer;

	public ComplexDTOSerializer(final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		simpleDTOSerializer = new SimpleDTOSerializer(simpleTypeConverterRegistry);
		mapDTOSerializer = new MapDTOSerializer(simpleTypeConverterRegistry);
		setDTOSerializer = new SetDTOSerializer(simpleTypeConverterRegistry);
	}

	@SuppressWarnings("unchecked")
	public ComplexConfigDTO createConfigDTO(final Object config, final ScopePath scopePath) {
		if (config instanceof ConfigProxy) {
			final ConfigProxy<ComplexConfigDTO> configProxy = (ConfigProxy<ComplexConfigDTO>) config;
			return createConfigDTO(
					config,
					configProxy.getConfigDTOs(),
					ProxyUtil.getConfigClass(config.getClass()),
					null,
					false,
					scopePath,
					false);
		}

		return createConfigDTO(config, null, config.getClass(), null, false, scopePath, true);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public ComplexConfigDTO createConfigDTO(
		final Object config,
		final List<ComplexConfigDTO> originalDTOs,
		final Class<?> configPropertyType,
		final String configPropertyName,
		final boolean polymorph,
		final ScopePath scopePath,
		final boolean shouldCreateWholeSubtree) {

		if (config != null && configPropertyType == null) {
			throw new IllegalArgumentException("The property name must not be null when config is not null!"); //$NON-NLS-1$
		}

		final String idPropertyName = config != null ? new ConfigIdPropertyUtil().findIdPropertyName(config.getClass()) : null;
		final ComplexConfigDTO configDTO = new ComplexConfigDTO();
		configDTO.setPropertyType(configPropertyType != null ? configPropertyType.getName() : null);
		configDTO.setPropertyName(configPropertyName);
		configDTO.setPolymorph(polymorph);
		configDTO.setDefiningScopePath(scopePath);
		configDTO.setNulled(config == null);
		configDTO.setIdPropertyName(idPropertyName);

		if (config != null) {
			for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(configPropertyType)) {
				final Annotation annotation = getConfigPropertyAnnotation(propertyDescriptor);
				if (annotation != null) {
					final String propertyName = propertyDescriptor.getName();
					final Class<?> propertyType = ProxyUtil.getConfigClass(propertyDescriptor.getPropertyType());
					final Object propertyValue = propertyAccessor.read(config, propertyName);

					if (shouldCreateWholeSubtree || shouldCreatePropertyDTO(config, propertyValue, propertyName, idPropertyName)) {
						final List<ConfigDTO> propertyOriginalDTOs = getPropertyOriginalDTOs(originalDTOs, propertyName);

						ConfigDTO propertyDTO;
						if (ConfigSimpleProperty.class.equals(annotation.annotationType())) {
							final ConfigSimpleProperty anno = (ConfigSimpleProperty) annotation;
							final SimpleTypeConverter<?> customConverter = createCustomConverter(anno.customConverter());
							propertyDTO = simpleDTOSerializer.createSimpleValueDTO(
									propertyValue,
									(List) propertyOriginalDTOs,
									propertyType,
									propertyName,
									scopePath,
									(SimpleTypeConverter<Object>) customConverter);

						} else if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
							final ConfigComplexProperty ccp = (ConfigComplexProperty) annotation;
							Class<?> typeToUse = propertyType;
							if (ccp.polymorph()) {
								if (propertyValue != null) {
									typeToUse = ProxyUtil.getConfigClass(propertyValue.getClass());
								} else {
									typeToUse = null;
								}
							}
							propertyDTO = createConfigDTO(
									propertyValue,
									(List) propertyOriginalDTOs,
									typeToUse,
									propertyName,
									ccp.polymorph(),
									scopePath,
									shouldCreateWholeSubtree);

						} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
							final ConfigListProperty listAnno = (ConfigListProperty) annotation;
							final SimpleTypeConverter<?> customConverter = createCustomConverter(listAnno.customConverter());
							final boolean complex = AnnotationUtil.getAnnotation(listAnno.itemType(), ConfigComplexType.class) != null;
							propertyDTO = listDTOSerializer.createListDTO(
									(List) propertyValue,
									(List) propertyOriginalDTOs,
									propertyType,
									propertyName,
									complex,
									listAnno.polymorph(),
									scopePath,
									listAnno.itemType(),
									this,
									simpleDTOSerializer,
									(SimpleTypeConverter<Object>) customConverter);

						} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
							final ConfigArrayProperty arrayAnno = (ConfigArrayProperty) annotation;
							final SimpleTypeConverter<?> customConverter = createCustomConverter(arrayAnno.customConverter());
							final List<Object> list = createListFromArray(propertyValue);
							final boolean complex = AnnotationUtil.getAnnotation(
									propertyType.getComponentType(),
									ConfigComplexType.class) != null;
							propertyDTO = listDTOSerializer.createListDTO(
									list,
									(List) propertyOriginalDTOs,
									propertyType,
									propertyName,
									complex,
									arrayAnno.polymorph(),
									scopePath,
									propertyType.getComponentType(),
									this,
									simpleDTOSerializer,
									(SimpleTypeConverter<Object>) customConverter);

						} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
							final ConfigSetProperty setAnno = (ConfigSetProperty) annotation;
							final SimpleTypeConverter<?> customConverter = createCustomConverter(setAnno.customConverter());
							final boolean complex = AnnotationUtil.getAnnotation(setAnno.itemType(), ConfigComplexType.class) != null;
							propertyDTO = setDTOSerializer.createSetDTO(
									(Set) propertyValue,
									(List) propertyOriginalDTOs,
									propertyType,
									propertyName,
									complex,
									setAnno.polymorph(),
									scopePath,
									setAnno.itemType(),
									shouldCreateWholeSubtree,
									this,
									simpleDTOSerializer,
									(SimpleTypeConverter<Object>) customConverter);

						} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
							final ConfigMapProperty mapAnno = (ConfigMapProperty) annotation;
							final boolean complex = AnnotationUtil.getAnnotation(mapAnno.valueType(), ConfigComplexType.class) != null;
							final SimpleTypeConverter<?> customValueConverter = createCustomConverter(mapAnno.customValueConverter());
							final SimpleTypeConverter<?> customKeyConverter = createCustomConverter(mapAnno.customKeyConverter());
							propertyDTO = mapDTOSerializer.createMapDTO(
									(Map) propertyValue,
									(List) propertyOriginalDTOs,
									propertyType,
									propertyName,
									complex,
									mapAnno.polymorph(),
									scopePath,
									mapAnno.keyType(),
									mapAnno.valueType(),
									shouldCreateWholeSubtree,
									this,
									simpleDTOSerializer,
									(SimpleTypeConverter<Object>) customValueConverter,
									(SimpleTypeConverter<Object>) customKeyConverter);

						} else {
							throw new IllegalStateException("Incomplete if-else block"); //$NON-NLS-1$
						}

						configDTO.addProperty(propertyDTO);
					}
				}
			}
		}

		updateVersionsAndParent(configDTO, originalDTOs, scopePath);

		return configDTO;
	}

	private SimpleTypeConverter<?> createCustomConverter(final Class<? extends SimpleTypeConverter<?>> customConverter) {
		if (NoCustomSimpleTypeConverter.class.equals(customConverter)) {
			return null;
		}
		return classInstantiation.newInstance(customConverter);
	}

	private List<Object> createListFromArray(final Object array) {
		if (array != null) {
			final List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < Array.getLength(array); i++) {
				list.add(Array.get(array, i));
			}
			return list;
		}
		return null;
	}

	private List<ConfigDTO> getPropertyOriginalDTOs(final List<ComplexConfigDTO> originalDTOs, final String propertyName) {
		if (originalDTOs == null) {
			return null;
		}

		final List<ConfigDTO> result = new ArrayList<ConfigDTO>();
		for (final ComplexConfigDTO originalDTO : originalDTOs) {
			final ConfigDTO propertyDTO = originalDTO.getProperty(propertyName);
			if (propertyDTO != null) {
				result.add(propertyDTO);
			}
		}

		return result;
	}

	private boolean shouldCreatePropertyDTO(
		final Object config,
		final Object propertyValue,
		final String propertyName,
		final String idPropertyName) {

		if (!(config instanceof ConfigProxy)) {
			return true;
		}

		final ConfigProxy<?> configProxy = (ConfigProxy<?>) config;
		if (configProxy.getPropertiesWithDiff().contains(propertyName)) {
			return true;
		}

		if (propertyValue instanceof ConfigProxy) {
			final ConfigProxy<?> valueConfigProxy = (ConfigProxy<?>) propertyValue;
			if (valueConfigProxy.hasDiff()) {
				return true;
			}
		}

		if (propertyName.equals(idPropertyName)) {
			return true;
		}

		return false;
	}

	private Annotation getConfigPropertyAnnotation(final PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor.getReadMethod() != null) {
			for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
				if (ConfigAnnotations.CONFIG_ANNOTATIONS.contains(annotation.annotationType())) {
					return annotation;
				}
			}
		}
		return null;
	}
}
