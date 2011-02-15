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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.internal.AnnotationUtil;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public final class ConfigProxyFactory implements IConfigObjectFactory {
	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
		@Override
		public boolean isHandled(final Method m) {
			// skip finalize methods
			return !(m.getParameterTypes().length == 0 && m.getName().equals("finalize")); //$NON-NLS-1$
		}
	};

	private final ISimpleTypeConverterRegistry simpleTypeConverterRegistry;
	private final ConcurrentHashMap<Class<?>, ProxyFactory> factoryCache;

	public ConfigProxyFactory(final ISimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		this.simpleTypeConverterRegistry = simpleTypeConverterRegistry;
		factoryCache = new ConcurrentHashMap<Class<?>, ProxyFactory>();
	}

	@Override
	public <T> T createRootConfigProxy(final Class<T> configClass, final IScopePath scope) {
		if (AnnotationUtil.getAnnotation(configClass, ConfigClass.class) == null) {
			throw new IllegalArgumentException("Can only create root proxies for classes annotated with @ConfigClass!"); //$NON-NLS-1$
		}

		ProxyFactory proxyFactory = factoryCache.get(configClass);
		if (proxyFactory == null) {
			proxyFactory = new CurrentClassLoaderProxyFactory();
			proxyFactory.setSuperclass(configClass);
			proxyFactory.setInterfaces(new Class[] {IRootConfigProxy.class});
			proxyFactory.setFilter(FINALIZE_FILTER);
			factoryCache.putIfAbsent(configClass, proxyFactory);
			proxyFactory = factoryCache.get(configClass);
		}

		if (proxyFactory == null) {
			throw new IllegalStateException();
		}

		final T result = newInstance(configClass, proxyFactory);

		final ProxyUpdater proxyUpdater = new ProxyUpdater(this, simpleTypeConverterRegistry);
		((ProxyObject) result).setHandler(new RootConfigProxyMethodHandler(configClass, scope, proxyUpdater));

		// also create proxies for objects which may be created by the constructor of the configuration
		proxyUpdater.updateConfig(result, Collections.<ComplexConfigDTO> emptyList());

		return result;
	}

	@Override
	public <T> T createComplexProperty(final Class<T> propertyClass) {
		return createComplexProperty(propertyClass, new Class[] {}, new Object[] {});
	}

	@Override
	public <T> T createComplexProperty(
		final Class<T> propertyClass,
		final Class<?>[] constructorArgumentTypes,
		final Object[] arguments) {

		if (AnnotationUtil.getAnnotation(propertyClass, ConfigComplexType.class) == null) {
			throw new IllegalArgumentException(
				"Can only create configuration objects for classes annotated with @ConfigComplexType!"); //$NON-NLS-1$
		}

		ProxyFactory proxyFactory = factoryCache.get(propertyClass);
		if (proxyFactory == null) {
			proxyFactory = new CurrentClassLoaderProxyFactory();
			proxyFactory.setSuperclass(propertyClass);
			proxyFactory.setInterfaces(new Class[] {IConfigProxy.class});
			proxyFactory.setFilter(FINALIZE_FILTER);
			factoryCache.putIfAbsent(propertyClass, proxyFactory);
			proxyFactory = factoryCache.get(propertyClass);
		}

		if (proxyFactory == null) {
			throw new IllegalStateException();
		}
		final T result = newInstance(propertyClass, proxyFactory, constructorArgumentTypes, arguments);

		final ProxyUpdater proxyUpdater = new ProxyUpdater(this, simpleTypeConverterRegistry);
		((ProxyObject) result).setHandler(new ConfigProxyMethodHandler(propertyClass, proxyUpdater));

		// also create proxies for objects which may be created by the constructor of the configuration
		proxyUpdater.updateConfig(result, Collections.<ComplexConfigDTO> emptyList());

		return result;
	}

	private <T> T newInstance(final Class<T> configClass, final ProxyFactory proxyFactory) {
		return newInstance(configClass, proxyFactory, new Class[] {}, new Object[] {});
	}

	@SuppressWarnings("unchecked")
	private <T> T newInstance(
		final Class<T> configClass,
		final ProxyFactory proxyFactory,
		final Class<?>[] constructorArgumentTypes,
		final Object[] arguments) {

		try {
			return (T) proxyFactory.create(constructorArgumentTypes, arguments);
		} catch (final RuntimeException e) {
			if (containsNoClassDefFoundError(e)) {
				throw new RuntimeException("Couldn't load configuration class '" //$NON-NLS-1$
					+ configClass
					+ "'. Did you export the package '" //$NON-NLS-1$
					+ configClass.getPackage().getName()
					+ "'?", e); //$NON-NLS-1$
			}
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean containsNoClassDefFoundError(final Exception e) {
		Throwable current = e;
		while (current != null) {
			if (current instanceof NoClassDefFoundError) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}

	private static class CurrentClassLoaderProxyFactory extends ProxyFactory {
		@Override
		protected ClassLoader getClassLoader() {
			return getClass().getClassLoader();
		}
	}
}
