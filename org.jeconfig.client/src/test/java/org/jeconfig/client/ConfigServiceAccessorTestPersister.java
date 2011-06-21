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

package org.jeconfig.client;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.server.persister.InMemoryPersister;

public class ConfigServiceAccessorTestPersister extends InMemoryPersister {

	private boolean shouldFailSave = false;
	private boolean shouldFailLoad = false;
	private boolean shouldFailLoadOnce = false;
	private boolean shouldFailUpdate = false;
	private int loadCount = 0;
	private int saveCount = 0;

	public void setShouldFailLoad(final boolean shouldFailLoad) {
		this.shouldFailLoad = shouldFailLoad;
	}

	public void setShouldFailLoadOnce(final boolean shouldFailLoadOnce) {
		this.shouldFailLoadOnce = shouldFailLoadOnce;
	}

	public void setShouldFailSave(final boolean shouldFailSave) {
		this.shouldFailSave = shouldFailSave;
	}

	public void setShouldFailUpdate(final boolean shouldFailUpdate) {
		this.shouldFailUpdate = shouldFailUpdate;
	}

	@Override
	public ComplexConfigDTO loadConfiguration(final ScopePath scopePath) {
		loadCount++;
		if (shouldFailLoad || shouldFailLoadOnce) {
			shouldFailLoadOnce = false;
			throw new StaleConfigException(scopePath, "Expected exception"); //$NON-NLS-1$
		}
		return super.loadConfiguration(scopePath);
	}

	@Override
	public void saveConfiguration(final ComplexConfigDTO configDTO) {
		saveCount++;
		if (shouldFailSave) {
			throw new StaleConfigException(configDTO.getDefiningScopePath(), "Expected exception"); //$NON-NLS-1$
		}
		super.saveConfiguration(configDTO);
	}

	@Override
	public void updateConfiguration(final ComplexConfigDTO configDTO) {
		if (shouldFailUpdate) {
			throw new StaleConfigException(configDTO.getDefiningScopePath(), "Expected exception"); //$NON-NLS-1$
		}
		super.updateConfiguration(configDTO);
	}

	public int getLoadCount() {
		return loadCount;
	}

	public int getSaveCount() {
		return saveCount;
	}
}
