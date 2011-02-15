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
import java.util.List;
import java.util.ListIterator;

import org.jeconfig.api.dto.ConfigListDTO;

/**
 * @param <V>
 */
public final class ConfigListDecorator<V> extends AbstractConfigCollectionDecorator<V, ConfigListDTO> implements List<V> {
	private final List<V> target;

	public ConfigListDecorator(final List<V> target) {
		super(target);
		this.target = target;
	}

	@Override
	public void add(final int index, final V element) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		target.add(index, element);
		setDirty();
		attachIfProxy(element);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends V> c) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final boolean success = target.addAll(index, c);
		if (success) {
			setDirty();
			for (final V v : c) {
				attachIfProxy(v);
			}
		}
		return success;
	}

	@Override
	public V get(final int index) {
		return target.get(index);
	}

	@Override
	public int indexOf(final Object o) {
		return target.indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return target.lastIndexOf(o);
	}

	@Override
	public ListIterator<V> listIterator() {
		return new ObservedListIterator(target.listIterator());
	}

	@Override
	public ListIterator<V> listIterator(final int index) {
		return new ObservedListIterator(target.listIterator(index));
	}

	@Override
	public V remove(final int index) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final V result = target.remove(index);
		setDirty();
		return result;
	}

	@Override
	public V set(final int index, final V element) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final V result = target.set(index, element);
		setDirty();
		attachIfProxy(element);
		return result;
	}

	@Override
	public List<V> subList(final int fromIndex, final int toIndex) {
		final ConfigListDecorator<V> result = new ConfigListDecorator<V>(target.subList(fromIndex, toIndex));
		result.setParentProxy(this);
		return result;
	}

	private class ObservedListIterator implements ListIterator<V> {
		private final ListIterator<V> targetIterator;

		public ObservedListIterator(final ListIterator<V> wrappedIterator) {
			this.targetIterator = wrappedIterator;
		}

		@Override
		public int nextIndex() {
			return targetIterator.nextIndex();
		}

		@Override
		public int previousIndex() {
			return targetIterator.previousIndex();
		}

		@Override
		public void remove() {
			if (isReadOnly()) {
				throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
			}
			targetIterator.remove();
			setDirty();
		}

		@Override
		public boolean hasNext() {
			return targetIterator.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return targetIterator.hasPrevious();
		}

		@Override
		public V next() {
			return targetIterator.next();
		}

		@Override
		public V previous() {
			return targetIterator.previous();
		}

		@Override
		public void add(final V o) {
			if (isReadOnly()) {
				throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
			}
			targetIterator.add(o);
			setDirty();
			attachIfProxy(o);
		}

		@Override
		public void set(final V o) {
			if (isReadOnly()) {
				throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
			}
			targetIterator.set(o);
			setDirty();
			attachIfProxy(o);
		}
	};

	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
