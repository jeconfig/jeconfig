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

import java.util.List;

import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.Scope;
import org.jeconfig.api.scope.ScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopeRegistry;
import org.jeconfig.api.util.Assert;

public final class ScopeValidator {
	private final ScopeRegistry scopeRegistry;

	public ScopeValidator() {
		this(null);
	}

	public ScopeValidator(final ScopeRegistry scopeRegistry) {
		this.scopeRegistry = scopeRegistry;
	}

	public void validateScopeDescriptor(final ScopeDescriptor scopeDescriptor) {
		Assert.paramNotNull(scopeDescriptor.getMandatoryProperties(), "scopeDescriptor#mandatoryProperties"); //$NON-NLS-1$

		validateScopePropertyNames(scopeDescriptor.getMandatoryProperties());
	}

	public void validateScopePath(final ScopePath scopePath) {
		final List<Scope> scopes = scopePath.getScopes();
		if (scopes.size() < 2) {
			throw new IllegalArgumentException("A scope path needs at least a 'class' and a 'codeDefault' scope."); //$NON-NLS-1$
		}
		if (!ClassScopeDescriptor.NAME.equals(scopes.get(0).getName())) {
			throw new IllegalArgumentException("The first scope needs to be the 'class' scope!"); //$NON-NLS-1$
		}
		if (!CodeDefaultScopeDescriptor.NAME.equals(scopes.get(1).getName())) {
			throw new IllegalArgumentException("The first scope needs to be the 'codeDefault' scope!"); //$NON-NLS-1$
		}

		final String className = scopes.get(0).getProperty(ClassScopeDescriptor.PROP_CLASS_NAME);
		if (className == null || className.isEmpty()) {
			throw new IllegalArgumentException("The 'class' scope needs a 'className' property!"); //$NON-NLS-1$
		}
		validateDuplicateScopes(scopes, scopePath);

		for (final Scope scope : scopes) {
			validateMandatoryProperties(scope);

			for (final String propertyKey : scope.getProperties().keySet()) {
				validateScopePropertyName(propertyKey);
			}
		}
	}

	private void validateDuplicateScopes(final List<Scope> scopes, final ScopePath scopePath) {
		for (int i = 0; i < scopes.size(); i++) {
			final String scopeName = scopes.get(i).getName();
			for (int j = 0; j < scopes.size(); j++) {
				if (i != j && scopeName.equals(scopes.get(j).getName())) {
					throw new IllegalArgumentException("ScopePath: " //$NON-NLS-1$
						+ scopePath.toString()
						+ "contains duplicate scope: " //$NON-NLS-1$
						+ scopeName);
				}
			}
		}
	}

	private void validateMandatoryProperties(final Scope scope) {
		if (scopeRegistry != null) {
			final ScopeDescriptor scopeDescriptor = getScopeDescriptor(scope.getName());
			for (final String mandatoryProperty : scopeDescriptor.getMandatoryProperties()) {
				final String value = scope.getProperty(mandatoryProperty);
				if (value == null || value.trim().isEmpty()) {
					throw new IllegalArgumentException("The property '" //$NON-NLS-1$
						+ mandatoryProperty
						+ "' of the scope '" //$NON-NLS-1$
						+ scopeDescriptor.getScopeName()
						+ "' is mandatory!"); //$NON-NLS-1$
				}
			}
		}
	}

	private ScopeDescriptor getScopeDescriptor(final String scopeName) {
		final ScopeDescriptor scopeDescriptor = scopeRegistry.getScopeDescriptor(scopeName);
		if (scopeDescriptor == null) {
			throw new IllegalArgumentException("The scope '" + scopeName + "' is not registered at the scope registry!"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return scopeDescriptor;
	}

	private void validateScopePropertyNames(final String[] propertyNames) {
		for (final String propertyName : propertyNames) {
			validateScopePropertyName(propertyName);
		}
	}

	private void validateScopePropertyName(final String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException("Scope properties with 'null' names are not allowed!"); //$NON-NLS-1$
		}
	}
}
