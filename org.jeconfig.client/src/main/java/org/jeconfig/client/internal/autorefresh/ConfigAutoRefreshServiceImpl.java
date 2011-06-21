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

import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.jeconfig.api.ConfigService;
import org.jeconfig.api.autorefresh.ConfigAutoRefreshService;
import org.jeconfig.api.autorefresh.ConfigRefreshNotifier;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.ConfigExceptionHandler;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.proxy.RootConfigProxy;

public final class ConfigAutoRefreshServiceImpl implements ConfigAutoRefreshService {
	private static final long DEFAULT_AUTO_REFRESH_TIME = 1 * 60 * 1000; //1 min default refresh time

	private final ConcurrentHashMap<ScopePath, ConfigRefreshJobContainer<?>> configs;
	private final AtomicReference<ConfigService> configServiceReference;
	private Timer timer;
	private volatile long autoRefreshTime = DEFAULT_AUTO_REFRESH_TIME;
	private final AtomicReference<Boolean> closed;

	public ConfigAutoRefreshServiceImpl() {
		this.configs = new ConcurrentHashMap<ScopePath, ConfigRefreshJobContainer<?>>();
		this.configServiceReference = new AtomicReference<ConfigService>();
		this.closed = new AtomicReference<Boolean>(Boolean.valueOf(false));
	}

	public void bindConfigService(final ConfigService configService) {
		this.configServiceReference.set(configService);
		startTimer();
	}

	public void unbindConfigService(final ConfigService configService) {
		this.configServiceReference.compareAndSet(configService, null);
		if (this.configServiceReference.get() == null) {
			stopTimer();
		}
	}

	@Override
	public synchronized <T> void manageConfig(
		final T config,
		final ConfigRefreshNotifier<T> notifier,
		final ConfigExceptionHandler exceptionHandler) {
		Assert.paramNotNull(exceptionHandler, "exceptionHandler"); //$NON-NLS-1$
		ensureNotClosed();

		if (config instanceof RootConfigProxy) {
			final RootConfigProxy proxy = (RootConfigProxy) config;
			configs.put(proxy.getScopePath(), new ConfigRefreshJobContainer<T>(proxy, exceptionHandler, notifier));
		} else {
			throw new IllegalArgumentException("The given config must be an instance of RootConfigProxy."); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("unchecked")
	private void doRefresh() {
		final ConfigService configService = configServiceReference.get();
		if (configService != null) {
			for (final Entry<ScopePath, ConfigRefreshJobContainer<?>> entry : configs.entrySet()) {
				final RootConfigProxy proxy = entry.getValue().getConfig();
				final RootConfigProxy tmpProxy = configService.load(proxy.getClass(), proxy.getScopePath());
				configService.refresh(tmpProxy);
				if (proxy.getConfigDTOs().size() == tmpProxy.getConfigDTOs().size()) {
					if (refreshNeeded(proxy, tmpProxy)) {
						((ConfigRefreshNotifier<Object>) entry.getValue().getNotifier()).refreshConfig(proxy, configService);
					}
				} else {
					((ConfigRefreshNotifier<Object>) entry.getValue().getNotifier()).refreshConfig(proxy, configService);
				}
			}
		}
	}

	private boolean refreshNeeded(final RootConfigProxy proxy, final RootConfigProxy tmpProxy) {
		final List<ComplexConfigDTO> configDTOs = proxy.getConfigDTOs();
		final List<ComplexConfigDTO> tmpDTOs = tmpProxy.getConfigDTOs();
		for (int i = 0; i < proxy.getConfigDTOs().size(); i++) {
			if (configDTOs.get(i).getVersion() < tmpDTOs.get(i).getVersion()) {
				return true;
			}
		}
		return false;
	}

	private void ensureNotClosed() {
		if (Boolean.TRUE.equals(closed.get())) {
			throw new IllegalStateException("Attempt to operate on closed service!"); //$NON-NLS-1$
		}
	}

	@Override
	public void setAutoRefreshInterval(final long millis) {
		ensureNotClosed();

		stopTimer();
		this.autoRefreshTime = millis;
		startTimer();
	}

	private void startTimer() {
		timer = new Timer("ConfigAutoRefreshService", true); //$NON-NLS-1$
		scheduleTask();
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		timer = null;
	}

	private void scheduleTask() {
		if (timer != null) {
			timer.schedule(new AutoRefreshTask(), autoRefreshTime);
		}
	}

	private class AutoRefreshTask extends TimerTask {

		@Override
		public void run() {
			doRefresh();
			scheduleTask();
		}
	}

	@Override
	public void close() {
		if (closed.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
			stopTimer();
		}
	}
}
