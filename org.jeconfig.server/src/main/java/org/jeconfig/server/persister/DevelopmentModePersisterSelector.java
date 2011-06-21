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

import java.util.Collection;

import org.jeconfig.api.persister.PersisterSelector;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.api.util.Assert;

/**
 * Persister selector which can be used while development.<br>
 * When in development mode, it saves user configurations in memory and not physically. <br>
 * <br>
 * Set this persister selector at the ConfigPersistenceService to use it.
 */
public final class DevelopmentModePersisterSelector implements PersisterSelector {
	private final PersisterSelector persisterSelector;
	private boolean developmentMode;

	public DevelopmentModePersisterSelector(final PersisterSelector persisterSelector, final boolean developmentMode) {
		Assert.paramNotNull(persisterSelector, "persisterSelector"); //$NON-NLS-1$
		this.persisterSelector = persisterSelector;
		this.developmentMode = developmentMode;
	}

	@Override
	public String getPersisterId(final ScopePath scopePath, final Collection<String> configPersisterIds) {
		if (configPersisterIds.size() == 0) {
			throw new IllegalStateException("Didn't get any configuration persister!"); //$NON-NLS-1$
		}
		if (developmentMode && scopePath != null && scopePath.findScopeByName(UserScopeDescriptor.NAME) != null) {
			return InMemoryPersister.ID;
		}
		return persisterSelector.getPersisterId(scopePath, configPersisterIds);
	}

	public boolean isDevelopmentMode() {
		return developmentMode;
	}

	public void setDevelopmentMode(final boolean developmentMode) {
		this.developmentMode = developmentMode;
	}
}
