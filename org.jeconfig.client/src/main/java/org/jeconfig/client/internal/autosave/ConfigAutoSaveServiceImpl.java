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

package org.jeconfig.client.internal.autosave;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.jeconfig.api.ConfigServiceAccessor;
import org.jeconfig.api.ConfigService;
import org.jeconfig.api.autosave.ConfigAutoSaveService;
import org.jeconfig.api.exception.ConfigExceptionHandler;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.proxy.ConfigDirtyStateListener;
import org.jeconfig.client.proxy.RootConfigProxy;
import org.jeconfig.client.proxy.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigAutoSaveServiceImpl implements ConfigAutoSaveService {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigAutoSaveServiceImpl.class);
	private static final long DEFAULT_AUTOSAVE_TIME = 5 * 60 * 1000;

	private final ConcurrentHashMap<ScopePath, RootConfigProxy> dirtyConfigs;
	private final ConcurrentHashMap<ScopePath, ConfigExceptionHandler> exceptionHandlers;
	private final AtomicReference<ConfigService> configServiceReference;
	private volatile long autoSaveTime = DEFAULT_AUTOSAVE_TIME;
	private final DirtyStateListener listener = new DirtyStateListener();
	private Timer timer;
	private final AtomicReference<Boolean> closed;

	public ConfigAutoSaveServiceImpl() {
		dirtyConfigs = new ConcurrentHashMap<ScopePath, RootConfigProxy>();
		exceptionHandlers = new ConcurrentHashMap<ScopePath, ConfigExceptionHandler>();
		configServiceReference = new AtomicReference<ConfigService>();
		closed = new AtomicReference<Boolean>(Boolean.valueOf(false));
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
	public void flush() {
		ensureNotClosed();

		stopTimer();
		try {
			doFlush();
		} finally {
			startTimer();
		}
	}

	private void doFlush() {
		final ConfigService configService = configServiceReference.get();
		if (configService != null) {
			final Iterator<Entry<ScopePath, RootConfigProxy>> entryIterator = dirtyConfigs.entrySet().iterator();
			while (entryIterator.hasNext()) {
				final Entry<ScopePath, RootConfigProxy> entry = entryIterator.next();
				final RootConfigProxy config = entry.getValue();
				final Class<?> configClass = ProxyUtil.getConfigClass(config.getClass());
				try {
					final ConfigExceptionHandler exceptionHandler = exceptionHandlers.get(config.getScopePath());
					final ConfigService configServiceAccessor = new ConfigServiceAccessor(configService, exceptionHandler);
					configServiceAccessor.save(config);
				} catch (final Exception e) {
					logSaveFailed(config.getScopePath(), configClass, config, e);
				} finally {
					entryIterator.remove();
				}
			}
		}
	}

	private void logSaveFailed(final ScopePath scopePath, final Class<?> configClass, final Object config, final Exception e) {
		LOG.error("Error while doing an auto-save for config-class: '" //$NON-NLS-1$
			+ configClass.getName()
			+ "' at scope path" //$NON-NLS-1$
			+ scopePath
			+ ". Will not retry to save it!", e); //$NON-NLS-1$
		if (config != null) {
			LOG.error("config: \n" + config.toString()); //$NON-NLS-1$
		}
	}

	private void startTimer() {
		timer = new Timer("ConfigAutoSaveService", true); //$NON-NLS-1$
		scheduleTask();
	}

	private void scheduleTask() {
		if (timer != null) {
			timer.schedule(new AutoSaveTask(), autoSaveTime);
		}
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		timer = null;
	}

	@Override
	public void close() {
		if (closed.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
			stopTimer();
			doFlush();
		}
	}

	private void ensureNotClosed() {
		if (Boolean.TRUE.equals(closed.get())) {
			throw new IllegalStateException("Attempt to operate on closed service!"); //$NON-NLS-1$
		}
	}

	@Override
	public synchronized <T> void manageConfig(final T config, final ConfigExceptionHandler exceptionHandler) {
		Assert.paramNotNull(exceptionHandler, "exceptionHandler"); //$NON-NLS-1$
		ensureNotClosed();

		if (config instanceof RootConfigProxy) {
			final RootConfigProxy proxy = (RootConfigProxy) config;
			if (proxy.isDirty()) {
				dirtyConfigs.put(proxy.getScopePath(), proxy);
			}
			proxy.addDirtyStateListener(listener);
			exceptionHandlers.put(proxy.getScopePath(), exceptionHandler);
		}
	}

	@Override
	public void setAutoSaveInterval(final long millis) {
		ensureNotClosed();

		stopTimer();
		this.autoSaveTime = millis;
		startTimer();
	}

	@Override
	public boolean hasDirtyConfig(final ScopePath scopePath) {
		ensureNotClosed();

		return dirtyConfigs.containsKey(scopePath);
	}

	private class AutoSaveTask extends TimerTask {
		@Override
		public void run() {
			doFlush();
			scheduleTask();
		}
	}

	private class DirtyStateListener implements ConfigDirtyStateListener {
		@Override
		public void dirtyStateChanged(final RootConfigProxy configProxy) {
			if (configProxy.isDirty()) {
				if (Boolean.TRUE.equals(closed.get())) {
					LOG.warn("A managed configuration became dirty.\nBut can't save the configuration because this service is closed!"); //$NON-NLS-1$
				} else {
					dirtyConfigs.put(configProxy.getScopePath(), configProxy);
				}
			}
		}
	}

}
