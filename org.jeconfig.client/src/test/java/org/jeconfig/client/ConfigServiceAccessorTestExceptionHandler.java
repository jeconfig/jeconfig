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

import org.jeconfig.api.exception.ConfigExceptionHandler;
import org.jeconfig.api.exception.LoadFailureSolutionStrategy;
import org.jeconfig.api.exception.RefreshFailureSolutionStrategy;
import org.jeconfig.api.exception.SaveFailureSolutionStrategy;
import org.jeconfig.api.scope.ScopePath;

public class ConfigServiceAccessorTestExceptionHandler implements ConfigExceptionHandler {

	private LoadFailureSolutionStrategy loadStrategy = LoadFailureSolutionStrategy.FAIL;
	private SaveFailureSolutionStrategy saveStrategy = SaveFailureSolutionStrategy.FAIL;
	private RefreshFailureSolutionStrategy refreshStrategy = RefreshFailureSolutionStrategy.FAIL;

	@Override
	public LoadFailureSolutionStrategy loadFailed(
		final ScopePath scopePath,
		final Class<?> configClass,
		final Exception e,
		final int retryCount) {

		switch (loadStrategy) {
			case RETRY:
				if (retryCount <= 5) {
					return LoadFailureSolutionStrategy.RETRY;
				}
				break;
			case OVERWRITE_STALE_CONFIG_WITH_PARENT:
				return LoadFailureSolutionStrategy.OVERWRITE_STALE_CONFIG_WITH_PARENT;
			default:
				break;
		}
		return LoadFailureSolutionStrategy.FAIL;
	}

	@Override
	public SaveFailureSolutionStrategy saveFailed(
		final ScopePath scopePath,
		final Object config,
		final Exception e,
		final int retryCount) {

		switch (saveStrategy) {
			case IGNORE:
				return SaveFailureSolutionStrategy.IGNORE;
			case RETRY:
				if (retryCount <= 5) {
					return SaveFailureSolutionStrategy.RETRY;
				}
				break;
			case REFRESH_CONFIG:
				return SaveFailureSolutionStrategy.REFRESH_CONFIG;
			default:
				break;
		}
		return SaveFailureSolutionStrategy.FAIL;
	}

	@Override
	public RefreshFailureSolutionStrategy refreshFailed(
		final ScopePath scopePath,
		final Object config,
		final Exception e,
		final int retryCount) {

		switch (refreshStrategy) {
			case IGNORE:
				return RefreshFailureSolutionStrategy.IGNORE;
			case RETRY:
				if (retryCount <= 5) {
					return RefreshFailureSolutionStrategy.RETRY;
				}
				break;
			default:
				break;
		}
		return RefreshFailureSolutionStrategy.FAIL;
	}

	public LoadFailureSolutionStrategy getLoadStrategy() {
		return loadStrategy;
	}

	public void setLoadStrategy(final LoadFailureSolutionStrategy loadStrategy) {
		this.loadStrategy = loadStrategy;
	}

	public SaveFailureSolutionStrategy getSaveStrategy() {
		return saveStrategy;
	}

	public void setSaveStrategy(final SaveFailureSolutionStrategy saveStrategy) {
		this.saveStrategy = saveStrategy;
	}

	public RefreshFailureSolutionStrategy getRefreshStrategy() {
		return refreshStrategy;
	}

	public void setRefreshStrategy(final RefreshFailureSolutionStrategy refreshStrategy) {
		this.refreshStrategy = refreshStrategy;
	}
}
