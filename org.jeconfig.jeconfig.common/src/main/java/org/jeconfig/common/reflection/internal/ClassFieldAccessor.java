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

package org.jeconfig.common.reflection.internal;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.api.util.Assert;

/**
 * This class is thread-safe.
 * 
 * @param <T>
 */
public class ClassFieldAccessor<T> {
	/** The cached single property readers. */
	private final ConcurrentHashMap<String, Field> fields;

	/** The persistence class of this reader. */
	private final Class<T> beanClass;

	/**
	 * Creates a new ClassFieldAccessor.
	 * 
	 * @param beanClass
	 */
	public ClassFieldAccessor(final Class<T> beanClass) {
		Assert.paramNotNull(beanClass, "beanClass"); //$NON-NLS-1$
		this.beanClass = beanClass;

		fields = new ConcurrentHashMap<String, Field>();
	}

	/**
	 * @param fieldName
	 * @return the field with the given name
	 */
	public Field getField(final String fieldName) {
		Field result = fields.get(fieldName);
		if (result == null) {
			try {
				result = beanClass.getDeclaredField(fieldName);
				result.setAccessible(true);
				fields.putIfAbsent(fieldName, result);
				return fields.get(fieldName);
			} catch (final NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}

		return result;
	}

	/**
	 * Reads the specified field of the specified bean which must be instance
	 * of the persistenceClass of this ClassFieldAccessor.
	 * 
	 * @param fieldName
	 * @param bean
	 * @return the property of the specified bean
	 */
	public Object read(final T bean, final String fieldName) {
		try {
			return getField(fieldName).get(bean);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes the specified field of the specified bean.
	 * 
	 * @param bean
	 * @param fieldName
	 * @param param
	 */
	public void write(final T bean, final String fieldName, final Object param) {
		try {
			getField(fieldName).set(bean, param);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the type of the specified field.
	 * 
	 * @param fieldName
	 * @return the type of the specified field
	 */
	public Class<?> getFieldType(final String fieldName) {
		return getField(fieldName).getType();
	}
}
