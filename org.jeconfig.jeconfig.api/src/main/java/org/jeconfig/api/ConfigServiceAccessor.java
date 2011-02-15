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

package org.jeconfig.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.exception.IConfigExceptionHandler;
import org.jeconfig.api.exception.LoadFailureSolutionStrategy;
import org.jeconfig.api.exception.RefreshFailureSolutionStrategy;
import org.jeconfig.api.exception.SaveFailureSolutionStrategy;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilder;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.api.util.Assert;

/**
 * Comfort facade for {@link IConfigService}.<br>
 * <br>
 * Offers convenience methods and the ability to handle exceptions automatically when an exception handler is given.
 */
public class ConfigServiceAccessor implements IConfigService {
	private final IConfigService configService;
	private IConfigExceptionHandler exceptionHandler = null;

	/**
	 * Creates a new accessor without automatic exception handling.
	 * 
	 * @param configService
	 */
	public ConfigServiceAccessor(final IConfigService configService) {
		this(configService, null);
	}

	/**
	 * Creates a new accessor with automatic exception handling.
	 * 
	 * @param configService
	 * @param exceptionHandler
	 */
	public ConfigServiceAccessor(final IConfigService configService, final IConfigExceptionHandler exceptionHandler) {
		this.configService = configService;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public <T> T createComplexObject(final Class<T> complexType) {
		return configService.createComplexObject(complexType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <I> List<I> createList(final Class<? extends List> listType) {
		return configService.createList(listType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <I> Set<I> createSet(final Class<? extends Set> setType) {
		return configService.createSet(setType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <K, V> Map<K, V> createMap(final Class<? extends Map> mapType) {
		return configService.createMap(mapType);
	}

	/**
	 * Creates a new configuration set of the default set type.<br>
	 * 
	 * @param <I>
	 * @return a new configuration set
	 */
	public <I> Set<I> createSet() {
		return createSet(HashSet.class);
	}

	/**
	 * Creates a new configuration list of the default list type.<br>
	 * 
	 * @param <I>
	 * @return a new configuration list
	 */
	public <I> List<I> createList() {
		return createList(ArrayList.class);
	}

	/**
	 * Creates a new configuration map of the default map type.<br>
	 * 
	 * @param <K>
	 * @param <V>
	 * @return a new configuration map
	 */
	public <K, V> Map<K, V> createMap() {
		return createMap(HashMap.class);
	}

	/**
	 * Deletes the configuration addressed by the scope path annotated at the configuration class.<br>
	 * Doesn't delete its child configurations!
	 * 
	 * @param configClass
	 */
	public void delete(final Class<?> configClass) {
		delete(getScopePathBuilderFactory(configClass).annotatedPath().create(), false);
	}

	/**
	 * Deletes all configurations of the given class.
	 * 
	 * @param configClass
	 */
	public void deleteAllOccurences(final Class<?> configClass) {
		final IScopePath scopePath = getScopePathBuilderFactory(configClass).stub().create();
		final IScope classScope = scopePath.getRootScope();

		configService.deleteAllOccurences(classScope.getName(), classScope.getProperties());
	}

	@Override
	public void delete(final IScopePath scopePath, final boolean deleteChildren) {
		configService.delete(scopePath, deleteChildren);
	}

	@Override
	public void deleteAllOccurences(final String scopeName, final Map<String, String> properties) {
		configService.deleteAllOccurences(scopeName, properties);
	}

	@Override
	public IScopePathBuilderFactory getScopePathBuilderFactory(final Class<?> configClass) {
		return configService.getScopePathBuilderFactory(configClass);
	}

	@Override
	public <T> T load(final Class<T> configClass, final IScopePath scopePath) {
		if (exceptionHandler != null) {
			return loadConfigUsingExceptionHandler(configClass, scopePath, null);
		}

		return configService.load(configClass, scopePath);
	}

	private <T> T loadConfigUsingExceptionHandler(
		final Class<T> configClass,
		final IScopePath scopePath,
		final LoadFailureSolutionStrategy solutionStrategy) {

		LoadFailureSolutionStrategy strategy = solutionStrategy;
		int retryCounter = 0;
		boolean runLoop;
		do {
			runLoop = false;
			try {
				return configService.load(configClass, scopePath);
			} catch (final StaleConfigException e) {
				strategy = exceptionHandler.loadFailed(scopePath, configClass, e, retryCounter);

				switch (strategy) {
					case OVERWRITE_STALE_CONFIG_WITH_PARENT:
						configService.delete(e.getScopePath(), false);
						runLoop = true;
						break;
					case RETRY:
						runLoop = true;
						break;
					case FAIL:
						throw e;
					default:
						throw new RuntimeException("incomplete switch-case"); //$NON-NLS-1$
				}

			} catch (final RuntimeException e1) {
				strategy = exceptionHandler.loadFailed(scopePath, configClass, e1, retryCounter);
				if (LoadFailureSolutionStrategy.RETRY == strategy) {
					runLoop = true;
				} else {
					throw e1;
				}
			}
			retryCounter++;
		} while (runLoop);

		// not reachable
		return null;
	}

	/**
	 * Loads or creates a configuration from the annotated scope path.
	 * 
	 * @param <T> the type of the configuration to load
	 * @param configClass the type of the configuration to load
	 * @return the configuration object
	 */
	public <T> T load(final Class<T> configClass) {
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$
		return load(configClass, getScopePathBuilderFactory(configClass).annotatedPath().create());
	}

	/**
	 * Loads or creates a configuration from the annotated scope path with the given instance name.<br>
	 * The annotated scope path must include the instance scope.
	 * 
	 * @param <T>
	 * @param configClass
	 * @param instanceName
	 * @return the configuration object
	 */
	public <T> T load(final Class<T> configClass, final String instanceName) {
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$
		Assert.paramNotNull(instanceName, "instanceName"); //$NON-NLS-1$

		final IScopePathBuilder scopePathBuilder = configService.getScopePathBuilderFactory(configClass).annotatedPath();
		scopePathBuilder.addPropertyToScope(
				InstanceScopeDescriptor.NAME,
				InstanceScopeDescriptor.PROP_INSTANCE_NAME,
				instanceName);

		return load(configClass, scopePathBuilder.create());
	}

	@Override
	public <T> void save(final T config) {
		if (exceptionHandler != null) {
			saveUsingExceptionHandler(config);
		} else {
			configService.save(config);
		}
	}

	private <T> void saveUsingExceptionHandler(final T config) {
		SaveFailureSolutionStrategy strategy = null;
		int retryCounter = 0;
		do {
			try {
				configService.save(config);
				return;
			} catch (final RuntimeException e) {
				strategy = exceptionHandler.saveFailed(getScopePath(config), config, e, retryCounter);

				switch (strategy) {
					case REFRESH_CONFIG:
						refresh(config);
						break;
					case RETRY:
						// nothing to do - loop will run again
						break;
					case IGNORE:
						return;
					case FAIL:
						throw e;
					default:
						throw new RuntimeException("incomplete swith-case"); //$NON-NLS-1$
				}
			}
			retryCounter++;
		} while (SaveFailureSolutionStrategy.RETRY == strategy);
	}

	@Override
	public <T> void copyToScopePath(final T config, final IScopePath destinationScopePath) {
		configService.copyToScopePath(config, destinationScopePath);
	}

	@Override
	public <T> void refresh(final T config) {
		if (exceptionHandler != null) {
			refreshUsingExceptionHandler(config);
		} else {
			configService.refresh(config);
		}
	}

	private <T> void refreshUsingExceptionHandler(final T config) {
		RefreshFailureSolutionStrategy strategy = null;
		int retryCounter = 0;
		do {
			try {
				configService.refresh(config);
				return;
			} catch (final RuntimeException e) {
				strategy = exceptionHandler.refreshFailed(getScopePath(config), config, e, retryCounter);

				switch (strategy) {
					case RETRY:
						// nothing to do - loop will run again
						break;
					case IGNORE:
						return;
					case FAIL:
						throw e;
					default:
						throw new RuntimeException("incomplete switch-case"); //$NON-NLS-1$
				}
			}
			retryCounter++;
		} while (RefreshFailureSolutionStrategy.RETRY == strategy);
	}

	@Override
	public Collection<IScopePath> listScopes(final String scopeName, final Map<String, String> properties) {
		return configService.listScopes(scopeName, properties);
	}

	/**
	 * Lists the scope paths of all persisted configurations.
	 * 
	 * @return the scope paths of all persisted configurations
	 */
	public Collection<IScopePath> listAllScopes() {
		return listScopes(ClassScopeDescriptor.NAME, Collections.<String, String> emptyMap());
	}

	@Override
	public IConfigUnsetter getConfigUnsetter() {
		return configService.getConfigUnsetter();
	}

	@Override
	public IScopePath getScopePath(final Object config) {
		return configService.getScopePath(config);
	}

	@Override
	public void addScopePathListener(final IScopePath forScopePath, final IScopePathListener listener) {
		configService.addScopePathListener(forScopePath, listener);
	}

	@Override
	public void removeScopePathListener(final IScopePath forScopePath, final IScopePathListener listener) {
		configService.removeScopePathListener(forScopePath, listener);
	}
}
