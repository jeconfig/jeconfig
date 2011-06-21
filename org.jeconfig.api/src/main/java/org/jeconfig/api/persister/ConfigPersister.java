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

package org.jeconfig.api.persister;

import java.util.Collection;
import java.util.Map;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.scope.ScopePath;

/**
 * Loads, saves, deletes and queries serialized configurations to/from a repository.<br>
 * <br>
 * This interface may be implemented by clients.
 */
public interface ConfigPersister {

	/**
	 * Provides the ID of the configuration persister instance.
	 * 
	 * @return the ID of the configuration persister instance
	 */
	String getId();

	/**
	 * Saves the configuration in its defined scope path.<br>
	 * The configuration must not yet exist in the repository.
	 * 
	 * @param configuration the DTO to save; never <code>null</code>
	 * 
	 * <br>
	 * <br>
	 *            <b>throws</b> StaleConfigException if the configuration already
	 *            exists in the repository <br>
	 *            <b>throws</b> StoreConfigException if errors occur during save
	 */
	void saveConfiguration(ComplexConfigDTO configuration);

	/**
	 * Updates the configuration in the repository with the given state.<br>
	 * The configuration must exist in the repository.
	 * 
	 * @param configuration the DTO to update; never <code>null</code>
	 * 
	 * <br>
	 * <br>
	 *            <b>throws</b> StaleConfigException if the configuration was modified in the
	 *            repository meanwhile or if the configuration does not exist in the repository<br>
	 *            <b>throws</b> StoreConfigException if errors occur during update
	 */
	void updateConfiguration(ComplexConfigDTO configuration);

	/**
	 * Loads the configuration from the given scope path.
	 * 
	 * @param scopePath the {@link ScopePath}
	 * @return the configuration or <code>null</code> if not found
	 * 
	 * <br>
	 * <br>
	 *         <b>throws</b> StoreConfigException if errors occur during load
	 */
	ComplexConfigDTO loadConfiguration(ScopePath scopePath);

	/**
	 * Deletes the configuration specified by scope path.<br>
	 * Optionally also deletes all child configurations of it. Child configurations
	 * are configurations whose scope paths begin with the specified scope path.
	 * 
	 * @param scopePath specifies the configuration to delete
	 * @param deleteChildren specifies whether child configurations should also be deleted
	 * 
	 * <br>
	 * <br>
	 *            <b>throws</b> StoreConfigException if errors occur during deletion
	 */
	void delete(ScopePath scopePath, boolean deleteChildren);

	/**
	 * Deletes all configurations with the specified scope name and the given properties in their path.<br>
	 * <br>
	 * Note that the scope name and the properties belong together. Only scope paths match which have a
	 * scope with the given name if the <b>same</b> scope has the specified properties.
	 * 
	 * @param scopeName the name of the scope to delete; must not be <code>null</code>
	 * @param properties the properties of the scope specified by scopeName; may be empty but not <code>null</code>
	 * 
	 * <br>
	 * <br>
	 *            <b>throws</b> StoreConfigException if errors occur during deletion
	 */
	void deleteAllOccurences(String scopeName, Map<String, String> properties);

	/**
	 * Lists the scope paths of all persisted configurations that contain a scope with the given name and the given properties.<br>
	 * Note that the scope name and the properties belong to together. Only scope paths match which have a
	 * scope with the given name if the <b>same</b> scope has the specified properties.
	 * 
	 * @param scopeName the scope name; must not be <code>null</code>
	 * @param properties the properties or empty map for all scopes with the given scopeName; must not be <code>null</code>
	 * @return the scope paths of the persisted configurations which match the filter conditions
	 * 
	 * <br>
	 * <br>
	 *         <b>throws</b> StoreConfigException if errors occur during list
	 */
	Collection<ScopePath> listScopes(final String scopeName, final Map<String, String> properties);
}
