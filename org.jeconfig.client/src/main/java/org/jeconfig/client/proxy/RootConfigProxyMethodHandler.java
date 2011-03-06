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

package org.jeconfig.client.proxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jeconfig.api.scope.IScopePath;

public final class RootConfigProxyMethodHandler extends ConfigProxyMethodHandler implements IRootConfigProxy {
	private boolean dirty;
	private boolean isNew = false;
	private final Set<IConfigDirtyStateListener> listeners;
	private Object self;
	private final IScopePath scopePath;

	public RootConfigProxyMethodHandler(final Class<?> configClass, final IScopePath scopePath, final ProxyUpdater proxyUpdater) {
		super(configClass, proxyUpdater);
		this.scopePath = scopePath;
		this.dirty = false;
		listeners = new HashSet<IConfigDirtyStateListener>();
	}

	@Override
	public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
		this.self = self;
		return super.invoke(self, thisMethod, proceed, args);
	}

	@Override
	public IScopePath getScopePath() {
		return scopePath;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty() {
		doSetDirtyState(true);
	}

	@Override
	public void resetDirty() {
		doSetDirtyState(false);
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override
	public void setNew(final boolean isNew) {
		this.isNew = isNew;
	}

	private void doSetDirtyState(final boolean newValue) {
		if (dirty != newValue) {
			this.dirty = newValue;
			fireDirtyChanged();
		}
	}

	private void fireDirtyChanged() {
		for (final IConfigDirtyStateListener listener : listeners) {
			listener.dirtyStateChanged((IRootConfigProxy) self);
		}
	}

	@Override
	public void addDirtyStateListener(final IConfigDirtyStateListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeDirtyStateListener(final IConfigDirtyStateListener listener) {
		listeners.remove(listener);
	}
}
