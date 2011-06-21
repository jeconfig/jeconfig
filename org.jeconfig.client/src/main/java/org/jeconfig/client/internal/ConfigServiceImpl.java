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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jeconfig.api.ConfigSetupService;
import org.jeconfig.api.ConfigUnsetter;
import org.jeconfig.api.ScopePathListener;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.DefaultConfigFactory;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StalenessNotifier;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.persister.ConfigPersister;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopeRegistry;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.InternalConfigService;
import org.jeconfig.client.internal.beanvalidation.BeanValidator;
import org.jeconfig.client.internal.conversion.SimpleTypeConverterRegistryImpl;
import org.jeconfig.client.internal.crossreferences.CrossReferencesResolver;
import org.jeconfig.client.internal.mapping.ConfigDTOMapper;
import org.jeconfig.client.internal.merging.ConfigMerger;
import org.jeconfig.client.internal.migration.ClassVersionTransformationChain;
import org.jeconfig.client.internal.validation.ConfigValidator;
import org.jeconfig.client.proxy.ConfigListDecorator;
import org.jeconfig.client.proxy.ConfigMapDecorator;
import org.jeconfig.client.proxy.ConfigObjectCopyUtil;
import org.jeconfig.client.proxy.ConfigProxyFactory;
import org.jeconfig.client.proxy.ConfigSetDecorator;
import org.jeconfig.client.proxy.ConfigObjectFactory;
import org.jeconfig.client.proxy.RootConfigProxy;
import org.jeconfig.client.proxy.ProxyUpdater;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.datastructure.CacheEntry;
import org.jeconfig.common.datastructure.LRUCache;
import org.jeconfig.common.reflection.ClassInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigServiceImpl implements ConfigSetupService, InternalConfigService {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);
	private static final int CACHE_SIZE = 200;

	private final ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
	private final SimpleTypeConverterRegistry simpleTypeConverterRegistry = new SimpleTypeConverterRegistryImpl();
	private final AtomicReference<ConfigPersister> configPersistenceServiceReference = new AtomicReference<ConfigPersister>();
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final ConfigObjectCopyUtil copyUtil = new ConfigObjectCopyUtil();
	private final BeanValidator beanValidator = new BeanValidator();
	private final ConfigObjectFactory proxyFactory;
	private final ConfigDTOMapper dtoMapper;
	private final ConfigMerger configMerger;
	private final Map<ScopePath, CacheEntry<ComplexConfigDTO>> serializedConfigCache;
	private final ConfigValidator configValidator;
	private final CrossReferencesResolver crossReferencesResolver;
	private final ProxyUpdater proxyUpdater;
	private final AtomicReference<Boolean> clientCacheEnabled = new AtomicReference<Boolean>(Boolean.TRUE);
	private final AtomicReference<StalenessNotifier> globalStalenessNotifier;
	private final Map<ScopePath, Set<ScopePathListener>> scopePathListeners;

	public ConfigServiceImpl() {
		configMerger = new ConfigMerger(simpleTypeConverterRegistry);
		proxyFactory = new ConfigProxyFactory(simpleTypeConverterRegistry);
		proxyUpdater = new ProxyUpdater(proxyFactory, simpleTypeConverterRegistry);
		serializedConfigCache = Collections.synchronizedMap(new LRUCache<ScopePath, CacheEntry<ComplexConfigDTO>>(CACHE_SIZE));
		configValidator = new ConfigValidator(this, this);
		crossReferencesResolver = new CrossReferencesResolver(this);
		dtoMapper = new ConfigDTOMapper(simpleTypeConverterRegistry, proxyFactory, proxyUpdater);
		globalStalenessNotifier = new AtomicReference<StalenessNotifier>(new NoOpStalenessNotifier());
		scopePathListeners = Collections.synchronizedMap(new HashMap<ScopePath, Set<ScopePathListener>>());
	}

	public void bindConfigPersister(final ConfigPersister persistenceService) {
		configPersistenceServiceReference.set(persistenceService);
	}

	public void unbindConfigPersister(final ConfigPersister persistenceService) {
		configPersistenceServiceReference.compareAndSet(persistenceService, null);
	}

	@Override
	public ScopePathBuilderFactoryImpl getScopePathBuilderFactory(final Class<?> configClass) {
		return new ScopePathBuilderFactoryImpl(scopeRegistry, configClass);
	}

	@Override
	public <T> T createComplexObject(final Class<T> complexType) {
		return proxyFactory.createComplexProperty(complexType);
	}

	@Override
	public <T> T createComplexObject(
		final Class<T> complexType,
		final Class<?>[] constructorArgumentTypes,
		final Object[] arguments) {
		return proxyFactory.createComplexProperty(complexType, constructorArgumentTypes, arguments);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public <I> List<I> createList(final Class<? extends List> listType) {
		final List target = classInstantiation.newInstance(listType);
		return new ConfigListDecorator<I>(target);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public <I> Set<I> createSet(final Class<? extends Set> setType) {
		final Set target = classInstantiation.newInstance(setType);
		return new ConfigSetDecorator<I>(target, proxyUpdater);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public <K, V> Map<K, V> createMap(final Class<? extends Map> mapType) {
		final Map target = classInstantiation.newInstance(mapType);

		return new ConfigMapDecorator<K, V>(target, proxyUpdater);
	}

	@Override
	public <T> T load(final Class<T> configClass, final ScopePath scopePath) {
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$
		Assert.paramNotNull(scopePath, "scope"); //$NON-NLS-1$

		if (LOG.isDebugEnabled()) {
			LOG.debug("loading config for scope: " + scopePath); //$NON-NLS-1$
		}

		final Class<T> realClass = ProxyUtil.getConfigClass(configClass);

		configValidator.validate(realClass, scopePath);

		final List<ComplexConfigDTO> configs = new ArrayList<ComplexConfigDTO>();
		loadConfigsOfScopePath(realClass, scopePath, configs);

		Collections.reverse(configs);
		final ComplexConfigDTO configDTO = configMerger.merge(scopePath, configs, realClass, globalStalenessNotifier.get());

		final T config = dtoMapper.deserializeRootConfig(configClass, configDTO, scopePath, configs);
		crossReferencesResolver.resolveCrossReferences(config);

		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.resetDirty();
		proxy.setNew(isNewConfig(proxy));

		beanValidator.validate(config);

		return config;
	}

	private boolean isNewConfig(final RootConfigProxy rootProxy) {
		final ComplexConfigDTO lastConfigDTO = rootProxy.getConfigDTOs().get(rootProxy.getConfigDTOs().size() - 1);
		return !lastConfigDTO.getDefiningScopePath().equals(rootProxy.getScopePath());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void refresh(final T config) {
		Assert.paramNotNull(config, "config"); //$NON-NLS-1$

		final RootConfigProxy rootConfigProxy = getRootConfigProxy(config);
		final Class<T> realClass = (Class<T>) ProxyUtil.getConfigClass(config.getClass());

		removeScopePathFromCache(rootConfigProxy.getScopePath());

		final T refreshedConfig = load(realClass, rootConfigProxy.getScopePath());

		copyUtil.copyConfigTree(proxyFactory, refreshedConfig, config);

		// resolve cross references again because there are not copied by the copy utility
		crossReferencesResolver.resolveCrossReferences(config);

		// update root proxy
		rootConfigProxy.resetDirty();
		fireScopePathRefreshed(rootConfigProxy.getScopePath());
	}

	private void removeScopePathFromCache(final ScopePath scopePath) {
		ScopePath currentPath = scopePath;
		while (!CodeDefaultScopeDescriptor.NAME.equals(currentPath.getLastScope().getName())) {
			removeFromCache(currentPath);
			currentPath = currentPath.getParentPath();
		}
	}

	private <T> void loadConfigsOfScopePath(
		final Class<T> configClass,
		final ScopePath scopePath,
		final List<ComplexConfigDTO> configs) {
		if (scopePath != null) {
			if (ClassScopeDescriptor.NAME.equals(scopePath.getLastScope().getName())) {
				configs.add(dtoMapper.serialize(classInstantiation.newInstance(configClass), scopePath));
				// no further recursion - must be the root of the tree
			} else if (CodeDefaultScopeDescriptor.NAME.equals(scopePath.getLastScope().getName())) {
				ComplexConfigDTO defaultConfigDTO = null;
				final T defaultConfig = getDefaultConfig(configClass, scopePath);
				if (defaultConfig != null) {
					defaultConfigDTO = dtoMapper.serialize(defaultConfig, scopePath);
				}
				configs.add(defaultConfigDTO);
				loadConfigsOfScopePath(configClass, scopePath.getParentPath(), configs);
			} else {
				configs.add(getSerializedConfig(scopePath, configClass));
				loadConfigsOfScopePath(configClass, scopePath.getParentPath(), configs);
			}
		}
	}

	private ComplexConfigDTO getSerializedConfig(final ScopePath scopePath, final Class<?> configClass) {
		if (Boolean.TRUE.equals(clientCacheEnabled.get())) {
			CacheEntry<ComplexConfigDTO> result = serializedConfigCache.get(scopePath);
			if (result == null) {
				final ComplexConfigDTO loadedConfiguration = configPersistenceServiceReference.get().loadConfiguration(scopePath);
				final ComplexConfigDTO serialiedObjectToUse = migrateConfigIfNeeded(scopePath, configClass, loadedConfiguration);
				result = new CacheEntry<ComplexConfigDTO>(serialiedObjectToUse);
				serializedConfigCache.put(scopePath, result);
			}

			return result.getElement();
		}
		return configPersistenceServiceReference.get().loadConfiguration(scopePath);
	}

	private ComplexConfigDTO migrateConfigIfNeeded(
		final ScopePath scopePath,
		final Class<?> configClass,
		final ComplexConfigDTO loadedConfiguration) {
		ComplexConfigDTO serialiedObjectToUse = loadedConfiguration;
		if (loadedConfiguration != null) {
			final ConfigClass configClassAnnotation = AnnotationUtil.getAnnotation(configClass, ConfigClass.class);
			final long classVersionOfConfigClass = configClassAnnotation.classVersion();
			final long classVersionOfSerializedObject = loadedConfiguration.getClassVersion();

			if (classVersionOfSerializedObject != classVersionOfConfigClass) {
				final ClassVersionTransformationChain transformationChain = new ClassVersionTransformationChain(configClass);

				if (transformationChain.canConvert(classVersionOfSerializedObject, classVersionOfConfigClass)) {
					serialiedObjectToUse = transformationChain.convert(
							classVersionOfSerializedObject,
							classVersionOfConfigClass,
							loadedConfiguration);
				} else {
					throw new StaleConfigException(scopePath, "classVersions do not match"); //$NON-NLS-1$
				}
			}
		}
		return serialiedObjectToUse;
	}

	@SuppressWarnings("unchecked")
	private <T> T getDefaultConfig(final Class<T> configClass, final ScopePath scopePath) {
		final ConfigClass annotation = AnnotationUtil.getAnnotation(configClass, ConfigClass.class);
		if (annotation == null) {
			throw new IllegalArgumentException(
				"The configuration class must be annotated with @" + ConfigClass.class.getSimpleName()); //$NON-NLS-1$
		}

		if (annotation.defaultConfigFactory() != null) {
			final DefaultConfigFactory<T> configFactory = (DefaultConfigFactory<T>) classInstantiation.newInstance(annotation.defaultConfigFactory());
			return configFactory.createDefaultConfig(scopePath);
		}

		return null;
	}

	@Override
	public <T> void save(final T config) {
		Assert.paramNotNull(config, "config"); //$NON-NLS-1$
		final Class<?> realClass = ProxyUtil.getConfigClass(config.getClass());

		final RootConfigProxy rootConfigProxy = getRootConfigProxy(config);
		final ScopePath scopePath = rootConfigProxy.getScopePath();

		configValidator.validate(config, rootConfigProxy.getScopePath());
		beanValidator.validate(config);

		final boolean isNewConfig = rootConfigProxy.isNew();
		final boolean dirty = rootConfigProxy.isDirty();

		if (dirty) {
			final ComplexConfigDTO serializedConfig = dtoMapper.serialize(config, scopePath);
			try {
				if (isNewConfig) {
					configPersistenceServiceReference.get().saveConfiguration(serializedConfig);
					rootConfigProxy.setNew(false);
				} else {
					configPersistenceServiceReference.get().updateConfiguration(serializedConfig);
				}
			} catch (final StaleConfigException e) {
				// remove stale configuration from cache
				removeFromCache(scopePath);
				throw e;
			}

			// update the cache with the new configuration
			putIntoCache(serializedConfig, scopePath);

			// we need to update the proxy with the new DTOs
			final List<ComplexConfigDTO> configs = new ArrayList<ComplexConfigDTO>();
			loadConfigsOfScopePath(realClass, scopePath, configs);
			Collections.reverse(configs);
			proxyUpdater.updateConfig(config, configs);

			//we need to resolve the cross references(there might be new ones)
			crossReferencesResolver.resolveCrossReferences(config);
			rootConfigProxy.resetDirty();
			fireScopePathSaved(scopePath);
		}
	}

	private <T> RootConfigProxy getRootConfigProxy(final T config) {
		if (!RootConfigProxy.class.isAssignableFrom(config.getClass())) {
			throw new IllegalArgumentException(
				"Config Objects must be obtained by the ConfigService#load methods. did not happen for: " //$NON-NLS-1$
					+ ProxyUtil.getConfigClass(config.getClass()));
		}
		return (RootConfigProxy) config;
	}

	@Override
	public <T> void copyToScopePath(final T config, final ScopePath destinationScopePath) {
		Assert.paramNotNull(config, "config"); //$NON-NLS-1$
		Assert.paramNotNull(destinationScopePath, "destinationScopePath"); //$NON-NLS-1$

		final RootConfigProxy rootConfigProxy = getRootConfigProxy(config);
		final ScopePath sourceScopePath = rootConfigProxy.getScopePath();

		if (sourceScopePath.equals(destinationScopePath)) {
			throw new IllegalArgumentException("The source and the destination scope of the configuration '" //$NON-NLS-1$
				+ ProxyUtil.getConfigClass(config.getClass())
				+ "' are equal. Use ConfigService.save() to save it!"); //$NON-NLS-1$
		}

		configValidator.validate(config, destinationScopePath);
		beanValidator.validate(config);

		final ComplexConfigDTO serializedConfig = dtoMapper.serialize(config, destinationScopePath);
		configPersistenceServiceReference.get().saveConfiguration(serializedConfig);

		// update the cache with the new configuration
		putIntoCache(serializedConfig, destinationScopePath);
		fireScopePathSaved(destinationScopePath);
	}

	private void putIntoCache(final ComplexConfigDTO serializedConfig, final ScopePath scopePath) {
		if (Boolean.TRUE.equals(clientCacheEnabled.get())) {
			final CacheEntry<ComplexConfigDTO> cacheEntry = new CacheEntry<ComplexConfigDTO>(serializedConfig);
			serializedConfigCache.put(scopePath, cacheEntry);
		}
	}

	private void removeFromCache(final ScopePath scopePath) {
		serializedConfigCache.remove(scopePath);
	}

	@Override
	public void delete(final ScopePath scopePath, final boolean deleteChildren) {
		// clear the cache to remove the configurations which will be deleted
		// this could be improved by removing only necessary entries
		serializedConfigCache.clear();

		configPersistenceServiceReference.get().delete(scopePath, deleteChildren);
	}

	@Override
	public void deleteAllOccurences(final String scopeName, final Map<String, String> properties) {
		// clear the cache to remove the configurations which will be deleted
		// this could be improved by removing only necessary entries
		serializedConfigCache.clear();

		configPersistenceServiceReference.get().deleteAllOccurences(scopeName, properties);
	}

	@Override
	public SimpleTypeConverterRegistry getSimpleTypeConverterRegistry() {
		return simpleTypeConverterRegistry;
	}

	@Override
	public ScopeRegistry getScopeRegistry() {
		return scopeRegistry;
	}

	@Override
	public Collection<ScopePath> listScopes(final String scopeName, final Map<String, String> properties) {
		return configPersistenceServiceReference.get().listScopes(scopeName, properties);
	}

	@Override
	public ConfigUnsetter getConfigUnsetter() {
		return new ConfigUnsetterImpl(configMerger, dtoMapper);
	}

	@Override
	public ScopePath getScopePath(final Object config) {
		if (config instanceof RootConfigProxy) {
			return ((RootConfigProxy) config).getScopePath();
		}
		return null;
	}

	@Override
	public void setClientCacheEnabled(final boolean enabled) {
		if (clientCacheEnabled.compareAndSet(Boolean.valueOf(!enabled), Boolean.valueOf(enabled))) {
			if (!enabled) {
				serializedConfigCache.clear();
			}
		}
	}

	@Override
	public void setGlobalStalenessNotifier(final StalenessNotifier stalenessNotifier) {
		Assert.paramNotNull(stalenessNotifier, "stalenessNotifier"); //$NON-NLS-1$

		globalStalenessNotifier.set(stalenessNotifier);
	}

	@Override
	public void addScopePathListener(final ScopePath forScopePath, final ScopePathListener listener) {
		Set<ScopePathListener> listeners = scopePathListeners.get(forScopePath);
		if (listeners == null) {
			listeners = new HashSet<ScopePathListener>();
		}
		listeners.add(listener);
		scopePathListeners.put(forScopePath, listeners);
	}

	@Override
	public void removeScopePathListener(final ScopePath forScopePath, final ScopePathListener listener) {
		final Set<ScopePathListener> listeners = scopePathListeners.get(forScopePath);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	private void fireScopePathSaved(final ScopePath scopePath) {
		final Set<ScopePathListener> listeners = scopePathListeners.get(scopePath);
		if (listeners != null) {
			for (final ScopePathListener listener : listeners) {
				listener.configSaved(scopePath);
			}
		}
	}

	private void fireScopePathRefreshed(final ScopePath scopePath) {
		final Set<ScopePathListener> listeners = scopePathListeners.get(scopePath);
		if (listeners != null) {
			for (final ScopePathListener listener : listeners) {
				listener.configRefreshed(scopePath);
			}
		}
	}
}
