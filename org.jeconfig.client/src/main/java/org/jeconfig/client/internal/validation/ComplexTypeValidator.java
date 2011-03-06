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
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ConfigProxyFactory;
import org.jeconfig.client.proxy.IConfigObjectFactory;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.ClassInstantiation;
import org.jeconfig.common.reflection.PropertyAccessor;

@SuppressWarnings("nls")
public final class ComplexTypeValidator {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final IConfigObjectFactory configObjectFactory;

	public ComplexTypeValidator(final ISimpleTypeConverterRegistry converterRegistry) {
		configObjectFactory = new ConfigProxyFactory(converterRegistry);
	}

	public void validate(
		final Class<?> type,
		final Object complexConfig,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators,
		final ISimpleTypeConverterRegistry converterRegistry) {

		if ((type.getModifiers() & Modifier.FINAL) != 0) {
			throw new IllegalArgumentException("The configuration class '" + type + "' must not be final!");
		}

		if ((type.getModifiers() & Modifier.PUBLIC) == 0) {
			throw new IllegalArgumentException("The configuration class '" + type + "' must be public!");
		}

		if ((type.getEnclosingClass() != null) && (type.getModifiers() & Modifier.STATIC) == 0) {
			throw new IllegalArgumentException("The type '"
				+ type
				+ "' is an inner non-static class. It can't be used as a configuration class!");
		}

		checkDefaultConstructor(type);
		checkEquals(type);

		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(ProxyUtil.getConfigClass(type))) {
			final Set<Annotation> propertyAnnotations = getConfigAnnotations(propertyDescriptor, validators);
			for (final Annotation annotation : propertyAnnotations) {
				final IPropertyValidator<Annotation> validator = validators.get(annotation.annotationType());
				final Set<Annotation> otherAnnotations = new HashSet<Annotation>(propertyAnnotations);
				otherAnnotations.remove(annotation);
				validateCompatibleAnnotations(annotation, otherAnnotations, validator);
				validator.validate(
						complexConfig,
						propertyDescriptor,
						annotation,
						otherAnnotations,
						converterRegistry,
						validators,
						this);
			}
		}
	}

	private void checkDefaultConstructor(final Class<?> type) {
		try {
			type.getConstructor();
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final NoSuchMethodException e) {
			throw new IllegalArgumentException("The configuration class " + type + " must have a public default-constructor!");
		}
	}

	private void checkEquals(final Class<?> type) {
		final Class<?> plainType = ProxyUtil.getConfigClass(type);
		final Object plainInstance = classInstantiation.newInstance(plainType);
		final Object proxyInstance = createProxy(plainType);

		if (plainInstance.hashCode() != proxyInstance.hashCode()) {
			throw new IllegalArgumentException("Either you forgot to implement equals and hashCode() in the type '"
				+ type
				+ "' or hashCode() is illegal.");
		}

		if (!plainInstance.equals(proxyInstance) || !proxyInstance.equals(plainInstance)) {
			throw new IllegalArgumentException(
				"Either you forgot to implement equals() and hashCode() in type '"
					+ type
					+ "' or equals is illegal.\n"
					+ "If you generate hashCode() and equals() with Eclipse, you should check the option 'Use 'instanceof' to compare types'");
		}
	}

	private Object createProxy(final Class<?> type) {
		if (AnnotationUtil.getAnnotation(type, ConfigClass.class) != null) {
			return configObjectFactory.createRootConfigProxy(type, null);
		} else if (AnnotationUtil.getAnnotation(type, ConfigComplexType.class) != null) {
			return configObjectFactory.createComplexProperty(type);
		}
		throw new IllegalArgumentException("Got configuration class without suitable annotation!");
	}

	private void validateCompatibleAnnotations(
		final Annotation mainAnnotation,
		final Set<Annotation> actualAnnotations,
		final IPropertyValidator<Annotation> validator) {
		final Set<Class<? extends Annotation>> compatibleAnnotations = validator.getCompatibleAnnotations();
		if (compatibleAnnotations != null) {
			for (final Annotation annotation : actualAnnotations) {
				if (!compatibleAnnotations.contains(annotation.annotationType())) {
					throw new IllegalArgumentException("The annotation '@"
						+ annotation.annotationType().getSimpleName()
						+ "' is not compatible with '@"
						+ mainAnnotation.annotationType().getSimpleName()
						+ "'!");
				}
			}
		}
		// else: validator's main annotation is compatible with all annotations
	}

	private Set<Annotation> getConfigAnnotations(
		final PropertyDescriptor propertyDescriptor,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators) {
		final Set<Annotation> result = new HashSet<Annotation>();

		if (propertyDescriptor.getReadMethod() != null) {
			for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
				if (validators.containsKey(annotation.annotationType())) {
					result.add(annotation);
				}
			}
		}

		return result;
	}
}
