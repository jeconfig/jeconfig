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
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

@SuppressWarnings("nls")
public final class ArrayPropertyValidator implements IPropertyValidator<ConfigArrayProperty> {
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
		final ConfigArrayProperty annotation,
		final Set<Annotation> otherAnnotations,
		final ISimpleTypeConverterRegistry converterRegistry,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators,
		final ComplexTypeValidator complexTypeValidator) {

		if (!propertyDescriptor.getPropertyType().isArray()) {
			throw new IllegalArgumentException("The property '"
				+ propertyDescriptor.getName()
				+ "' is annotated as an array but is no array!");
		}

		final Class<?> itemType = propertyDescriptor.getPropertyType().getComponentType();
		if (itemType.isPrimitive() && annotation.polymorph()) {
			throw new IllegalArgumentException("'polymorph'='true' is not supported for primitive types!");
		}

		if (propertyDescriptor.getWriteMethod() == null) {
			throw new IllegalArgumentException("Didn't find a setter for the property '"
				+ propertyDescriptor.getName()
				+ "'. You should have one because changes of the array cannot be tracked. "
				+ "Arrays must not be modified but exchanged using the setter.");
		}

		if (!annotation.polymorph()) {
			final ConfigComplexType complexTypeAnnotation = AnnotationUtil.getAnnotation(itemType, ConfigComplexType.class);
			if (complexTypeAnnotation == null) {
				if (annotation.customConverter() == NoCustomSimpleTypeConverter.class
					&& !converterRegistry.isTypeSupported(itemType)) {
					throw new IllegalArgumentException("The type '"
						+ itemType
						+ "' is unknown. Register a simple type converter for it, "
						+ "' annotate it with @"
						+ ConfigComplexType.class.getSimpleName()
						+ " or make it polymorph!");
				}
			} else {
				complexTypeValidator.validate(itemType, null, validators, converterRegistry);
			}
		}

		if (config == null) {
			return;
		}

		final Object array = propertyAccessor.read(config, propertyDescriptor.getName());
		if (array != null && !itemType.isPrimitive()) {
			for (int i = 0; i < Array.getLength(array); i++) {
				final Object object = Array.get(array, i);

				collectionElementValidator.validate(
						object,
						itemType,
						annotation.polymorph(),
						"array",
						converterRegistry,
						validators,
						complexTypeValidator);
			}
		}
	}
}
