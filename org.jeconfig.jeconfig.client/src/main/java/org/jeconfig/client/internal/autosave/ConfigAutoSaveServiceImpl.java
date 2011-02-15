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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeconfig.api.ConfigServiceAccessor;
import org.jeconfig.api.IConfigService;
import org.jeconfig.api.autosave.IConfigAutoSaveService;
import org.jeconfig.api.exception.IConfigExceptionHandler;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.proxy.IConfigDirtyStateListener;
import org.jeconfig.client.proxy.IRootConfigProxy;
import org.jeconfig.client.proxy.ProxyUtil;

public final class ConfigAutoSaveServiceImpl implements IConfigAutoSaveService {
	private static final Log LOG = LogFactory.getLog(ConfigAutoSaveServiceImpl.class);
	private static final long DEFAULT_AUTOSAVE_TIME = 5 * 60 * 1000;

	private final ConcurrentHashMap<IScopePath, IRootConfigProxy> dirtyConfigs;
	private final ConcurrentHashMap<IScopePath, IConfigExceptionHandler> exceptionHandlers;
	private final AtomicReference<IConfigService> configServiceReference;
	private volatile long autoSaveTime = DEFAULT_AUTOSAVE_TIME;
	private final DirtyStateListener listener = new DirtyStateListener();
	private Timer timer;
	private final AtomicReference<Boolean> closed;

	public ConfigAutoSaveServiceImpl() {
		dirtyConfigs = new ConcurrentHashMap<IScopePath, IRootConfigProxy>();
		exceptionHandlers = new ConcurrentHashMap<IScopePath, IConfigExceptionHandler>();
		configServiceReference = new AtomicReference<IConfigService>();
		closed = new AtomicReference<Boolean>(Boolean.valueOf(false));
	}

	public void bindConfigService(final IConfigService configService) {
		this.configServiceReference.set(configService);
		startTimer();
	}

	public void unbindConfigService(final IConfigService configService) {
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
		final IConfigService configService = configServiceReference.get();
		if (configService != null) {
			final Iterator<Entry<IScopePath, IRootConfigProxy>> entryIterator = dirtyConfigs.entrySet().iterator();
			while (entryIterator.hasNext()) {
				final Entry<IScopePath, IRootConfigProxy> entry = entryIterator.next();
				final IRootConfigProxy config = entry.getValue();
				final Class<?> configClass = ProxyUtil.getConfigClass(config.getClass());
				try {
					final IConfigExceptionHandler exceptionHandler = exceptionHandlers.get(config.getScopePath());
					final IConfigService configServiceAccessor = new ConfigServiceAccessor(configService, exceptionHandler);
					configServiceAccessor.save(config);
				} catch (final Exception e) {
					logSaveFailed(config.getScopePath(), configClass, config, e);
				} finally {
					entryIterator.remove();
				}
			}
		}
	}

	private void logSaveFailed(final IScopePath scopePath, final Class<?> configClass, final Object config, final Exception e) {
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
	public synchronized <T> void manageConfig(final T config, final IConfigExceptionHandler exceptionHandler) {
		Assert.paramNotNull(exceptionHandler, "exceptionHandler"); //$NON-NLS-1$
		ensureNotClosed();

		if (config instanceof IRootConfigProxy) {
			final IRootConfigProxy proxy = (IRootConfigProxy) config;
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
	public boolean hasDirtyConfig(final IScopePath scopePath) {
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

	private class DirtyStateListener implements IConfigDirtyStateListener {
		@Override
		public void dirtyStateChanged(final IRootConfigProxy configProxy) {
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
