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
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

@SuppressWarnings("nls")
public final class ComplexPropertyValidator implements IPropertyValidator<ConfigComplexProperty> {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();

	@Override
	public Set<Class<? extends Annotation>> getCompatibleAnnotations() {
		return Collections.emptySet();
	}

	@Override
	public void validate(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigComplexProperty annotation,
		final Set<Annotation> otherAnnotations,
		final ISimpleTypeConverterRegistry converterRegistry,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators,
		final ComplexTypeValidator complexTypeValidator,
		final Set<Class<?>> validatedComplexTypes) {

		if (!annotation.polymorph()) {
			final ConfigComplexType complexTypeAnno = AnnotationUtil.getAnnotation(
					propertyDescriptor.getPropertyType(),
					ConfigComplexType.class);
			if (complexTypeAnno == null) {
				throw new IllegalArgumentException("The type '"
					+ propertyDescriptor.getPropertyType()
					+ "' of the non-polymorph propoperty '"
					+ propertyDescriptor.getName()
					+ "' must not be annotated with @"
					+ ConfigComplexType.class.getSimpleName()
					+ "!");
			}
			complexTypeValidator.validate(
					propertyDescriptor.getPropertyType(),
					null,
					validators,
					converterRegistry,
					validatedComplexTypes);
		}

		if (config == null) {
			return;
		}

		final Object actualValue = propertyAccessor.read(config, propertyDescriptor.getName());
		if (actualValue != null) {
			final Class<?> actualType = ProxyUtil.getConfigClass(actualValue.getClass());

			if (annotation.polymorph()) {
				if (AnnotationUtil.getAnnotation(actualType, ConfigComplexType.class) == null) {
					throw new IllegalArgumentException("The type '"
						+ actualType
						+ "' must be annotated with @"
						+ ConfigComplexType.class.getSimpleName()
						+ "!");
				}
			} else {
				if (!propertyDescriptor.getPropertyType().equals(actualType)) {
					throw new IllegalArgumentException("The value '"
						+ actualValue
						+ " is not of the type '"
						+ propertyDescriptor.getPropertyType()
						+ "'. If you want to use objects of different types on property '"
						+ propertyDescriptor.getName()
						+ "', set 'polymorph' to 'true'!");
				}
			}
		}
	}

}
