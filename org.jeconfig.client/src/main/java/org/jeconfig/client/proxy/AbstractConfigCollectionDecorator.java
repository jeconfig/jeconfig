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
import java.util.Iterator;

import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.util.Assert;

/**
 * @param <E>
 * @param <DTO_TYPE>
 */
public abstract class AbstractConfigCollectionDecorator<E, DTO_TYPE extends IConfigDTO> extends AbstractConfigProxy<DTO_TYPE> implements
		Collection<E> {
	private final Collection<E> target;

	public AbstractConfigCollectionDecorator(final Collection<E> target) {
		Assert.paramNotNull(target, "target"); //$NON-NLS-1$
		if (target instanceof IConfigProxy) {
			throw new IllegalArgumentException("Attempt to decorate a collection twice!"); //$NON-NLS-1$
		}

		this.target = target;
	}

	@Override
	public boolean add(final E e) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final boolean success = target.add(e);
		if (success) {
			setDirty();
			attachIfProxy(e);
		}
		return success;
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final boolean success = target.addAll(c);
		if (success) {
			setDirty();
			for (final E e : c) {
				attachIfProxy(e);
			}
		}
		return success;
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
	public boolean contains(final Object o) {
		return target.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return target.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return target.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> targetIterator = target.iterator();
		return new Iterator<E>() {
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
			public E next() {
				return targetIterator.next();
			}
		};
	}

	@Override
	public boolean remove(final Object o) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final boolean success = target.remove(o);
		if (success) {
			setDirty();
		}
		return success;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final boolean success = target.removeAll(c);
		if (success) {
			setDirty();
		}
		return success;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		if (isReadOnly()) {
			throw new UnsupportedOperationException(CROSSREFERENCES_ARE_READONLY);
		}
		final boolean success = target.retainAll(c);
		if (success) {
			setDirty();
		}
		return success;
	}

	@Override
	public int size() {
		return target.size();
	}

	@Override
	public Object[] toArray() {
		return target.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return target.toArray(a);
	}

	@Override
	public boolean equals(final Object obj) {
		return target.equals(obj);
	}

	@Override
	public int hashCode() {
		return target.hashCode();
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
			for (final E theE : target) {
				if (theE instanceof IConfigProxy) {
					final IConfigProxy<?> proxyE = (IConfigProxy<?>) theE;
					if (proxyE.hasDiff()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void attachIfProxy(final Object obj) {
		attachNewValueIfProxy(obj, this);
	}
}
