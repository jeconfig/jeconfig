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

package org.jeconfig.client.proxy;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.util.proxy.ProxyObject;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.internal.ConfigIdPropertyUtil;
import org.jeconfig.common.reflection.ClassInstantiation;
import org.jeconfig.common.reflection.PropertyAccessor;

/**
 * Scans for not wrapped collections/complex types and wraps them. updated configDto of defining scope
 */
public final class ProxyUpdater {
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final ConfigObjectFactory objectFactory;
	private final ConfigObjectCopyUtil copyUtil;
	private final SimpleTypeConverterRegistry simpleTypeConverterRegistry;
	private final ConfigIdPropertyUtil configIdPropertyUtil = new ConfigIdPropertyUtil();

	public ProxyUpdater(final ConfigObjectFactory proxyFactory, final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		this.objectFactory = proxyFactory;
		this.simpleTypeConverterRegistry = simpleTypeConverterRegistry;
		copyUtil = new ConfigObjectCopyUtil();
	}

	public SimpleTypeConverterRegistry getSimpleTypeConverterRegistry() {
		return simpleTypeConverterRegistry;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void updateConfigProxy(final ConfigProxy proxy, final List<ConfigDTO> dtos, final Annotation configPropertyAnnotation) {
		Assert.paramNotNull(proxy, "proxy"); //$NON-NLS-1$
		Assert.paramNotNull(dtos, "dtos"); //$NON-NLS-1$
		Assert.paramNotNull(configPropertyAnnotation, "configPropertyAnnotation"); //$NON-NLS-1$

		if (proxy instanceof ConfigListDecorator) {
			updateListProxy((ConfigListDecorator) proxy, (List) proxy, (List) dtos, configPropertyAnnotation);
		} else if (proxy instanceof ConfigSetDecorator) {
			updateSetProxy((ConfigSetDecorator) proxy, (Set) proxy, (List) dtos, configPropertyAnnotation);
		} else if (proxy instanceof ConfigMapDecorator) {
			updateMapProxy((ConfigMapDecorator) proxy, (Map) proxy, (List) dtos, configPropertyAnnotation);
		} else {
			updateConfig(proxy, (List) dtos);
		}
	}

	public <T> void updateConfig(final T config, final List<ComplexConfigDTO> complexDtos) {
		final List<ComplexConfigDTO> notNullComplexDtos = new ArrayList<ComplexConfigDTO>();
		for (final ComplexConfigDTO dto : complexDtos) {
			if (dto != null) {
				notNullComplexDtos.add(dto);
			}
		}
		doUpdateConfig(config, notNullComplexDtos);
	}

	private <T> void doUpdateConfig(final T config, final List<ComplexConfigDTO> complexDtos) {
		updateComplexDTO(config, complexDtos);

		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(ProxyUtil.getConfigClass(config.getClass()))) {
			if (propertyDescriptor.getReadMethod() != null) {
				final String propertyName = propertyDescriptor.getName();
				for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
					if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
						final List<ComplexConfigDTO> dtos = getComplexDtos(complexDtos, propertyName);
						handleComplexProperty(config, propertyDescriptor, (ConfigComplexProperty) annotation, dtos);
					} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
						final List<ConfigSetDTO> dtos = getSetDtos(complexDtos, propertyName);
						handleSetProperty(config, propertyDescriptor, annotation, dtos);
					} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
						final List<ConfigMapDTO> dtos = getMapDtos(complexDtos, propertyName);
						handleMap(config, propertyDescriptor, annotation, dtos);
					} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
						final List<ConfigListDTO> dtos = getListDtos(complexDtos, propertyName);
						handleList(config, propertyDescriptor, annotation, dtos);
					} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
						final List<ConfigListDTO> dtos = getListDtos(complexDtos, propertyName);
						handleArray(config, propertyDescriptor, (ConfigArrayProperty) annotation, dtos);
					}
				}
			}
		}
	}

	private List<ComplexConfigDTO> getComplexDtos(final List<ComplexConfigDTO> complexDtos, final String propertyName) {
		final List<ComplexConfigDTO> dtos = new ArrayList<ComplexConfigDTO>();
		if (complexDtos != null) {
			for (final ComplexConfigDTO d : complexDtos) {
				if (d != null) {
					final ComplexConfigDTO complexProperty = d.getComplexProperty(propertyName);
					if (complexProperty != null) {
						dtos.add(complexProperty);
					}
				}
			}
		}
		return dtos;
	}

	private List<ConfigSetDTO> getSetDtos(final List<ComplexConfigDTO> complexDtos, final String propertyName) {
		final List<ConfigSetDTO> dtos = new ArrayList<ConfigSetDTO>();
		if (complexDtos != null) {
			for (final ComplexConfigDTO d : complexDtos) {
				if (d != null) {
					final ConfigSetDTO setProperty = d.getSetProperty(propertyName);
					if (setProperty != null) {
						dtos.add(setProperty);
					}
				}
			}
		}
		return dtos;
	}

	private List<ConfigMapDTO> getMapDtos(final List<ComplexConfigDTO> complexDtos, final String propertyName) {
		final List<ConfigMapDTO> dtos = new ArrayList<ConfigMapDTO>();
		if (complexDtos != null) {
			for (final ComplexConfigDTO d : complexDtos) {
				if (d != null) {
					final ConfigMapDTO mapProperty = d.getMapProperty(propertyName);
					if (mapProperty != null) {
						dtos.add(mapProperty);
					}
				}
			}
		}
		return dtos;
	}

	private List<ConfigListDTO> getListDtos(final List<ComplexConfigDTO> complexDtos, final String propertyName) {
		final List<ConfigListDTO> dtos = new ArrayList<ConfigListDTO>();
		if (complexDtos != null) {
			for (final ComplexConfigDTO d : complexDtos) {
				if (d != null) {
					final ConfigListDTO listProperty = d.getListProperty(propertyName);
					if (listProperty != null) {
						dtos.add(listProperty);
					}
				}
			}
		}
		return dtos;
	}

	@SuppressWarnings("unchecked")
	private <T> void updateComplexDTO(final T config, final List<ComplexConfigDTO> complexDtos) {
		if (config instanceof ConfigProxy) {
			final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
			proxy.setConfigDTOs(complexDtos);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void handleArray(
		final T config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigArrayProperty annotation,
		final List<ConfigListDTO> listDTOs) {
		final Object originalArray = propertyAccessor.read(config, propertyDescriptor.getName());
		if (originalArray != null) {
			final Class<?> arrayItemType = propertyDescriptor.getPropertyType().getComponentType();
			final ConfigComplexType itemTypeAnnotation = AnnotationUtil.getAnnotation(arrayItemType, ConfigComplexType.class);
			if (itemTypeAnnotation != null || annotation.polymorph()) {
				for (int i = 0; i < Array.getLength(originalArray); i++) {
					final Object obj = Array.get(originalArray, i);
					Object item = null;
					if (obj instanceof ConfigProxy) {
						item = obj;
					} else if (obj != null) {
						if (annotation.polymorph()) {
							item = objectFactory.createComplexProperty(ProxyUtil.getConfigClass(obj.getClass()));
						} else {
							item = objectFactory.createComplexProperty(arrayItemType);
						}
						copyUtil.copyConfigTree(objectFactory, obj, item);
					}
					Array.set(originalArray, i, item);
					if (item != null) {
						updateConfig(item, Collections.EMPTY_LIST);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void handleList(
		final T config,
		final PropertyDescriptor propertyDescriptor,
		final Annotation annotation,
		final List<ConfigListDTO> listDtos) {
		final List<Object> originalList = (List<Object>) propertyAccessor.read(config, propertyDescriptor.getName());
		if (originalList != null) {
			ConfigListDecorator<Object> listDecorator;
			if (originalList instanceof ConfigListDecorator) {
				listDecorator = (ConfigListDecorator<Object>) originalList;
			} else {
				listDecorator = new ConfigListDecorator<Object>(new ArrayList<Object>());
				propertyAccessor.write(config, propertyDescriptor.getName(), listDecorator);
			}
			updateListProxy(listDecorator, originalList, listDtos, annotation);
		}
	}

	private void updateListProxy(
		final ConfigListDecorator<Object> listDecorator,
		final List<Object> originalList,
		final List<ConfigListDTO> listDtos,
		final Annotation annotation) {

		listDecorator.setInitializingWhile(new Runnable() {
			@Override
			public void run() {
				updateListDto(listDecorator, listDtos);
				final ConfigListProperty listAnnotation = (ConfigListProperty) annotation;
				final ConfigComplexType itemTypeAnnotation = AnnotationUtil.getAnnotation(
						listAnnotation.itemType(),
						ConfigComplexType.class);
				if (itemTypeAnnotation != null || listAnnotation.polymorph()) {
					final List<Object> tempList = new LinkedList<Object>();
					for (final Object obj : originalList) {

						Object complexProperty = null;
						if (obj instanceof ConfigProxy) {
							complexProperty = obj;
						} else if (obj != null) {
							if (listAnnotation.polymorph()) {
								complexProperty = objectFactory.createComplexProperty(ProxyUtil.getConfigClass(obj.getClass()));
							} else {
								complexProperty = objectFactory.createComplexProperty(listAnnotation.itemType());
							}
							copyUtil.copyConfigTree(objectFactory, obj, complexProperty);
						}
						tempList.add(complexProperty);

						if (complexProperty != null) {
							updateConfig(complexProperty, Collections.<ComplexConfigDTO> emptyList());
						}
					}
					listDecorator.clear();
					listDecorator.addAll(tempList);
				} else {
					if (listDecorator != originalList) {
						listDecorator.clear();
						listDecorator.addAll(originalList);
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private <T> void handleMap(
		final T config,
		final PropertyDescriptor propertyDescriptor,
		final Annotation annotation,
		final List<ConfigMapDTO> mapDtos) {
		final Map<Object, Object> originalMap = (Map<Object, Object>) propertyAccessor.read(config, propertyDescriptor.getName());
		if (originalMap != null) {
			ConfigMapDecorator<Object, Object> mapDecorator;
			if (originalMap instanceof ConfigMapDecorator) {
				mapDecorator = (ConfigMapDecorator<Object, Object>) originalMap;
			} else {
				mapDecorator = new ConfigMapDecorator<Object, Object>(new HashMap<Object, Object>(), this);
				propertyAccessor.write(config, propertyDescriptor.getName(), mapDecorator);
			}
			updateMapProxy(mapDecorator, originalMap, mapDtos, annotation);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateMapProxy(
		final ConfigMapDecorator<Object, Object> mapDecorator,
		final Map<Object, Object> originalMap,
		final List<ConfigMapDTO> mapDtos,
		final Annotation annotation) {

		mapDecorator.setInitializingWhile(new Runnable() {
			@Override
			public void run() {
				updateMapDto(mapDecorator, mapDtos);
				final ConfigMapProperty mapAnnotation = (ConfigMapProperty) annotation;
				final ConfigComplexType valueTypeAnnotation = AnnotationUtil.getAnnotation(
						mapAnnotation.valueType(),
						ConfigComplexType.class);
				final SimpleTypeConverter<?> customConverter = createCustomConverter(mapAnnotation.customValueConverter());
				if (valueTypeAnnotation != null || mapAnnotation.polymorph()) {
					final Map<Object, Object> tempMap = new HashMap<Object, Object>();
					for (final Entry<Object, Object> entry : originalMap.entrySet()) {
						Object complexValue = null;
						if (entry.getValue() instanceof ConfigProxy) {
							complexValue = entry.getValue();
						} else if (entry.getValue() != null) {
							if (mapAnnotation.polymorph()) {
								complexValue = objectFactory.createComplexProperty(ProxyUtil.getConfigClass(entry.getValue().getClass()));
							} else {
								complexValue = objectFactory.createComplexProperty(mapAnnotation.valueType());
							}
							copyUtil.copyConfigTree(objectFactory, entry.getValue(), complexValue);
						}
						tempMap.put(entry.getKey(), complexValue);
						if (complexValue != null) {
							final String stringKey = getObjectValue(
									(SimpleTypeConverter<Object>) customConverter,
									entry.getKey());
							final List<ComplexConfigDTO> complexConfigDTOs = getDtosForKey(mapDtos, stringKey);
							updateConfig(complexValue, complexConfigDTOs);
						}
					}
					mapDecorator.clear();
					mapDecorator.putAll(tempMap);
				} else {
					if (mapDecorator != originalMap) {
						mapDecorator.clear();
						mapDecorator.putAll(originalMap);
					}
				}
			}
		});
	}

	private List<ComplexConfigDTO> getDtosForKey(final List<ConfigMapDTO> mapDtos, final String stringKey) {
		final List<ComplexConfigDTO> complexConfigDTOs = new ArrayList<ComplexConfigDTO>();
		if (mapDtos != null) {
			for (final ConfigMapDTO md : mapDtos) {
				final Map<String, ConfigDTO> dtoMap = md.getMap();
				if (dtoMap != null) {
					final ComplexConfigDTO configDTO = (ComplexConfigDTO) dtoMap.get(stringKey);
					if (configDTO != null) {
						complexConfigDTOs.add(configDTO);
					}
				}
			}
		}
		return complexConfigDTOs;
	}

	@SuppressWarnings("unchecked")
	private <T> void handleSetProperty(
		final T config,
		final PropertyDescriptor propertyDescriptor,
		final Annotation annotation,
		final List<ConfigSetDTO> setDtos) {
		final Set<Object> originalSet = (Set<Object>) propertyAccessor.read(config, propertyDescriptor.getName());
		if (originalSet != null) {
			ConfigSetDecorator<Object> setDecorator;
			if (originalSet instanceof ConfigSetDecorator) {
				setDecorator = (ConfigSetDecorator<Object>) originalSet;
			} else {
				setDecorator = new ConfigSetDecorator<Object>(new HashSet<Object>(), this);
				propertyAccessor.write(config, propertyDescriptor.getName(), setDecorator);
			}
			updateSetProxy(setDecorator, originalSet, setDtos, annotation);
		}
	}

	private void updateSetProxy(
		final ConfigSetDecorator<Object> setDecorator,
		final Set<Object> originalSet,
		final List<ConfigSetDTO> setDtos,
		final Annotation annotation) {

		setDecorator.setInitializingWhile(new Runnable() {
			@Override
			public void run() {
				updateSetDto(setDecorator, setDtos);
				final ConfigSetProperty setAnnotation = (ConfigSetProperty) annotation;
				final ConfigComplexType typeAnnotation = AnnotationUtil.getAnnotation(
						setAnnotation.itemType(),
						ConfigComplexType.class);
				if (typeAnnotation != null || setAnnotation.polymorph()) {
					final Set<Object> tempSet = new HashSet<Object>();
					for (final Object obj : originalSet) {
						Object complexProperty = null;
						if (obj instanceof ConfigProxy) {
							complexProperty = obj;
						} else if (obj != null) {
							if (setAnnotation.polymorph()) {
								complexProperty = objectFactory.createComplexProperty(ProxyUtil.getConfigClass(obj.getClass()));
							} else {
								complexProperty = objectFactory.createComplexProperty(setAnnotation.itemType());
							}
							copyUtil.copyConfigTree(objectFactory, obj, complexProperty);
						}
						tempSet.add(complexProperty);
						if (complexProperty != null) {
							final String idPropertyName = configIdPropertyUtil.getIdPropertyName(setAnnotation.itemType());
							final List<ComplexConfigDTO> complexDtos = findComplexDto(setDtos, complexProperty, idPropertyName);
							updateConfig(complexProperty, complexDtos);
						}
					}
					setDecorator.clear();
					setDecorator.addAll(tempSet);

				} else {
					if (setDecorator != originalSet) {
						setDecorator.clear();
						setDecorator.addAll(originalSet);
					}
				}
			}
		});
	}

	private List<ComplexConfigDTO> findComplexDto(
		final List<ConfigSetDTO> setDtos,
		final Object complexProperty,
		final String idPropertyName) {
		final List<ComplexConfigDTO> complexDtos = new ArrayList<ComplexConfigDTO>();

		for (final ConfigSetDTO setDto : setDtos) {
			final Set<ConfigDTO> items = setDto.getItems();
			if (items != null) {
				for (final ConfigDTO dto : items) {
					final ComplexConfigDTO complexDto = (ComplexConfigDTO) dto;
					final ConfigSimpleValueDTO idValueDto = complexDto.getSimpleValueProperty(idPropertyName);
					final String idStringValue = configIdPropertyUtil.getIdPropertyValueAsString(
							complexProperty,
							idPropertyName,
							simpleTypeConverterRegistry);
					if (idValueDto.getValue().equals(idStringValue)) {
						complexDtos.add(complexDto);
					}
				}
			}
		}
		return complexDtos;
	}

	private String getObjectValue(final SimpleTypeConverter<Object> customConverter, final Object objectValue) {
		if (customConverter != null) {
			return customConverter.convertToSerializedForm(objectValue);
		} else {
			return simpleTypeConverterRegistry.convertToSerializedForm(objectValue);
		}
	}

	private void updateMapDto(final Map<Object, Object> map, final List<ConfigMapDTO> dtos) {
		final ConfigMapDecorator<Object, Object> decorator = (ConfigMapDecorator<Object, Object>) map;
		decorator.setConfigDTOs(dtos);
	}

	private void updateListDto(final List<Object> list, final List<ConfigListDTO> dtos) {
		final ConfigListDecorator<Object> decorator = (ConfigListDecorator<Object>) list;
		decorator.setConfigDTOs(dtos);
	}

	private void updateSetDto(final Set<Object> set, final List<ConfigSetDTO> dtos) {
		final ConfigSetDecorator<Object> decorator = (ConfigSetDecorator<Object>) set;
		decorator.setConfigDTOs(dtos);
	}

	private <T> void handleComplexProperty(
		final T config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigComplexProperty annotation,
		final List<ComplexConfigDTO> dtos) {
		final Object object = propertyAccessor.read(config, propertyDescriptor.getName());
		if (object != null) {
			if (!(object instanceof ProxyObject)) {
				Object complexProperty = null;
				if (annotation.polymorph()) {
					complexProperty = objectFactory.createComplexProperty(ProxyUtil.getConfigClass(object.getClass()));
				} else {
					complexProperty = objectFactory.createComplexProperty(propertyDescriptor.getPropertyType());
				}
				copyUtil.copyConfigTree(objectFactory, object, complexProperty);
				propertyAccessor.write(config, propertyDescriptor.getName(), complexProperty);
				updateConfig(complexProperty, dtos);
			} else {
				updateConfig(object, dtos);
			}
		}
	}

	private SimpleTypeConverter<?> createCustomConverter(final Class<? extends SimpleTypeConverter<?>> customConverter) {
		if (NoCustomSimpleTypeConverter.class.equals(customConverter)) {
			return null;
		}
		return classInstantiation.newInstance(customConverter);
	}
}
