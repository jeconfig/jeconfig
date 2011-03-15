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

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;

@SuppressWarnings("nls")
public final class CrossReferencePropertyValidator implements IPropertyValidator<ConfigCrossReference> {

	@Override
	public Set<Class<? extends Annotation>> getCompatibleAnnotations() {
		return Collections.emptySet();
	}

	@Override
	public void validate(
		final Object config,
		final PropertyDescriptor propertyDescriptor,
		final ConfigCrossReference annotation,
		final Set<Annotation> otherAnnotations,
		final ISimpleTypeConverterRegistry converterRegistry,
		final Map<Class<? extends Annotation>, IPropertyValidator<Annotation>> validators,
		final ComplexTypeValidator complexTypeValidator,
		final Set<Class<?>> validatedComplexTypes) {

		final Class<?> crossReferenceConfigClass = ProxyUtil.getConfigClass(propertyDescriptor.getPropertyType());

		final ConfigClass configClassAnnotation = AnnotationUtil.getAnnotation(crossReferenceConfigClass, ConfigClass.class);
		if (configClassAnnotation == null) {
			throw new IllegalArgumentException("The property '"
				+ propertyDescriptor.getName()
				+ "' has a @"
				+ ConfigCrossReference.class.getSimpleName()
				+ " annotation. This annotation is only allowed on fields with types which are annotated with the @"
				+ ConfigClass.class.getSimpleName()
				+ "' annotation!'");

		}
	}
}
