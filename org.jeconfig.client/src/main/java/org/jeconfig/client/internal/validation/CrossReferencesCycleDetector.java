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

package org.jeconfig.client.internal.validation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jeconfig.api.IConfigService;
import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilder;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

/**
 * Detects cycles of cross references in configuration classes.
 * 
 * Note that cycles caused by polymorph properties are not detected!
 */
public final class CrossReferencesCycleDetector {
	private final IConfigService configService;
	private final PropertyAccessor propertyAccessor;

	public CrossReferencesCycleDetector(final IConfigService configService) {
		this.configService = configService;
		propertyAccessor = new PropertyAccessor();
	}

	public void detectCycles(final Class<?> configClass, final IScopePath scope) {
		final List<IScopePath> usedScopes = new ArrayList<IScopePath>();
		usedScopes.add(scope);
		final Set<Class<?>> checkedTypes = new HashSet<Class<?>>();
		detectCycles(configClass, usedScopes, checkedTypes);
	}

	private void detectCycles(final Class<?> type, final List<IScopePath> usedScopes, final Set<Class<?>> checkedTypes) {
		final Class<?> configClass = ProxyUtil.getConfigClass(type);
		if (checkedTypes.contains(configClass)) {
			return;
		}
		checkedTypes.add(configClass);

		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(configClass)) {
			if (propertyDescriptor.getReadMethod() != null) {
				for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
					if (ConfigCrossReference.class.equals(annotation.annotationType())) {
						handleCrossReference(
								propertyDescriptor.getPropertyType(),
								(ConfigCrossReference) annotation,
								usedScopes,
								checkedTypes);
					} else if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
						handleComplexType(propertyDescriptor, (ConfigComplexProperty) annotation, usedScopes, checkedTypes);
					} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
						handleArrayProperty(propertyDescriptor, (ConfigArrayProperty) annotation, usedScopes, checkedTypes);
					} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
						handleListProperty(propertyDescriptor, (ConfigListProperty) annotation, usedScopes, checkedTypes);
					} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
						handleSetProperty(propertyDescriptor, (ConfigSetProperty) annotation, usedScopes, checkedTypes);
					} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
						handleMapProperty(propertyDescriptor, (ConfigMapProperty) annotation, usedScopes, checkedTypes);
					}
				}
			}
		}
	}

	private void handleMapProperty(
		final PropertyDescriptor propertyDescriptor,
		final ConfigMapProperty annotation,
		final List<IScopePath> usedScopes,
		final Set<Class<?>> checkedTypes) {

		if (isComplexType(annotation.valueType())) {
			detectCycles(annotation.valueType(), usedScopes, checkedTypes);
		}
	}

	private void handleSetProperty(
		final PropertyDescriptor propertyDescriptor,
		final ConfigSetProperty annotation,
		final List<IScopePath> usedScopes,
		final Set<Class<?>> checkedTypes) {

		if (isComplexType(annotation.itemType())) {
			detectCycles(annotation.itemType(), usedScopes, checkedTypes);
		}
	}

	private void handleListProperty(
		final PropertyDescriptor propertyDescriptor,
		final ConfigListProperty annotation,
		final List<IScopePath> usedScopes,
		final Set<Class<?>> checkedTypes) {

		if (isComplexType(annotation.itemType())) {
			detectCycles(annotation.itemType(), usedScopes, checkedTypes);
		}
	}

	private void handleArrayProperty(
		final PropertyDescriptor propertyDescriptor,
		final ConfigArrayProperty annotation,
		final List<IScopePath> usedScopes,
		final Set<Class<?>> checkedTypes) {

		final Class<?> itemType = propertyDescriptor.getPropertyType().getComponentType();
		if (isComplexType(itemType)) {
			detectCycles(itemType, usedScopes, checkedTypes);
		}
	}

	private void handleComplexType(
		final PropertyDescriptor propertyDescriptor,
		final ConfigComplexProperty annotation,
		final List<IScopePath> usedScopes,
		final Set<Class<?>> checkedTypes) {

		if (isComplexType(propertyDescriptor.getPropertyType())) {
			detectCycles(propertyDescriptor.getPropertyType(), usedScopes, checkedTypes);
		}
	}

	private void handleCrossReference(
		final Class<?> crossReferenceType,
		final ConfigCrossReference annotation,
		final List<IScopePath> usedScopes,
		final Set<Class<?>> checkedTypes) {
		final IScopePath scopeOfReference = getScope(crossReferenceType, annotation);
		if (usedScopes.contains(scopeOfReference)) {
			throw new IllegalArgumentException("Detected cross references cycle:\n" //$NON-NLS-1$
				+ getCycleString(usedScopes, scopeOfReference));
		}

		final List<IScopePath> usedScopesForReference = new ArrayList<IScopePath>(usedScopes);
		usedScopesForReference.add(scopeOfReference);

		detectCycles(crossReferenceType, usedScopesForReference, checkedTypes);
	}

	private String getCycleString(final List<IScopePath> usedScopes, final IScopePath nextScope) {
		final StringBuilder sb = new StringBuilder();

		for (final IScopePath scope : usedScopes) {
			sb.append(scope).append("\n --> "); //$NON-NLS-1$
		}
		sb.append(nextScope);

		return sb.toString();
	}

	private IScopePath getScope(final Class<?> configClass, final ConfigCrossReference crossReferenceAnnotation) {
		final IScopePathBuilderFactory scopeFactory = configService.getScopePathBuilderFactory(configClass);
		IScopePathBuilder builder;
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

	private boolean isComplexType(final Class<?> type) {
		return AnnotationUtil.getAnnotation(type, ConfigComplexType.class) != null;
	}
}
