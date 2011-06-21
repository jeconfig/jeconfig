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

package org.jeconfig.client.internal;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.api.scope.ScopeRegistry;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.scope.ScopePathBuilderImpl;

public final class ScopePathBuilderFactoryImpl implements ScopePathBuilderFactory {
	private final ScopeRegistry scopeRegistry;
	private final Class<?> configClass;

	public ScopePathBuilderFactoryImpl(final ScopeRegistry scopeRegistry, final Class<?> configClass) {
		Assert.paramNotNull(scopeRegistry, "scopeRegistry"); //$NON-NLS-1$
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$

		this.scopeRegistry = scopeRegistry;
		this.configClass = ProxyUtil.getConfigClass(configClass);
	}

	@Override
	public ScopePathBuilder stub() {
		return annotatedPathUntil(CodeDefaultScopeDescriptor.NAME);
	}

	@Override
	public ScopePathBuilder annotatedPath() {
		return annotatedPathUntil(null);
	}

	@Override
	public ScopePathBuilder annotatedPathUntil(final String lastScope) {
		final ScopePathBuilder builder = new ScopePathBuilderImpl(scopeRegistry, configClass);

		final ConfigClass configClassAnnotation = AnnotationUtil.getAnnotation(configClass, ConfigClass.class);
		if (configClassAnnotation == null) {
			throw new IllegalArgumentException("The given object has no ConfigClass annotation"); //$NON-NLS-1$
		}

		builder.append(ClassScopeDescriptor.NAME);
		builder.append(CodeDefaultScopeDescriptor.NAME);

		if (CodeDefaultScopeDescriptor.NAME.equals(lastScope)) {
			return builder;
		}

		for (final String scopeName : configClassAnnotation.scopePath()) {
			builder.append(scopeName);

			if (scopeName.equals(lastScope)) {
				break;
			}
		}

		final ScopePath scopePath = builder.create();
		if (lastScope != null && !scopePath.getLastScope().getName().equals(lastScope)) {
			throw new IllegalArgumentException("The scope '" //$NON-NLS-1$
				+ lastScope
				+ "' was not found in the scope of the configuration class '" //$NON-NLS-1$
				+ configClass
				+ "'!"); //$NON-NLS-1$
		}

		return builder;
	}

}
