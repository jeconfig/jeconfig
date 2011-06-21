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

package org.jeconfig.server.persister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.persister.ConfigPersister;
import org.jeconfig.api.scope.Scope;
import org.jeconfig.api.scope.ScopePath;

public class InMemoryPersister implements ConfigPersister {
	public static final String ID = "InMemoryPersister"; //$NON-NLS-1$

	private final Map<ScopePath, ComplexConfigDTO> savedObjects;

	public InMemoryPersister() {
		savedObjects = new HashMap<ScopePath, ComplexConfigDTO>();
	}

	@Override
	public void saveConfiguration(final ComplexConfigDTO configDTO) {
		if (savedObjects.containsKey(configDTO.getDefiningScopePath())) {
			throw new StaleConfigException(configDTO.getDefiningScopePath(), "Can't save a configuration which already exists."); //$NON-NLS-1$
		}
		savedObjects.put(configDTO.getDefiningScopePath(), configDTO);
	}

	@Override
	public ComplexConfigDTO loadConfiguration(final ScopePath scopePath) {
		return savedObjects.get(scopePath);
	}

	@Override
	public void updateConfiguration(final ComplexConfigDTO configDTO) {
		if (!savedObjects.containsKey(configDTO.getDefiningScopePath())) {
			throw new StaleConfigException(configDTO.getDefiningScopePath(), "The configuration to update doesnt exist"); //$NON-NLS-1$
		}
		final long olderVersion = savedObjects.get(configDTO.getDefiningScopePath()).getVersion();
		final long newVersion = configDTO.getVersion();
		if (newVersion <= olderVersion) {
			throw new StaleConfigException(
				configDTO.getDefiningScopePath(),
				"The configuration to update is not newer than the existing one"); //$NON-NLS-1$
		}
		savedObjects.put(configDTO.getDefiningScopePath(), configDTO);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public Collection<ScopePath> listScopes(final String scopeName, final Map<String, String> properties) {
		final List<ScopePath> resultList = new ArrayList<ScopePath>();

		for (final ScopePath tmpScopePath : savedObjects.keySet()) {
			for (final Scope tmpScope : tmpScopePath.getScopes()) {
				if (tmpScope.getName().equals(scopeName) && tmpScope.containsAllProperties(properties)) {
					resultList.add(tmpScopePath);
				}
			}
		}

		return resultList;
	}

	@Override
	public void delete(final ScopePath scopePath, final boolean deleteChildren) {
		if (deleteChildren) {
			for (final Iterator<ScopePath> it = savedObjects.keySet().iterator(); it.hasNext();) {
				final ScopePath tmpScopePath = it.next();
				if (tmpScopePath.startsPathWith(scopePath)) {
					it.remove();
				}
			}
		} else {
			savedObjects.remove(scopePath);
		}
	}

	@Override
	public void deleteAllOccurences(final String scopeName, final Map<String, String> properties) {

		for (final Iterator<ScopePath> it = savedObjects.keySet().iterator(); it.hasNext();) {
			final ScopePath tmpScopePath = it.next();

			for (final Scope tmpScope : tmpScopePath.getScopes()) {
				if (tmpScope.getName().equals(scopeName)) {
					if (tmpScope.containsAllProperties(properties)) {
						it.remove();
					}
				}
			}
		}
	}

}
