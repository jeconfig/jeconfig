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

package org.jeconfig.client.internal.merging;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

public final class ComplexTypeMerger {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();

	public ComplexConfigDTO merge(
		final ComplexConfigDTO parent,
		final ComplexConfigDTO child,
		final Class<?> configClass,
		final Map<Class<? extends Annotation>, IPropertyMerger> mergers,
		final StalePropertiesMergingResultImpl mergingResult) {

		if (child == null) {
			return parent;
		}
		if (parent == null) {
			return child;
		}

		if (!parent.getPropertyType().equals(child.getPropertyType())) {
			throw new IllegalArgumentException("Cannot merge instances of different types"); //$NON-NLS-1$
		}

		final ComplexConfigDTO result = child.flatCopy();

		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(ProxyUtil.getConfigClass(configClass))) {
			if (propertyDescriptor.getReadMethod() != null) {
				for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
					final IPropertyMerger merger = mergers.get(annotation.annotationType());
					if (merger != null) {
						merger.merge(result, parent, child, propertyDescriptor, mergers, this, mergingResult);
						break;
					}
				}
			}
		}

		return result;
	}
}
