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

package org.jeconfig.aspect.test;

import org.jeconfig.api.exception.IConfigExceptionHandler;
import org.jeconfig.api.exception.LoadFailureSolutionStrategy;
import org.jeconfig.api.exception.RefreshFailureSolutionStrategy;
import org.jeconfig.api.exception.SaveFailureSolutionStrategy;
import org.jeconfig.api.scope.IScopePath;

public class TestConfigExceptionHandler implements IConfigExceptionHandler {
	private static boolean loadFailed = false;
	private static boolean saveFailed = false;
	private static boolean refreshFailed = false;

	@Override
	public LoadFailureSolutionStrategy loadFailed(
		final IScopePath scopePath,
		final Class<?> configClass,
		final Exception e,
		final int retryCount) {
		setLoadFailed(true);

		return LoadFailureSolutionStrategy.FAIL;
	}

	@Override
	public SaveFailureSolutionStrategy saveFailed(
		final IScopePath scopePath,
		final Object config,
		final Exception e,
		final int retryCount) {
		setSaveFailed(true);

		return SaveFailureSolutionStrategy.FAIL;
	}

	@Override
	public RefreshFailureSolutionStrategy refreshFailed(
		final IScopePath scopePath,
		final Object config,
		final Exception e,
		final int retryCount) {
		setRefreshFailed(true);

		return RefreshFailureSolutionStrategy.FAIL;
	}

	public static void setLoadFailed(final boolean loadFailed) {
		TestConfigExceptionHandler.loadFailed = loadFailed;
	}

	public static void setRefreshFailed(final boolean refreshFailed) {
		TestConfigExceptionHandler.refreshFailed = refreshFailed;
	}

	public static void setSaveFailed(final boolean saveFailed) {
		TestConfigExceptionHandler.saveFailed = saveFailed;
	}

	public static boolean isLoadFailed() {
		return loadFailed;
	}

	public static boolean isRefreshFailed() {
		return refreshFailed;
	}

	public static boolean isSaveFailed() {
		return saveFailed;
	}
}
