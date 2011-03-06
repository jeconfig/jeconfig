/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel, Lukas Gross
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

package org.jeconfig.exporter.internal;

import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilder;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;

/**
 * Generates a scope path including export scope from a given scope.
 */
public final class ExportScopePathGenerator {

	/**
	 * Builds a new scope path containing export scope.
	 * Properties of the given scope are kept.
	 * 
	 * @param <T> the type of the configuration
	 * @param scopePath the scope to add export scope
	 * @param configClass the type of the configuration
	 * @return the new scope containing export scope
	 */
	public <T> IScopePath buildExportScopePath(final IScopePath scopePath, final Class<T> configClass) {
		final IScopePathBuilder builder = new InternalScopePathBuilderFactory().createBuilder();

		boolean previousScopeWasClassOrCodeDefault = false;
		for (final IScope scope : scopePath.getScopes()) {
			if (previousScopeWasClassOrCodeDefault && !isClassOrCodeDefault(scope)) {
				builder.append(ExportScopeDescriptor.NAME);
			}

			builder.append(scope.getName(), scope.getProperties());
			previousScopeWasClassOrCodeDefault = isClassOrCodeDefault(scope);
		}
		return builder.create();
	}

	private boolean isClassOrCodeDefault(final IScope scope) {
		return ClassScopeDescriptor.NAME.equals(scope.getName()) || CodeDefaultScopeDescriptor.NAME.equals(scope.getName());
	}
}
