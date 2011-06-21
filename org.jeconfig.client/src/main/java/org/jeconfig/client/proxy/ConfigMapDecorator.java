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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.util.Assert;

/**
 * @param <K>
 * @param <V>
 */
public final class ConfigMapDecorator<K, V> extends AbstractConfigProxy<ConfigMapDTO> implements Map<K, V> {
	private final Map<K, V> target;
	private final ProxyUpdater proxyUpdater;

	public ConfigMapDecorator(final Map<K, V> target, final ProxyUpdater proxyUpdater) {
		Assert.paramNotNull(target, "target"); //$NON-NLS-1$
		Assert.paramNotNull(proxyUpdater, "proxyUpdater"); //$NON-NLS-1$

		if (target instanceof ConfigProxy) {
			throw new IllegalArgumentException("Attempt to decorate a map twice!"); //$NON-NLS-1$
		}

		this.target = target;
		this.proxyUpdater = proxyUpdater;
	}

	@Override
	public void clear() {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		target.clear();
		setDirty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return target.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return target.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		final ConfigSetDecorator<java.util.Map.Entry<K, V>> result = new ConfigSetDecorator<java.util.Map.Entry<K, V>>(
			target.entrySet(),
			proxyUpdater);
		result.setParentProxy(this);
		return result;
	}

	@Override
	public V get(final Object key) {
		return target.get(key);
	}

	@Override
	public boolean isEmpty() {
		return target.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		final ConfigSetDecorator<K> result = new ConfigSetDecorator<K>(target.keySet(), proxyUpdater);
		result.setParentProxy(this);
		return result;
	}

	@Override
	public V put(final K key, final V value) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final V result = target.put(key, value);
		setDirty();
		attachIfProxy(key, value);
		return result;
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		target.putAll(m);
		setDirty();
		for (final Entry<? extends K, ? extends V> entry : m.entrySet()) {
			attachIfProxy(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(final Object key) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final V result = target.remove(key);
		setDirty();
		return result;
	}

	@Override
	public int size() {
		return target.size();
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(target.values());
	}

	@Override
	public int hashCode() {
		return target.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return target.equals(obj);
	}

	@Override
	public String toString() {
		return target.toString();
	}

	@Override
	public boolean hasDiff() {
		if (super.hasDiff()) {
			return true;
		} else {
			for (final V theV : target.values()) {
				if (theV instanceof ConfigProxy) {
					final ConfigProxy<?> proxyV = (ConfigProxy<?>) theV;
					if (proxyV.hasDiff()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void attachIfProxy(final Object key, final Object value) {
		attachNewValueIfProxy(value, this);

		if (shouldUpdateProxy()) {
			proxyUpdater.updateConfigProxy(this, (List) getConfigDTOs(), getConfigAnnotation());
		}
	}
}
