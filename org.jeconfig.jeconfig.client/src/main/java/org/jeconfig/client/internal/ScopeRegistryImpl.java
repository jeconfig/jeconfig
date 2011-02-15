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

import java.util.concurrent.ConcurrentHashMap;

import org.jeconfig.api.scope.ApplicationScopeDescriptor;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopeDescriptor;
import org.jeconfig.api.scope.IScopePropertyProvider;
import org.jeconfig.api.scope.IScopeRegistry;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.api.util.Assert;
import org.jeconfig.common.scope.ScopeValidator;

public final class ScopeRegistryImpl implements IScopeRegistry {
	private final ConcurrentHashMap<String, IScopeDescriptor> scopes;
	private final ConcurrentHashMap<String, IScopePropertyProvider> propertyProviders;

	private final ScopeValidator scopeValidator;

	public ScopeRegistryImpl() {
		scopes = new ConcurrentHashMap<String, IScopeDescriptor>();
		propertyProviders = new ConcurrentHashMap<String, IScopePropertyProvider>();
		scopeValidator = new ScopeValidator();
		registerDefaultScopes();
		registerDefaultPropertyProviders();
	}

	private void registerDefaultScopes() {
		addScopeDescriptor(new ClassScopeDescriptor());
		addScopeDescriptor(new CodeDefaultScopeDescriptor());
		addScopeDescriptor(new InstanceScopeDescriptor());
		addScopeDescriptor(new DefaultScopeDescriptor());
		addScopeDescriptor(new GlobalScopeDescriptor());
		addScopeDescriptor(new ApplicationScopeDescriptor());
		addScopeDescriptor(new UserScopeDescriptor());
	}

	private void registerDefaultPropertyProviders() {
		addScopePropertyProvider(new ClassScopePropertyProvider());
	}

	@Override
	public IScopeDescriptor getScopeDescriptor(final String scopeName) {
		return scopes.get(scopeName);
	}

	@Override
	public void addScopeDescriptor(final IScopeDescriptor scopeDescriptor) {
		Assert.paramNotNull(scopeDescriptor, "scopeDescriptor"); //$NON-NLS-1$

		scopeValidator.validateScopeDescriptor(scopeDescriptor);

		final IScopeDescriptor currentlySetDescriptor = scopes.putIfAbsent(scopeDescriptor.getScopeName(), scopeDescriptor);
		if (currentlySetDescriptor != null) {
			throw new IllegalArgumentException("Attempt to add a scope descriptor with a name which is already in use!"); //$NON-NLS-1$
		}
	}

	@Override
	public void removeScopeDescriptor(final IScopeDescriptor scopeDescriptor) {
		Assert.paramNotNull(scopeDescriptor, "scopeDescriptor"); //$NON-NLS-1$
		scopes.remove(scopeDescriptor.getScopeName());
	}

	@Override
	public IScopePropertyProvider getScopePropertyProvider(final String scopeName) {
		return propertyProviders.get(scopeName);
	}

	@Override
	public void addScopePropertyProvider(final IScopePropertyProvider provider) {
		Assert.paramNotNull(provider, "provider"); //$NON-NLS-1$
		final IScopePropertyProvider currentlySetProvider = propertyProviders.putIfAbsent(provider.getScopeName(), provider);
		if (currentlySetProvider != null) {
			throw new IllegalArgumentException(
				"Attempt to add a property provider for a scope which has already a property provider registered!"); //$NON-NLS-1$
		}
	}

	@Override
	public void removeScopePropertyProvider(final IScopePropertyProvider provider) {
		Assert.paramNotNull(provider, "provider"); //$NON-NLS-1$
		propertyProviders.remove(provider.getScopeName());
	}

}
