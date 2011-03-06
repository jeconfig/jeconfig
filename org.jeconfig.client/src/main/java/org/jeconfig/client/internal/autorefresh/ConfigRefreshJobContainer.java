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

package org.jeconfig.client.internal.autorefresh;

import org.jeconfig.api.autorefresh.IConfigRefreshNotifier;
import org.jeconfig.api.exception.IConfigExceptionHandler;
import org.jeconfig.client.proxy.IRootConfigProxy;

public class ConfigRefreshJobContainer<T> {

	private final IRootConfigProxy config;
	private final IConfigExceptionHandler exceptionHandler;
	private final IConfigRefreshNotifier<T> notifier;

	public ConfigRefreshJobContainer(
		final IRootConfigProxy config,
		final IConfigExceptionHandler exceptionHandler,
		final IConfigRefreshNotifier<T> notifier) {
		this.config = config;
		this.exceptionHandler = exceptionHandler;
		this.notifier = notifier;
	}

	public IRootConfigProxy getConfig() {
		return config;
	}

	public IConfigExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public IConfigRefreshNotifier<T> getNotifier() {
		return notifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((config == null) ? 0 : config.hashCode());
		result = prime * result + ((exceptionHandler == null) ? 0 : exceptionHandler.hashCode());
		result = prime * result + ((notifier == null) ? 0 : notifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ConfigRefreshJobContainer<?> other = (ConfigRefreshJobContainer<?>) obj;
		if (config == null) {
			if (other.config != null) {
				return false;
			}
		} else if (!config.equals(other.config)) {
			return false;
		}
		if (exceptionHandler == null) {
			if (other.exceptionHandler != null) {
				return false;
			}
		} else if (!exceptionHandler.equals(other.exceptionHandler)) {
			return false;
		}
		if (notifier == null) {
			if (other.notifier != null) {
				return false;
			}
		} else if (!notifier.equals(other.notifier)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "ConfigExceptionHandlerContainer [config="
			+ config
			+ ", exceptionHandler="
			+ exceptionHandler
			+ ", notifier="
			+ notifier
			+ "]";
	}
}
