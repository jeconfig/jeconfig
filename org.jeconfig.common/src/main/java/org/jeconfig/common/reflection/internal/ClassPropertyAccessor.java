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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.api.util.Assert;

/**
 * This class is thread-safe.
 * 
 * @param <T>
 */
public class ClassPropertyAccessor<T> {
	/** The cached single property descriptors. */
	private final ConcurrentHashMap<String, PropertyDescriptor> propertyDescriptors;

	/**
	 * Creates a new ClassPropertyReader.
	 * 
	 * @param beanClass
	 */
	public ClassPropertyAccessor(final Class<T> beanClass) {
		Assert.paramNotNull(beanClass, "beanClass"); //$NON-NLS-1$

		propertyDescriptors = new ConcurrentHashMap<String, PropertyDescriptor>();

		try {
			final BeanInfo info = Introspector.getBeanInfo(beanClass);
			for (final PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
				propertyDescriptors.put(propertyDescriptor.getName(), propertyDescriptor);
			}
		} catch (final IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param propertyName
	 * @return the getter to read the specified property
	 */
	public Method getReadMethod(final String propertyName) {
		final PropertyDescriptor propertyDescriptor = propertyDescriptors.get(propertyName);
		if (propertyDescriptor != null && propertyDescriptor.getReadMethod() != null) {
			return propertyDescriptor.getReadMethod();
		}

		throw new IllegalArgumentException("Didn't find read method for property '" + propertyName + "'."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @param propertyName
	 * @return the setter to write the specified property
	 */
	public Method getWriteMethod(final String propertyName) {
		final PropertyDescriptor propertyDescriptor = propertyDescriptors.get(propertyName);
		if (propertyDescriptor != null && propertyDescriptor.getWriteMethod() != null) {
			return propertyDescriptor.getWriteMethod();
		}

		throw new IllegalArgumentException("Didn't find write method for property '" + propertyName + "'."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Reads the specified property of the specified bean which must be instance
	 * of the persistenceClass of this ClassPropertyAccessor.
	 * 
	 * @param propertyName
	 * @param bean
	 * @return the property of the specified bean
	 */
	public Object read(final T bean, final String propertyName) {
		try {
			return getReadMethod(propertyName).invoke(bean);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes the specified property of the specified bean.
	 * 
	 * @param bean
	 * @param propertyName
	 * @param param
	 */
	public void write(final T bean, final String propertyName, final Object param) {
		try {
			getWriteMethod(propertyName).invoke(bean, param);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the type of the specified property.
	 * 
	 * @param propertyName
	 * @return the type of the specified property
	 */
	public Class<?> getPropertyType(final String propertyName) {
		return getReadMethod(propertyName).getReturnType();
	}

	/**
	 * @return the propertyDescriptors
	 */
	public Set<PropertyDescriptor> getPropertyDescriptors() {
		return new HashSet<PropertyDescriptor>(propertyDescriptors.values());
	}
}
