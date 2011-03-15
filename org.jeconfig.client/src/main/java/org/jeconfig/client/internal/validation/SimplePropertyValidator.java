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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigIdProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;

public final class SimplePropertyValidator implements IPropertyValidator<ConfigSimpleProperty> {
	private static Set<Class<? extends Annotation>> compatibleAnnotations = createCompatibleAnnotations();

	private static Set<Class<? extends Annotation>> createCompatibleAnnotations() {
		final Set<Class<? extends Annotation>> result = new HashSet<Class<? extends Annotation>>();
		result.add(ConfigIdProperty.class);
		return result;
	}

	@Override
	public Set<Class<? extends Annotation>> getCompatibleAnnotations() {
		return compatibleAnnotations;
	}

	@Override
	public void validate(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigSimpleProperty annotation,
		final Set<Annotation> otherAnnotations,
		final ISimpleTypeConverterRegistry converterRegistry,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators,
		final ComplexTypeValidator complexTypeValidator,
		final Set<Class<?>> validatedComplexTypes) {

		if (annotation.customConverter() == NoCustomSimpleTypeConverter.class
			&& !converterRegistry.isTypeSupported(propertyDescriptor.getPropertyType())) {
			throw new IllegalArgumentException(
				"The type '" //$NON-NLS-1$
					+ propertyDescriptor.getPropertyType()
					+ "' is not supported for simple config fields. Define a customConverter or Register a custom simple type converter to use it!"); //$NON-NLS-1$
		}
	}

}
