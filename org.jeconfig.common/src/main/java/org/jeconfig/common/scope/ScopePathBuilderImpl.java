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

package org.jeconfig.common.scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jeconfig.api.scope.Scope;
import org.jeconfig.api.scope.ScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.api.scope.ScopePropertyProvider;
import org.jeconfig.api.scope.ScopeRegistry;
import org.jeconfig.api.util.Assert;

public final class ScopePathBuilderImpl implements ScopePathBuilder {
	private final ScopeRegistry scopeRegistry;

	private final Class<?> configClass;

	private final List<Scope> scopes;

	private final ScopeValidator scopeValidator;

	public ScopePathBuilderImpl() {
		this(null, null);
	}

	public ScopePathBuilderImpl(final ScopeRegistry scopeRegistry, final Class<?> configClass) {
		this.scopeRegistry = scopeRegistry;
		this.configClass = configClass;
		scopes = new ArrayList<Scope>();
		scopeValidator = new ScopeValidator(scopeRegistry);
	}

	@Override
	public ScopePathBuilder append(final String scopeName) {
		return append(scopeName, Collections.<String, String> emptyMap());
	}

	@Override
	public ScopePathBuilder append(final String scopeName, final Map<String, String> properties) {
		Assert.paramNotNull(scopeName, "scopeName"); //$NON-NLS-1$
		Assert.paramNotNull(properties, "properties"); //$NON-NLS-1$

		Map<String, String> scopeProperties;
		if (scopeRegistry != null && configClass != null) {
			final ScopeDescriptor scopeDescriptor = getScopeDescriptor(scopeName);
			scopeProperties = getScopePropertiesFromProvider(scopeDescriptor);
		} else {
			scopeProperties = new HashMap<String, String>();
		}
		scopeProperties.putAll(properties);

		scopes.add(new ScopeImpl(scopeName, scopeProperties));

		return this;
	}

	@Override
	public ScopePathBuilder appendAll(final String[] scopeNames) {
		Assert.paramNotNull(scopeNames, "scopeNames"); //$NON-NLS-1$

		for (final String scopeName : scopeNames) {
			append(scopeName);
		}
		return this;
	}

	private ScopeDescriptor getScopeDescriptor(final String scopeName) {
		final ScopeDescriptor scopeDescriptor = scopeRegistry.getScopeDescriptor(scopeName);
		if (scopeDescriptor == null) {
			throw new IllegalArgumentException("The scope '" + scopeName + "' is not registered at the scope registry!"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return scopeDescriptor;
	}

	private Map<String, String> getScopePropertiesFromProvider(final ScopeDescriptor scopeDescriptor) {
		final ScopePropertyProvider propertyProvider = scopeRegistry.getScopePropertyProvider(scopeDescriptor.getScopeName());
		if (propertyProvider != null) {
			return new HashMap<String, String>(propertyProvider.getProperties(configClass));
		}

		return new HashMap<String, String>();
	}

	@Override
	public ScopePathBuilder addPropertyToScope(final String scopeName, final String propertyName, final String propertyValue) {
		Assert.paramNotNull(scopeName, "scopeName"); //$NON-NLS-1$
		Assert.paramNotNull(propertyName, "propertyName"); //$NON-NLS-1$

		for (final ListIterator<Scope> it = scopes.listIterator(); it.hasNext();) {
			final Scope scope = it.next();
			if (scope.getName().equals(scopeName)) {
				final Map<String, String> properties = scope.getProperties();
				properties.put(propertyName, propertyValue);
				final Scope newScope = new ScopeImpl(scopeName, properties);
				it.remove();
				it.add(newScope);
				return this;
			}
		}

		throw new IllegalArgumentException("Didn't find scope '" + scopeName + "'!"); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public ScopePath create() {
		final ScopePath result = new ScopePathImpl(scopes);

		scopeValidator.validateScopePath(result);

		return result;
	}

}
