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

package org.jeconfig.client.internal.conversion;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.util.Assert;

public final class SimpleTypeConverterRegistryImpl implements SimpleTypeConverterRegistry {
	private final ConcurrentHashMap<Class<?>, SimpleTypeConverter<?>> converters;

	public SimpleTypeConverterRegistryImpl() {
		converters = new ConcurrentHashMap<Class<?>, SimpleTypeConverter<?>>();
		new DefaultConverterFactory().createConverters(this);
	}

	@Override
	public boolean isTypeSupported(final Class<?> type) {
		return getConverter(type) != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> SimpleTypeConverter<T> getConverter(final Class<T> simpleType) {
		Assert.paramNotNull(simpleType, "simpleType"); //$NON-NLS-1$

		for (final Entry<Class<?>, SimpleTypeConverter<?>> entry : converters.entrySet()) {
			if (entry.getKey().isAssignableFrom(simpleType)) {
				return (SimpleTypeConverter<T>) entry.getValue();
			}
		}
		return null;
	}

	@Override
	public <T> void addConverter(final Class<T> simpleType, final SimpleTypeConverter<T> converter) {
		Assert.paramNotNull(simpleType, "simpleType"); //$NON-NLS-1$
		Assert.paramNotNull(converter, "converter"); //$NON-NLS-1$

		converters.put(simpleType, converter);
	}

	@Override
	public <T> void removeConverter(final Class<T> simpleType, final SimpleTypeConverter<T> converter) {
		Assert.paramNotNull(simpleType, "simpleType"); //$NON-NLS-1$
		Assert.paramNotNull(converter, "converter"); //$NON-NLS-1$

		converters.remove(simpleType, converter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> String convertToSerializedForm(final T object) {
		if (object == null) {
			return null;
		}

		final SimpleTypeConverter<T> converter = getConverter((Class<T>) object.getClass());
		if (converter != null) {
			return converter.convertToSerializedForm(object);
		}

		throw new IllegalArgumentException("Didn't find converter for type '" + object.getClass() + "'!"); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public <T> T convertToObject(final Class<T> objectClass, final String serializedForm) {
		final SimpleTypeConverter<T> converter = getConverter(objectClass);
		if (converter != null) {
			return converter.convertToObject(objectClass, serializedForm);
		}

		throw new IllegalArgumentException("Didn't find converter for type '" + objectClass + "'!"); //$NON-NLS-1$//$NON-NLS-2$
	}
}
