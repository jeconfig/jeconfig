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

package org.jeconfig.client.internal.crossreferences;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.ConfigService;
import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ConfigProxy;
import org.jeconfig.client.proxy.RootConfigProxy;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

import javassist.util.proxy.ProxyObject;

public final class CrossReferencesResolver {
	private final ConfigService configService;
	private final PropertyAccessor propertyAccessor;

	public CrossReferencesResolver(final ConfigService configService) {
		this.configService = configService;
		propertyAccessor = new PropertyAccessor();
	}

	public void resolveCrossReferences(final Object config) {
		if (config instanceof RootConfigProxy && config instanceof ProxyObject) {
			final RootConfigProxy rp = (RootConfigProxy) config;
			rp.setReadOnlyCrossReferences(false);
			handleComplexType(config);
			rp.setReadOnlyCrossReferences(true);

		} else {
			throw new IllegalArgumentException("not a proxy"); //$NON-NLS-1$
		}
	}

	private void handleComplexType(final Object config) {
		if (config != null) {
			final Class<?> configClass = ProxyUtil.getConfigClass(config.getClass());
			final ConfigProxy<?> proxy = (ConfigProxy<?>) config;
			proxy.setReadOnlyCrossReferences(false);

			for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(configClass)) {
				if (propertyDescriptor.getReadMethod() != null) {
					for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
						if (ConfigCrossReference.class.equals(annotation.annotationType())) {
							resolveCrossReference(config, propertyDescriptor, (ConfigCrossReference) annotation);
						} else if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
							handleComplexType(propertyAccessor.read(config, propertyDescriptor.getName()));
						} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
							handleArrayProperty(config, propertyDescriptor, (ConfigArrayProperty) annotation);
						} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
							handleListProperty(config, propertyDescriptor, (ConfigListProperty) annotation);
						} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
							handleSetProperty(config, propertyDescriptor, (ConfigSetProperty) annotation);
						} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
							handleMapProperty(config, propertyDescriptor, (ConfigMapProperty) annotation);
						}
					}
				}
			}
			proxy.setReadOnlyCrossReferences(true);
		}
	}

	private void resolveCrossReference(
		final Object config,
		final PropertyDescriptor propDesc,
		final ConfigCrossReference crossReferenceAnnotation) {

		final Class<?> referenceClass = propDesc.getPropertyType();
		final ScopePath referenceScope = getScope(propDesc.getPropertyType(), crossReferenceAnnotation);

		final Object referenceConfig = configService.load(referenceClass, referenceScope);
		propertyAccessor.write(config, propDesc.getName(), referenceConfig);

		handleReadOnlyComplexType(referenceConfig);
	}

	private void handleReadOnlyComplexType(final Object referenceConfig) {
		final Class<?> configClass = ProxyUtil.getConfigClass(referenceConfig.getClass());
		final ConfigProxy<?> proxy = (ConfigProxy<?>) referenceConfig;
		proxy.setReadOnly(false);
		proxy.setReadOnlyCrossReferences(false);

		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(configClass)) {
			if (propertyDescriptor.getReadMethod() != null) {
				for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
					if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
						handleReadOnlyComplexType(propertyAccessor.read(referenceConfig, propertyDescriptor.getName()));
					} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
						handleReadOnlyArrayProperty(referenceConfig, propertyDescriptor, (ConfigArrayProperty) annotation);
					} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
						handleReadOnlyListProperty(referenceConfig, propertyDescriptor, (ConfigListProperty) annotation);
					} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
						handleReadOnlySetProperty(referenceConfig, propertyDescriptor, (ConfigSetProperty) annotation);
					} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
						handleReadOnlyMapProperty(referenceConfig, propertyDescriptor, (ConfigMapProperty) annotation);
					}
				}
			}
		}
		proxy.setReadOnly(true);
		proxy.setReadOnlyCrossReferences(true);
	}

	@SuppressWarnings("unchecked")
	private void handleReadOnlyMapProperty(
		final Object referenceConfig,
		final PropertyDescriptor propertyDescriptor,
		final ConfigMapProperty annotation) {

		final Map<Object, Object> map = (Map<Object, Object>) propertyAccessor.read(referenceConfig, propertyDescriptor.getName());
		if (map != null) {
			final ConfigProxy<?> proxy = (ConfigProxy<?>) map;
			proxy.setReadOnly(true);
			if (isComplexType(annotation.valueType()) || annotation.polymorph()) {
				for (final Object value : map.values()) {
					handleComplexType(value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleReadOnlySetProperty(
		final Object referenceConfig,
		final PropertyDescriptor propertyDescriptor,
		final ConfigSetProperty annotation) {

		final Set<Object> set = (Set<Object>) propertyAccessor.read(referenceConfig, propertyDescriptor.getName());
		if (set != null) {
			final ConfigProxy<?> proxy = (ConfigProxy<?>) set;
			proxy.setReadOnly(true);
			if (isComplexType(annotation.itemType()) || annotation.polymorph()) {
				for (final Object item : set) {
					handleReadOnlyComplexType(item);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleReadOnlyListProperty(
		final Object referenceConfig,
		final PropertyDescriptor propertyDescriptor,
		final ConfigListProperty annotation) {

		final List<Object> list = (List<Object>) propertyAccessor.read(referenceConfig, propertyDescriptor.getName());
		if (list != null) {
			final ConfigProxy<?> proxy = (ConfigProxy<?>) list;
			proxy.setReadOnly(true);
			if (isComplexType(annotation.itemType()) || annotation.polymorph()) {
				for (final Object item : list) {
					handleReadOnlyComplexType(item);
				}
			}
		}
	}

	private void handleReadOnlyArrayProperty(
		final Object referenceConfig,
		final PropertyDescriptor propertyDescriptor,
		final ConfigArrayProperty annotation) {
		final Class<?> itemType = propertyDescriptor.getPropertyType().getComponentType();
		if (isComplexType(itemType) || annotation.polymorph()) {
			final Object array = propertyAccessor.read(referenceConfig, propertyDescriptor.getName());
			if (array != null) {
				for (int i = 0; i < Array.getLength(array); i++) {
					final Object current = Array.get(array, i);
					handleReadOnlyComplexType(current);
				}
			}
		}
	}

	private void handleArrayProperty(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigArrayProperty annotation) {

		final Class<?> itemType = propertyDescriptor.getPropertyType().getComponentType();
		if (isComplexType(itemType) || annotation.polymorph()) {
			final Object array = propertyAccessor.read(config, propertyDescriptor.getName());
			if (array != null) {
				for (int i = 0; i < Array.getLength(array); i++) {
					final Object current = Array.get(array, i);
					handleComplexType(current);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleMapProperty(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigMapProperty annotation) {

		if (isComplexType(annotation.valueType()) || annotation.polymorph()) {
			final Map<Object, Object> map = (Map<Object, Object>) propertyAccessor.read(config, propertyDescriptor.getName());
			if (map != null) {
				for (final Object value : map.values()) {
					handleComplexType(value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleSetProperty(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigSetProperty annotation) {

		if (isComplexType(annotation.itemType()) || annotation.polymorph()) {
			final Set<Object> set = (Set<Object>) propertyAccessor.read(config, propertyDescriptor.getName());
			if (set != null) {
				for (final Object item : set) {
					handleComplexType(item);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleListProperty(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigListProperty annotation) {

		if (isComplexType(annotation.itemType()) || annotation.polymorph()) {
			final List<Object> list = (List<Object>) propertyAccessor.read(config, propertyDescriptor.getName());
			if (list != null) {
				for (final Object item : list) {
					handleComplexType(item);
				}
			}
		}
	}

	private boolean isComplexType(final Class<?> type) {
		return AnnotationUtil.getAnnotation(type, ConfigComplexType.class) != null;
	}

	private ScopePath getScope(final Class<?> configClass, final ConfigCrossReference crossReferenceAnnotation) {
		final ScopePathBuilderFactory scopeFactory = configService.getScopePathBuilderFactory(configClass);
		ScopePathBuilder builder;
		if (crossReferenceAnnotation.scopePath().length > 0) {
			builder = scopeFactory.stub().appendAll(crossReferenceAnnotation.scopePath());
		} else {
			builder = scopeFactory.annotatedPath();
		}
		if (!crossReferenceAnnotation.instanceName().isEmpty()) {
			builder.addPropertyToScope(
					InstanceScopeDescriptor.NAME,
					InstanceScopeDescriptor.PROP_INSTANCE_NAME,
					crossReferenceAnnotation.instanceName());
		}
		return builder.create();
	}
}
