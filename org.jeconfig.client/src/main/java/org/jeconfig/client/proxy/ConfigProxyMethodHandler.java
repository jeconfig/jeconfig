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

package org.jeconfig.client.proxy;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.MethodHandler;

import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.client.internal.ConfigAnnotations;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ConfigProxyMethodHandler extends AbstractConfigProxy<ComplexConfigDTO> implements MethodHandler {
	private static final String CROSS_REFERENCES_ARE_READONLY = "Cross References are readonly"; //$NON-NLS-1$

	private final Set<Method> modifyingCrossReferenceMethods = new HashSet<Method>();
	private final Map<Method, ModifyingMethodInfo> modifyingMethods = new HashMap<Method, ConfigProxyMethodHandler.ModifyingMethodInfo>();
	private final Set<String> propertiesWithDiff = new HashSet<String>();

	private final PropertyAccessor propertyAccessor;
	private final ProxyUpdater proxyUpdater;

	private Object self;

	public ConfigProxyMethodHandler(final Class<?> configClass, final ProxyUpdater proxyUpdater) {
		this.proxyUpdater = proxyUpdater;

		propertyAccessor = new PropertyAccessor();
		for (final PropertyDescriptor propertyDescriptor : propertyAccessor.getPropertyDescriptors(ProxyUtil.getConfigClass(configClass))) {
			if (propertyDescriptor.getReadMethod() != null) {
				for (final Annotation annotation : propertyDescriptor.getReadMethod().getAnnotations()) {
					if (ConfigAnnotations.CONFIG_ANNOTATIONS.contains(annotation.annotationType())) {
						if (propertyDescriptor.getWriteMethod() != null) {
							modifyingMethods.put(propertyDescriptor.getWriteMethod(), new ModifyingMethodInfo(
								propertyDescriptor,
								annotation));
						}
					}
					if (ConfigCrossReference.class.equals(annotation.annotationType())) {
						if (propertyDescriptor.getWriteMethod() != null) {
							modifyingCrossReferenceMethods.add(propertyDescriptor.getWriteMethod());
						}
					}
				}
			}
		}
	}

	@Override
	public Set<String> getPropertiesWithDiff() {
		return new HashSet<String>(propertiesWithDiff);
	}

	@Override
	public void setPropertiesWithDiff(final Set<String> propertiesWithDiff) {
		this.propertiesWithDiff.clear();
		this.propertiesWithDiff.addAll(propertiesWithDiff);
	}

	private Set<Method> getModifyingCrossReferenceMethods() {
		return modifyingCrossReferenceMethods;
	}

	@SuppressWarnings({"rawtypes"})
	@Override
	public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
		this.self = self;
		if (isReadOnlyCrossReferences() && getModifyingCrossReferenceMethods().contains(thisMethod)) {
			throw new UnsupportedOperationException(CROSS_REFERENCES_ARE_READONLY);
		}
		if (isReadOnly() && modifyingMethods.containsKey(thisMethod)) {
			throw new UnsupportedOperationException(CROSS_REFERENCES_ARE_READONLY);
		}

		if (proceed == null) {
			try {
				return thisMethod.invoke(this, args);
			} catch (final InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException) {
					throw e.getTargetException();
				}
			}
		}
		// check whether thisMethod is a modifying method - note that
		// it is important to use thisMethod and not proceed as proceed is
		// a method defined by javassist!
		boolean shouldAttachNewValue = false;
		if (modifyingMethods.containsKey(thisMethod)) {
			final ModifyingMethodInfo methodInfo = modifyingMethods.get(thisMethod);
			shouldAttachNewValue = shouldAttachNewValue(args[0], methodInfo);
			if (shouldAttachNewValue) {
				attachNewValueIfProxy(args[0], (IConfigProxy) self, methodInfo);
			}
			if (!isInitializing()) {
				propertiesWithDiff.add(methodInfo.getPropertyName());
			}
			setDirty();
		}

		try {
			final Object result = proceed.invoke(self, args);

			if (shouldAttachNewValue && shouldUpdateProxy()) {
				proxyUpdater.updateConfig(self, getConfigDTOs());
			}

			return result;
		} catch (final InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw e.getTargetException();
			}
			throw e;
		}
	}

	private boolean shouldAttachNewValue(final Object newValue, final ModifyingMethodInfo methodInfo) {
		final Object oldValue = propertyAccessor.read(self, methodInfo.getPropertyName());

		if (oldValue != newValue) {
			final Annotation configPropertyAnnotation = methodInfo.getConfigPropertyAnnotation();
			if (newValue != null) {
				if (ConfigComplexProperty.class.equals(configPropertyAnnotation.annotationType())
					|| ConfigListProperty.class.equals(configPropertyAnnotation.annotationType())
					|| ConfigSetProperty.class.equals(configPropertyAnnotation.annotationType())
					|| ConfigMapProperty.class.equals(configPropertyAnnotation.annotationType())) {

					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	protected void attachNewValueIfProxy(final Object newValue, final IConfigProxy<?> self, final ModifyingMethodInfo methodInfo) {
		if (newValue instanceof IConfigProxy) {
			final IConfigProxy proxy = (IConfigProxy) newValue;
			proxy.setConfigAnnotation(methodInfo.getConfigPropertyAnnotation());
			super.attachNewValueIfProxy(
					newValue,
					self,
					ConfigAnnotations.isCollectionAnnotation(methodInfo.getConfigPropertyAnnotation()));
		} else if (!isInitializing()) {
			throw new IllegalArgumentException(
				"illegal argument use IConfigService.create* methods to create Config (sub)Objects for: " + newValue.getClass()); //$NON-NLS-1$
		}
	}

	@Override
	public boolean hasDiff() {
		if (super.hasDiff()) {
			return true;
		} else {
			for (final ModifyingMethodInfo methodInfo : modifyingMethods.values()) {
				try {
					final Object property = methodInfo.getPropertyDescriptor().getReadMethod().invoke(self, new Object[] {});
					if (property != null && property instanceof IConfigProxy) {
						final IConfigProxy<?> proxy = (IConfigProxy<?>) property;
						if (proxy.hasDiff()) {
							return true;
						}
					}
				} catch (final Throwable e) {
					throw new RuntimeException(e);
				}
			}
		}
		return false;
	}

	private static class ModifyingMethodInfo {
		private final PropertyDescriptor propertyDescriptor;
		private final Annotation configPropertyAnnotation;

		public ModifyingMethodInfo(final PropertyDescriptor propertyDescriptor, final Annotation configPropertyAnnotation) {
			this.propertyDescriptor = propertyDescriptor;
			this.configPropertyAnnotation = configPropertyAnnotation;
		}

		public PropertyDescriptor getPropertyDescriptor() {
			return propertyDescriptor;
		}

		public Annotation getConfigPropertyAnnotation() {
			return configPropertyAnnotation;
		}

		public String getPropertyName() {
			return propertyDescriptor.getName();
		}
	}
}
