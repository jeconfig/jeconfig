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

package org.jeconfig.api.exception;

import org.jeconfig.api.scope.ScopePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link ConfigExceptionHandler} interface.<br>
 * Logs all exceptions and uses default failure solution strategies.
 */
public class DefaultConfigExceptionHandler implements ConfigExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultConfigExceptionHandler.class);

	@Override
	public LoadFailureSolutionStrategy loadFailed(
		final ScopePath scopePath,
		final Class<?> configClass,
		final Exception e,
		final int retryCount) {

		LOG.error("Error while doing a 'load' for config with scope path: '" + scopePath, e); //$NON-NLS-1$

		return LoadFailureSolutionStrategy.FAIL;
	}

	@Override
	public RefreshFailureSolutionStrategy refreshFailed(
		final ScopePath scopePath,
		final Object config,
		final Exception e,
		final int retryCount) {

		LOG.error("Error while doing a 'refresh' for config with scope path: '" + scopePath, e); //$NON-NLS-1$
		if (config != null) {
			LOG.error("config: \n" + config.toString()); //$NON-NLS-1$
		}

		return RefreshFailureSolutionStrategy.FAIL;
	}

	@Override
	public SaveFailureSolutionStrategy saveFailed(
		final ScopePath scopePath,
		final Object config,
		final Exception e,
		final int retryCount) {

		LOG.error("Error while doing a 'save' for config with scope path: '" + scopePath, e); //$NON-NLS-1$
		if (config != null) {
			LOG.error("config: \n" + config.toString()); //$NON-NLS-1$
		}

		if (e instanceof StaleConfigException && retryCount < 10) {
			LOG.info("Trying to refresh a stale configuration."); //$NON-NLS-1$
			return SaveFailureSolutionStrategy.REFRESH_CONFIG;
		}

		return SaveFailureSolutionStrategy.FAIL;
	}
}
