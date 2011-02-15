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

package org.jeconfig.api.autorefresh;

import org.jeconfig.api.exception.IConfigExceptionHandler;

/**
 * Service which informs a config notifier if a refresh is needed.
 */
public interface IConfigAutoRefreshService {

	/**
	 * Sets the interval this service should look if a refresh is needed configurations.
	 * 
	 * @param millis the refresh interval in milli seconds
	 */
	void setAutoRefreshInterval(long millis);

	/**
	 * Registers a configuration to be auto-refreshed at its interval.
	 * 
	 * @param <T> the type of the configuration
	 * @param config the configuration which should be auto-refreshed
	 * @param notifier the notifier to inform if a refresh is necessary
	 * @param exceptionHandler handles exceptions if errors occur during refresh
	 */
	<T> void manageConfig(T config, IConfigRefreshNotifier<T> notifier, IConfigExceptionHandler exceptionHandler);

	/**
	 * Closes this service.
	 */
	void close();
}
