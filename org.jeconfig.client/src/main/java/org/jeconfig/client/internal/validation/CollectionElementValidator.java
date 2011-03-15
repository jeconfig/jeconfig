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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;

@SuppressWarnings("nls")
public final class CollectionElementValidator {

	protected void validate(
		final Object object,
		final Class<?> annotatedItemType,
		final boolean polymorph,
		final String collectionName,
		final ISimpleTypeConverterRegistry converterRegistry,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators,
		final ComplexTypeValidator complexTypeValidator,
		final Set<Class<?>> validatedComplexTypes) {
		if (object != null) {
			if (annotatedItemType.isPrimitive()) {
				if (!getWrapper(annotatedItemType).isAssignableFrom(object.getClass())) {
					throw new IllegalArgumentException("The "
						+ collectionName
						+ " item '"
						+ object
						+ "' is not compatible with the annotated item type '"
						+ annotatedItemType
						+ "'!");
				}
			} else {
				if (!annotatedItemType.isAssignableFrom(object.getClass())) {
					throw new IllegalArgumentException("The "
						+ collectionName
						+ " item '"
						+ object
						+ "' is not compatible with the annotated item type '"
						+ annotatedItemType
						+ "'!");
				}

				if (polymorph) {
					final ConfigComplexType complexTypeAnnotation = AnnotationUtil.getAnnotation(
							object.getClass(),
							ConfigComplexType.class);
					if (complexTypeAnnotation == null) {
						throw new IllegalArgumentException("The type of the "
							+ collectionName
							+ " item '"
							+ object
							+ "' must be annotated with @"
							+ ConfigComplexType.class.getSimpleName()
							+ "!");
					}

					// check whether default-constructor exists
					try {
						object.getClass().getConstructor();
					} catch (final Exception e) {
						throw new IllegalArgumentException("The class '"
							+ object.getClass()
							+ "' must have a default constructor!");
					}

					complexTypeValidator.validate(object.getClass(), null, validators, converterRegistry, validatedComplexTypes);
				} else {
					checkNonPolymorphClassCompatibility(annotatedItemType, object, collectionName);
				}
			}
		}
	}

	private void checkNonPolymorphClassCompatibility(final Class<?> itemType, final Object object, final String collectionName) {
		if (!itemType.equals(ProxyUtil.getConfigClass(object.getClass()))) {
			throw new IllegalArgumentException("The "
				+ collectionName
				+ " item '"
				+ object
				+ "' is not of the annotated item type '"
				+ itemType
				+ "'. If you want to use objects of different types in your "
				+ collectionName
				+ ", set 'polymorph' to 'true'!");
		}
	}

	private Class<?> getWrapper(final Class<?> primitiveType) {
		if (int.class.equals(primitiveType)) {
			return Integer.class;
		}
		if (long.class.equals(primitiveType)) {
			return Long.class;
		}
		if (double.class.equals(primitiveType)) {
			return Double.class;
		}
		if (float.class.equals(primitiveType)) {
			return Float.class;
		}
		if (short.class.equals(primitiveType)) {
			return Short.class;
		}
		if (byte.class.equals(primitiveType)) {
			return Byte.class;
		}
		if (boolean.class.equals(primitiveType)) {
			return Boolean.class;
		}
		throw new IllegalArgumentException("Didn't find wrapper for primitive type: " + primitiveType);
	}
}
