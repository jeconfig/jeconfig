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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigIdProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

@SuppressWarnings("nls")
public final class SetPropertyValidator implements PropertyValidator<ConfigSetProperty> {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();
	private final CollectionElementValidator collectionElementValidator = new CollectionElementValidator();

	@Override
	public Set<Class<? extends Annotation>> getCompatibleAnnotations() {
		return Collections.emptySet();
	}

	@Override
	public void validate(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigSetProperty annotation,
		final Set<Annotation> otherAnnotations,
		final SimpleTypeConverterRegistry converterRegistry,
		final Map<Class<? extends Annotation>, PropertyValidator<Annotation>> validators,
		final ComplexTypeValidator complexTypeValidator,
		final Set<Class<?>> validatedComplexTypes) {

		if (annotation.itemType().isPrimitive() && annotation.polymorph()) {
			throw new IllegalArgumentException("'polymorph'='true' is not supported for primitive types!");
		}

		final ConfigComplexType complexTypeAnnotation = AnnotationUtil.getAnnotation(
				annotation.itemType(),
				ConfigComplexType.class);
		if (!annotation.polymorph()) {
			if (complexTypeAnnotation == null
				&& annotation.customConverter() == NoCustomSimpleTypeConverter.class
				&& !converterRegistry.isTypeSupported(annotation.itemType())) {
				throw new IllegalArgumentException("The type '"
					+ annotation.itemType()
					+ "' of the property '"
					+ propertyDescriptor.getName()
					+ "' is unknown. Register a simple type converter for it,"
					+ " annotate it with @"
					+ ConfigComplexType.class.getSimpleName()
					+ " or make the set polymorph!");
			} else if (complexTypeAnnotation != null) {
				complexTypeValidator.validate(annotation.itemType(), null, validators, converterRegistry, validatedComplexTypes);

				if (getIdProperty(annotation.itemType()) == null) {
					throw new IllegalArgumentException("The class '"
						+ annotation.itemType()
						+ "' must have an config ID property.");
				}
			}
		}

		if (config == null) {
			return;
		}

		final Set<?> set = (Set<?>) propertyAccessor.read(config, propertyDescriptor.getName());
		if (set != null) {
			for (final Object object : set) {
				collectionElementValidator.validate(
						object,
						annotation.itemType(),
						annotation.polymorph(),
						"set",
						converterRegistry,
						validators,
						complexTypeValidator,
						validatedComplexTypes);
			}

			if (complexTypeAnnotation != null && !annotation.polymorph()) {
				ensureIdUniueness(propertyDescriptor.getName(), set, getIdProperty(annotation.itemType()));
			}
		}
	}

	private void ensureIdUniueness(final String setPropertyName, final Set<?> set, final String idPropertyName) {
		final Set<Object> ids = new HashSet<Object>();
		for (final Object object : set) {
			if (object != null) {
				final Object id = propertyAccessor.read(object, idPropertyName);
				if (ids.contains(id)) {
					throw new IllegalArgumentException("More than one items of the set '"
						+ setPropertyName
						+ "' have the id '"
						+ id
						+ "'!");
				}
				ids.add(id);
			}
		}
	}

	public String getIdProperty(final Class<?> configClass) {
		final Class<?> type = ProxyUtil.getConfigClass(configClass);

		String result = null;
		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(type)) {
			if (isIdProperty(propertyDescriptor)) {
				if (result != null) {
					throw new IllegalArgumentException("The class '"
						+ type
						+ "' has more than one ID properties which is not allowed.");
				}

				result = propertyDescriptor.getName();
			}
		}

		return result;
	}

	private boolean isIdProperty(final PropertyDescriptor propertyDescriptor) {
		for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
			if (ConfigIdProperty.class.equals(annotation.annotationType())) {
				return true;
			}
		}

		return false;
	}
}
