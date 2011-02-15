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

import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilder;
import org.jeconfig.api.scope.IScopePropertyProvider;
import org.jeconfig.api.scope.IScopeRegistry;
import org.jeconfig.api.util.Assert;

public final class ScopePathBuilderImpl implements IScopePathBuilder {
	private final IScopeRegistry scopeRegistry;

	private final Class<?> configClass;

	private final List<IScope> scopes;

	private final ScopeValidator scopeValidator;

	public ScopePathBuilderImpl() {
		this(null, null);
	}

	public ScopePathBuilderImpl(final IScopeRegistry scopeRegistry, final Class<?> configClass) {
		this.scopeRegistry = scopeRegistry;
		this.configClass = configClass;
		scopes = new ArrayList<IScope>();
		scopeValidator = new ScopeValidator(scopeRegistry);
	}

	@Override
	public IScopePathBuilder append(final String scopeName) {
		return append(scopeName, Collections.<String, String> emptyMap());
	}

	@Override
	public IScopePathBuilder append(final String scopeName, final Map<String, String> properties) {
		Assert.paramNotNull(scopeName, "scopeName"); //$NON-NLS-1$
		Assert.paramNotNull(properties, "properties"); //$NON-NLS-1$

		Map<String, String> scopeProperties;
		if (scopeRegistry != null && configClass != null) {
			final IScopeDescriptor scopeDescriptor = getScopeDescriptor(scopeName);
			scopeProperties = getScopePropertiesFromProvider(scopeDescriptor);
		} else {
			scopeProperties = new HashMap<String, String>();
		}
		scopeProperties.putAll(properties);

		scopes.add(new ScopeImpl(scopeName, scopeProperties));

		return this;
	}

	@Override
	public IScopePathBuilder appendAll(final String[] scopeNames) {
		Assert.paramNotNull(scopeNames, "scopeNames"); //$NON-NLS-1$

		for (final String scopeName : scopeNames) {
			append(scopeName);
		}
		return this;
	}

	private IScopeDescriptor getScopeDescriptor(final String scopeName) {
		final IScopeDescriptor scopeDescriptor = scopeRegistry.getScopeDescriptor(scopeName);
		if (scopeDescriptor == null) {
			throw new IllegalArgumentException("The scope '" + scopeName + "' is not registered at the scope registry!"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return scopeDescriptor;
	}

	private Map<String, String> getScopePropertiesFromProvider(final IScopeDescriptor scopeDescriptor) {
		final IScopePropertyProvider propertyProvider = scopeRegistry.getScopePropertyProvider(scopeDescriptor.getScopeName());
		if (propertyProvider != null) {
			return new HashMap<String, String>(propertyProvider.getProperties(configClass));
		}

		return new HashMap<String, String>();
	}

	@Override
	public IScopePathBuilder addPropertyToScope(final String scopeName, final String propertyName, final String propertyValue) {
		Assert.paramNotNull(scopeName, "scopeName"); //$NON-NLS-1$
		Assert.paramNotNull(propertyName, "propertyName"); //$NON-NLS-1$

		for (final ListIterator<IScope> it = scopes.listIterator(); it.hasNext();) {
			final IScope scope = it.next();
			if (scope.getName().equals(scopeName)) {
				final Map<String, String> properties = scope.getProperties();
				properties.put(propertyName, propertyValue);
				final IScope newScope = new ScopeImpl(scopeName, properties);
				it.remove();
				it.add(newScope);
				return this;
			}
		}

		throw new IllegalArgumentException("Didn't find scope '" + scopeName + "'!"); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public IScopePath create() {
		final IScopePath result = new ScopePathImpl(scopes);

		scopeValidator.validateScopePath(result);

		return result;
	}

}
