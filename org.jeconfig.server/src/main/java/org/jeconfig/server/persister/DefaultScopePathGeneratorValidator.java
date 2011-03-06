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

package org.jeconfig.server.persister;

import java.util.List;

import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;

public final class DefaultScopePathGeneratorValidator {
	private static final String SCOPE_NAME_PATTERN = "[a-zA-Z0-9]+"; //$NON-NLS-1$
	private static final String PROPERTY_NAME_PATTERN = "[a-zA-Z0-9\\.]+"; //$NON-NLS-1$
	private static final String PROPERTY_VALUE_PATTERN = "[a-zA-Z0-9_$\\.]+"; //$NON-NLS-1$

	private DefaultScopePathGeneratorValidator() {}

	public static void validateScopePath(final IScopePath scopePath) {
		final List<IScope> scopes = scopePath.getScopes();
		for (final IScope scope : scopes) {
			if (!scope.getName().matches(SCOPE_NAME_PATTERN)) {
				throw new IllegalArgumentException("The scope name '" //$NON-NLS-1$
					+ scope.getName()
					+ "' must match the pattern '" //$NON-NLS-1$
					+ SCOPE_NAME_PATTERN
					+ "'!"); //$NON-NLS-1$
			}
			for (final String propertyKey : scope.getProperties().keySet()) {
				validateScopePropertyName(propertyKey);
				validateScopePropertyValue(scope.getProperty(propertyKey));
			}
		}
	}

	private static void validateScopePropertyName(final String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException("Scope properties with 'null' names are not allowed!"); //$NON-NLS-1$
		}
		if (!propertyName.matches(PROPERTY_NAME_PATTERN)) {
			throw new IllegalArgumentException("The scope property name '" //$NON-NLS-1$
				+ propertyName
				+ "' must match the pattern '" //$NON-NLS-1$
				+ PROPERTY_NAME_PATTERN
				+ "'!"); //$NON-NLS-1$
		}
	}

	private static void validateScopePropertyValue(final String propertyValue) {
		if (propertyValue != null) {
			if (!propertyValue.matches(PROPERTY_VALUE_PATTERN)) {
				throw new IllegalArgumentException("The scope property value'" //$NON-NLS-1$
					+ propertyValue
					+ "' must match the pattern '" //$NON-NLS-1$
					+ PROPERTY_VALUE_PATTERN
					+ "'!"); //$NON-NLS-1$
			}
		}
	}
}
