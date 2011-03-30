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

package org.jeconfig.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.persister.IConfigPersistenceService;
import org.jeconfig.api.persister.IConfigPersister;
import org.jeconfig.api.persister.IPersisterSelector;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.common.datastructure.CacheEntry;
import org.jeconfig.common.datastructure.LRUCache;
import org.jeconfig.server.persister.DefaultPersisterSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration persistence service.
 * 
 * Persister which delegates to a persister which is chosen by a persister selector.
 */
public final class ConfigPersistenceServiceImpl implements IConfigPersistenceService {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigPersistenceServiceImpl.class);
	private static final int CACHE_SIZE = 400;

	private final Map<String, IConfigPersister> configPersisters;
	private final AtomicReference<IPersisterSelector> persisterSelector;
	private final Map<IScopePath, CacheEntry<ComplexConfigDTO>> configCache;
	private final AtomicReference<Boolean> cacheEnabled = new AtomicReference<Boolean>(Boolean.TRUE);

	public ConfigPersistenceServiceImpl() {
		configPersisters = new ConcurrentHashMap<String, IConfigPersister>();
		persisterSelector = new AtomicReference<IPersisterSelector>();
		persisterSelector.set(new DefaultPersisterSelector());
		configCache = Collections.synchronizedMap(new LRUCache<IScopePath, CacheEntry<ComplexConfigDTO>>(CACHE_SIZE));
	}

	@Override
	public void setPersisterSelector(final IPersisterSelector persisterSelector) {
		this.persisterSelector.set(persisterSelector);
	}

	public void unsetPersisterSelector(final IPersisterSelector persisterSelector) {
		this.persisterSelector.compareAndSet(persisterSelector, new DefaultPersisterSelector());
	}

	public void setConfigPersister(final IConfigPersister configPersister) {
		Assert.paramNotNull(configPersister, "configPersister"); //$NON-NLS-1$
		setConfigPersisters(new IConfigPersister[] {configPersister});
	}

	public void setConfigPersisters(final IConfigPersister[] configPersisters) {
		Assert.paramNotNull(configPersisters, "configPersisters"); //$NON-NLS-1$
		this.configPersisters.clear();
		for (final IConfigPersister configPersister : configPersisters) {
			addConfigPersister(configPersister);
		}
	}

	@Override
	public void addConfigPersister(final IConfigPersister configPersister) {
		Assert.paramNotNull(configPersister, "configPersister"); //$NON-NLS-1$
		configPersisters.put(configPersister.getId(), configPersister);
		LOG.info("added config persister: " + configPersister.getId()); //$NON-NLS-1$
	}

	@Override
	public void removeConfigPersister(final IConfigPersister configPersister) {
		if (configPersister != null) {
			configPersisters.remove(configPersister.getId());
			LOG.info("removed config persister: " + configPersister.getId()); //$NON-NLS-1$
		}
	}

	private IConfigPersister getPersisterForScopePath(final IScopePath scopePath) {
		final String persisterId = persisterSelector.get().getPersisterId(scopePath, configPersisters.keySet());
		if (persisterId == null) {
			throw new IllegalStateException(
				"Didn't find configuration persister id for scope: " + scopePath + "\nPlease check provided persister selector!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final IConfigPersister persister = configPersisters.get(persisterId);
		if (persister == null) {
			throw new IllegalStateException("Didn't find configuration persister with id '" + persisterId + "'!"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return persister;
	}

	@Override
	public void delete(final IScopePath scopePath, final boolean deleteChildren) {
		// clear the cache to remove the configurations which will be deleted
		// this could be improved by removing only necessary entries
		configCache.clear();

		if (deleteChildren) {
			for (final IConfigPersister persister : configPersisters.values()) {
				persister.delete(scopePath, deleteChildren);
			}
		} else {
			getPersisterForScopePath(scopePath).delete(scopePath, deleteChildren);
		}
	}

	@Override
	public void deleteAllOccurences(final String scopeName, final Map<String, String> properties) {
		// clear the cache to remove the configurations which will be deleted
		// this could be improved by removing only necessary entries
		configCache.clear();

		for (final IConfigPersister persister : configPersisters.values()) {
			persister.deleteAllOccurences(scopeName, properties);
		}
	}

	@Override
	public String getId() {
		// not used
		return null;
	}

	@Override
	public Collection<IScopePath> listScopes(final String scopeName, final Map<String, String> properties) {
		Assert.paramNotEmpty(scopeName, "scopeName"); //$NON-NLS-1$
		final Collection<IScopePath> scopesList = new LinkedList<IScopePath>();
		for (final IConfigPersister persister : configPersisters.values()) {
			scopesList.addAll(persister.listScopes(scopeName, properties));
		}
		return scopesList;
	}

	@Override
	public ComplexConfigDTO loadConfiguration(final IScopePath scopePath) {
		if (Boolean.TRUE.equals(cacheEnabled.get())) {
			CacheEntry<ComplexConfigDTO> result = configCache.get(scopePath);
			if (result == null) {
				result = new CacheEntry<ComplexConfigDTO>(getPersisterForScopePath(scopePath).loadConfiguration(scopePath));
				configCache.put(scopePath, result);
			}

			return result.getElement();
		}
		return getPersisterForScopePath(scopePath).loadConfiguration(scopePath);
	}

	@Override
	public void saveConfiguration(final ComplexConfigDTO configDTO) {
		try {
			getPersisterForScopePath(configDTO.getDefiningScopePath()).saveConfiguration(configDTO);
			putIntoCache(configDTO, configDTO.getDefiningScopePath());
		} catch (final StaleConfigException e) {
			removeFromCache(configDTO.getDefiningScopePath());
			throw e;
		}
	}

	@Override
	public void updateConfiguration(final ComplexConfigDTO configDTO) {
		try {
			getPersisterForScopePath(configDTO.getDefiningScopePath()).updateConfiguration(configDTO);
			putIntoCache(configDTO, configDTO.getDefiningScopePath());
		} catch (final StaleConfigException e) {
			removeFromCache(configDTO.getDefiningScopePath());
			throw e;
		}
	}

	private void putIntoCache(final ComplexConfigDTO config, final IScopePath scopePath) {
		if (Boolean.TRUE.equals(cacheEnabled.get())) {
			final CacheEntry<ComplexConfigDTO> cacheEntry = new CacheEntry<ComplexConfigDTO>(config);
			configCache.put(scopePath, cacheEntry);
		}
	}

	private void removeFromCache(final IScopePath scopePath) {
		configCache.remove(scopePath);
	}

	@Override
	public void setCacheEnabled(final boolean enabled) {
		if (cacheEnabled.compareAndSet(Boolean.valueOf(!enabled), Boolean.valueOf(enabled))) {
			if (!enabled) {
				configCache.clear();
			}
		}
	}
}
