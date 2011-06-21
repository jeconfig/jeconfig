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

package org.jeconfig.client.internal;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;

import org.jeconfig.api.annotation.ConfigIdProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.ClassInstantiation;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ConfigIdPropertyUtil {
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();

	public String findIdPropertyName(final Class<?> configClass) {
		for (final PropertyDescriptor propDesc : propertyAccessor.getPropertyDescriptors(ProxyUtil.getConfigClass(configClass))) {
			if (propDesc.getReadMethod() != null) {
				for (final Annotation annotation : propDesc.getReadMethod().getAnnotations()) {
					if (ConfigIdProperty.class.equals(annotation.annotationType())) {
						return propDesc.getName();
					}
				}
			}
		}
		return null;
	}

	public String getIdPropertyName(final Class<?> configClass) {
		final String result = findIdPropertyName(configClass);

		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Didn't find ID property in type '" + configClass + "'!"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public String getIdPropertyValueAsString(final Object config, final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		final String idPropertyName = getIdPropertyName(config.getClass());
		return getIdPropertyValueAsString(config, idPropertyName, simpleTypeConverterRegistry);
	}

	@SuppressWarnings("unchecked")
	public String getIdPropertyValueAsString(
		final Object config,
		final String idPropertyName,
		final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {

		if (config != null) {
			try {
				final PropertyDescriptor desc = new PropertyDescriptor(
					idPropertyName,
					ProxyUtil.getConfigClass(config.getClass()));
				final ConfigSimpleProperty annotation = desc.getReadMethod().getAnnotation(ConfigSimpleProperty.class);
				final SimpleTypeConverter<Object> customIdConverter = (SimpleTypeConverter<Object>) createCustomConverter(annotation.customConverter());
				final Object obj = propertyAccessor.read(config, idPropertyName);
				if (obj == null) {
					throw new IllegalArgumentException("Got config which has no ID set: " + config); //$NON-NLS-1$
				}
				if (customIdConverter != null) {
					return customIdConverter.convertToSerializedForm(obj);
				} else {
					if (!simpleTypeConverterRegistry.isTypeSupported(obj.getClass())) {
						throw new RuntimeException("Didn't find converter for simple type: " + obj.getClass()); //$NON-NLS-1$
					}
					return simpleTypeConverterRegistry.convertToSerializedForm(obj);
				}
			} catch (final IntrospectionException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	private SimpleTypeConverter<?> createCustomConverter(final Class<? extends SimpleTypeConverter<?>> customConverter) {
		if (NoCustomSimpleTypeConverter.class.equals(customConverter)) {
			return null;
		}
		return new ClassInstantiation().newInstance(customConverter);
	}
}
