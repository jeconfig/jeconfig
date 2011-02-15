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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.common.reflection.internal.ClassPropertyAccessor;

/**
 * This class is thread-safe.
 */
public final class PropertyAccessor {
	/** The cached class property readers. Synchronized. */
	private final ConcurrentHashMap<Class<?>, ClassPropertyAccessor<?>> accessors;

	public PropertyAccessor() {
		accessors = new ConcurrentHashMap<Class<?>, ClassPropertyAccessor<?>>();
	}

	/**
	 * Reads the specified property of the specified bean.
	 * 
	 * @param bean
	 * @param propertyName
	 * @return the specified property of the specified bean
	 */
	public Object read(final Object bean, final String propertyName) {
		return getAccessor(bean.getClass()).read(bean, propertyName);
	}

	/**
	 * Writes the specified property of the specified bean.
	 * 
	 * @param bean
	 * @param propertyName
	 * @param param
	 */
	public void write(final Object bean, final String propertyName, final Object param) {
		getAccessor(bean.getClass()).write(bean, propertyName, param);
	}

	/**
	 * Returns the type of the specified property of the specified class.
	 * 
	 * @param beanClass
	 * @param propertyName
	 * @return the type of the specified property of the specified class
	 */
	public Class<?> getPropertyType(final Class<?> beanClass, final String propertyName) {
		return getAccessor(beanClass).getPropertyType(propertyName);
	}

	/**
	 * Returns the method to read the specified property of the given class.
	 * 
	 * @param beanClass
	 * @param propertyName
	 * @return the getter to read the specified property
	 */
	public Method getReadMethod(final Class<?> beanClass, final String propertyName) {
		return getAccessor(beanClass).getReadMethod(propertyName);
	}

	/**
	 * Returns the method to write the specified property of the given class.
	 * 
	 * @param beanClass
	 * @param propertyName
	 * @return the setter to write the specified property
	 */
	public Method getWriteMethod(final Class<?> beanClass, final String propertyName) {
		return getAccessor(beanClass).getWriteMethod(propertyName);
	}

	@SuppressWarnings("unchecked")
	private ClassPropertyAccessor<Object> getAccessor(final Class<?> beanClass) {
		ClassPropertyAccessor<?> accessor = accessors.get(beanClass);
		if (accessor == null) {
			accessor = new ClassPropertyAccessor<Object>((Class<Object>) beanClass);
			accessors.putIfAbsent(beanClass, accessor);

			// request the accessor again to ensure that only the instance in the map is used
			return (ClassPropertyAccessor<Object>) accessors.get(beanClass);
		}

		return (ClassPropertyAccessor<Object>) accessor;
	}

	/**
	 * @param beanClass
	 * @return the descriptors of all properties of the given bean class
	 */
	public Collection<PropertyDescriptor> getPropertyDescriptors(final Class<?> beanClass) {
		return getAccessor(beanClass).getPropertyDescriptors();
	}
}
