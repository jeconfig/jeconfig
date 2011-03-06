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

package org.jeconfig.common.reflection;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.common.reflection.internal.ClassFieldAccessor;

/**
 * This class is thread-safe.
 */
public final class FieldAccessor {
	/** The cached class field readers. Synchronized. */
	private final ConcurrentHashMap<Class<?>, ClassFieldAccessor<?>> accessors;

	public FieldAccessor() {
		accessors = new ConcurrentHashMap<Class<?>, ClassFieldAccessor<?>>();
	}

	/**
	 * Reads the specified field of the specified bean.
	 * 
	 * @param bean
	 * @param fieldName
	 * @return the specified field of the specified bean
	 */
	public Object read(final Object bean, final String fieldName) {
		return getAccessor(bean.getClass()).read(bean, fieldName);
	}

	/**
	 * Writes the specified field of the specified bean.
	 * 
	 * @param bean
	 * @param fieldName
	 * @param param
	 */
	public void write(final Object bean, final String fieldName, final Object param) {
		getAccessor(bean.getClass()).write(bean, fieldName, param);
	}

	/**
	 * Returns the type of the specified field of the specified class.
	 * 
	 * @param beanClass
	 * @param fieldName
	 * @return the type of the specified field of the specified class
	 */
	public Class<?> getFieldType(final Class<?> beanClass, final String fieldName) {
		return getAccessor(beanClass).getFieldType(fieldName);
	}

	/**
	 * Returns the method to read the specified field of the given class.
	 * 
	 * @param beanClass
	 * @param fieldName
	 * @return the getter to read the specified field
	 */
	public Field getField(final Class<?> beanClass, final String fieldName) {
		return getAccessor(beanClass).getField(fieldName);
	}

	@SuppressWarnings("unchecked")
	private ClassFieldAccessor<Object> getAccessor(final Class<?> beanClass) {
		ClassFieldAccessor<?> accessor = accessors.get(beanClass);
		if (accessor == null) {
			accessor = new ClassFieldAccessor<Object>((Class<Object>) beanClass);
			accessors.putIfAbsent(beanClass, accessor);

			// request the accessor again to ensure that only the instance in the map is used
			return (ClassFieldAccessor<Object>) accessors.get(beanClass);
		}

		return (ClassFieldAccessor<Object>) accessor;
	}
}
