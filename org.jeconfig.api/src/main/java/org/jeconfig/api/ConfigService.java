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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilderFactory;

/**
 * Configuration service core API.<br>
 * 
 * Implementations must be thread-safe.<br>
 * <br>
 * This interface is not intended to be implemented by clients.
 */
public interface ConfigService {

	/**
	 * Creates a new complex configuration object of the given type.<br>
	 * This is the only valid way how clients can create new configuration objects.
	 * 
	 * @param <T>
	 * @param complexType
	 * @return the new configuration object
	 */
	<T> T createComplexObject(final Class<T> complexType);

	/**
	 * Creates a new configuration set of the given type.<br>
	 * Note that the result is a decorator backed by a set of the given type!
	 * 
	 * @param <I>
	 * @param setType
	 * @return a set decorator backed by a set of the given type
	 */
	@SuppressWarnings("rawtypes")
	<I> Set<I> createSet(Class<? extends Set> setType);

	/**
	 * Creates a new configuration list of the given type.<br>
	 * Note that the result is a decorator backed by a list of the given type.
	 * 
	 * @param <I>
	 * @param listType
	 * @return a list decorator backed by a list of the given type
	 */
	@SuppressWarnings("rawtypes")
	<I> List<I> createList(Class<? extends List> listType);

	/**
	 * Creates a new configuration map of the given type.<br>
	 * Note that the result is a decorator backed by a map of the given type.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param mapType
	 * @return a map decorator backed by a map of the given type
	 */
	@SuppressWarnings("rawtypes")
	<K, V> Map<K, V> createMap(Class<? extends Map> mapType);

	/**
	 * Loads or creates a merged configuration from the given scope.
	 * 
	 * @param <T> the type of the configuration to load
	 * @param scopePath the scope path describing the source of the configuration
	 * @param configClass the type of the configuration to load
	 * @return the configuration object; is never <code>null</code><br>
	 * <br>
	 *         <b>throws</b> StoreConfigException
	 */
	<T> T load(final Class<T> configClass, final ScopePath scopePath);

	/**
	 * Saves the given configuration object into its source scope. <br>
	 * <br>
	 * <b>Important:</b>
	 * Note that all references to sub-configurations are not longer valid after save.
	 * They must be obtained again from the root configuration after save.
	 * 
	 * @param <T> the type of the configuration to save
	 * @param config the configuration object <br>
	 *            <b>throws</b> StaleConfigException, StoreConfigException,
	 *            IllegalArgumentException when the config was not created using the config service
	 */
	<T> void save(T config);

	/**
	 * Saves the given configuration object into the specified scope.
	 * There must no configuration exist in the destination scope.<br>
	 * <br>
	 * This method makes a copy of the configuration at the destination scope path.
	 * The given configuration will stay attached to its source scope path!
	 * 
	 * @param <T> the type of the configuration to save
	 * @param config the configuration object
	 * @param destinationScopePath the scope path describing the destination <br>
	 *            <b>throws</b> StaleConfigException if the configuration already
	 *            exists in the repository <br>
	 *            <b>throws</b> StoreConfigException if errors occur during save
	 *            <b>throws</b> IllegalArgumentException when the config was not created using the config service
	 */
	<T> void copyToScopePath(T config, ScopePath destinationScopePath);

	/**
	 * Refreshes the given configuration with the version of the repository.<br>
	 * <br>
	 * Note that even the refreshed configuration could be out of date if persistence service caching is enabled
	 * and the configuration is edited directly in the repository!
	 * 
	 * @param <T>
	 * @param config
	 */
	<T> void refresh(T config);

	/**
	 * Deletes the configuration specified by scope.
	 * 
	 * Note that this method may not transactional (especially if it involves multiple persisters).
	 * 
	 * @param scopePath the scope path describing the configuration to delete
	 * @param deleteChildren specifies whether child configurations should also be deleted <br>
	 *            <b>throws</b> StoreConfigException
	 */
	void delete(ScopePath scopePath, boolean deleteChildren);

	/**
	 * Deletes all configurations with the specified scope name and the given properties in their path.
	 * 
	 * Note that this method may not be transactional (especially when it involves multiple persisters).
	 * 
	 * @param scopeName
	 * @param properties
	 * <br>
	 *            <b>throws</b> StoreConfigException
	 */
	void deleteAllOccurences(String scopeName, Map<String, String> properties);

	/**
	 * Lists all scopes that contain a scope with the given name and properties
	 * (the keys and values of the specified properties must be contained by the result scopes).
	 * 
	 * @param scopeName the scope name, must not be null
	 * @param properties the properties or empty map for all scopes with the given scopeName
	 * @return the scopes <br>
	 *         <b>throws</b> StoreConfigException
	 */
	Collection<ScopePath> listScopes(String scopeName, Map<String, String> properties);

	/**
	 * Provides a scope factory to create scopes for the given configuration class.
	 * 
	 * @param configClass
	 * @return a factory to create scopes
	 */
	ScopePathBuilderFactory getScopePathBuilderFactory(Class<?> configClass);

	/**
	 * Returns a {@link ConfigUnsetter} which is able to un-set configuration properties.
	 * 
	 * @return a {@link ConfigUnsetter}
	 */
	ConfigUnsetter getConfigUnsetter();

	/**
	 * Returns the scope path of the given configuration object.
	 * 
	 * @param config may be <code>null</code>
	 * @return the scope path of the configuration or <code>null</code> if configuration is <code>null</code> or if it is not a
	 *         configuration object retrieved from this service
	 */
	ScopePath getScopePath(Object config);

	/**
	 * Add a listener that informs about saved configurations on the given scope path
	 * 
	 * @param forScopePath the scope path
	 * @param listener the listener
	 */
	void addScopePathListener(ScopePath forScopePath, ScopePathListener listener);

	/**
	 * remove the listener for the given scope path
	 * 
	 * @param forScopePath the scope path
	 * @param listener the listener to remove from the scope path
	 */
	void removeScopePathListener(ScopePath forScopePath, ScopePathListener listener);
}
