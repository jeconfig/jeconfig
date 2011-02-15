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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.internal.AnnotationUtil;

public abstract class AbstractConfigProxy<DTO_TYPE extends IConfigDTO> implements IConfigProxy<DTO_TYPE> {
	protected static final String CROSSREFERENCES_ARE_READONLY = "Crossreferences are readonly"; //$NON-NLS-1$

	private IConfigProxy<?> parent;
	private List<DTO_TYPE> configDTOs;
	private boolean readOnly = false;
	private boolean readOnlyCrossRefs = false;
	private boolean initializing = false;
	private boolean diff = false;
	private Annotation configAnnotation;

	@Override
	public IScopePath getScopePath() {
		if (parent != null) {
			return parent.getScopePath();
		}
		return null;
	}

	@Override
	public IConfigProxy<?> getParentProxy() {
		return parent;
	}

	@Override
	public void setParentProxy(final IConfigProxy<?> parent) {
		this.parent = parent;
	}

	@Override
	public void setDirty() {
		if (!isInitializing()) {
			diff = true;
			if (parent != null) {
				parent.setDirty();
			}
		}
	}

	@Override
	public final void setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public final boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public final void setReadOnlyCrossReferences(final boolean readOnly) {
		this.readOnlyCrossRefs = readOnly;
	}

	@Override
	public final boolean isReadOnlyCrossReferences() {
		return readOnlyCrossRefs;
	}

	private void setInitializing(final boolean initializing) {
		this.initializing = initializing;
	}

	@Override
	public final boolean isInitializing() {
		return initializing;
	}

	@Override
	public final void setConfigDTOs(final List<DTO_TYPE> dtos) {
		this.configDTOs = dtos;
	}

	@Override
	public final List<DTO_TYPE> getConfigDTOs() {
		return configDTOs;
	}

	@Override
	public final DTO_TYPE getLeafConfigDTO() {
		final List<DTO_TYPE> configs = getConfigDTOs();
		return configs.size() > 0 ? configs.get(configs.size() - 1) : null;
	}

	@Override
	public Set<String> getPropertiesWithDiff() {
		// can be overwritten
		return Collections.emptySet();
	}

	@Override
	public void setPropertiesWithDiff(final Set<String> propertiesWithDiff) {
		// can be overwritten
	}

	@Override
	public boolean hasDiff() {
		if (diff) {
			return true;
		}

		return getPropertiesWithDiff().size() > 0;
	}

	@Override
	public final void setDiff(final boolean diff) {
		this.diff = diff;
	}

	@Override
	public boolean isDetached() {
		IConfigProxy<?> current = this;
		do {
			if (current instanceof IRootConfigProxy) {
				return false;
			}
			current = current.getParentProxy();
		} while (current != null);

		return true;
	}

	@Override
	public void setConfigAnnotation(final Annotation annotation) {
		this.configAnnotation = annotation;
	}

	@Override
	public Annotation getConfigAnnotation() {
		return configAnnotation;
	}

	protected boolean shouldUpdateProxy() {
		return !isInitializing() && !isDetached();
	}

	protected void attachNewValueIfProxy(final Object newValue, final IConfigProxy<?> self) {
		attachNewValueIfProxy(newValue, self, false);
	}

	protected void attachNewValueIfProxy(final Object newValue, final IConfigProxy<?> self, final boolean newValueIsCollection) {
		if (newValue != null) {
			final ConfigComplexType complexTypeAnno = AnnotationUtil.getAnnotation(newValue.getClass(), ConfigComplexType.class);
			if (complexTypeAnno != null || newValueIsCollection) {
				if (!(newValue instanceof IConfigProxy)) {
					throw new IllegalArgumentException(
						"Got a configuration object which was not created by the configuration service!\n" + //$NON-NLS-1$
							"Always use the create*()-Methods of the configuration service to create objects/collections!"); //$NON-NLS-1$
				}
				final IConfigProxy<?> proxy = (IConfigProxy<?>) newValue;
				if (!isInitializing() && !proxy.isDetached()) {
					throw new IllegalArgumentException("The configuration object is already set at a configuration." //$NON-NLS-1$
						+ "A configuration object can't be used twice. Please create a new one!"); //$NON-NLS-1$
				}

				proxy.setParentProxy(self);
			}
		}
	}

	@Override
	public void setInitializingWhile(final Runnable runnable) {
		if (!isInitializing()) {
			setInitializing(true);
			try {
				runnable.run();
			} finally {
				setInitializing(false);
			}
		} else {
			// reentrant
			runnable.run();
		}
	}
}
