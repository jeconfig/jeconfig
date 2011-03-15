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
import java.util.List;

import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.util.Assert;

public final class ScopePathImpl implements IScopePath {
	private static final long serialVersionUID = 1L;

	private final List<IScope> scopes;

	public ScopePathImpl(final List<IScope> scopes) {
		Assert.paramNotEmpty(scopes, "scopes"); //$NON-NLS-1$
		this.scopes = new ArrayList<IScope>(scopes);
	}

	@Override
	public List<IScope> getScopes() {
		return Collections.unmodifiableList(scopes);
	}

	@Override
	public IScope getRootScope() {
		return scopes.get(0);
	}

	@Override
	public IScope getLastScope() {
		return scopes.get(scopes.size() - 1);
	}

	@Override
	public IScopePath getParentPath() {
		if (scopes.size() > 1) {
			return new ScopePathImpl(scopes.subList(0, scopes.size() - 1));
		}

		return null;
	}

	@Override
	public IScope findScopeByName(final String name) {
		for (final IScope scope : scopes) {
			if (scope.getName().equals(name)) {
				return scope;
			}
		}
		return null;
	}

	@Override
	public boolean startsPathWith(final IScopePath other) {
		final List<IScope> otherScopeList = other.getScopes();
		final List<IScope> scopeList = getScopes();

		if (scopeList.size() < otherScopeList.size()) {
			return false;
		}

		for (int i = 0; i < otherScopeList.size(); i++) {
			final IScope scope = scopeList.get(i);
			final IScope otherScope = otherScopeList.get(i);

			if (!scope.equals(otherScope)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ScopePathImpl other = (ScopePathImpl) obj;
		if (scopes == null) {
			if (other.scopes != null) {
				return false;
			}
		} else if (!scopes.equals(other.scopes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < scopes.size(); i++) {
			final IScope scope = scopes.get(i);
			sb.append(scope);
			if (i < scopes.size() - 1) {
				sb.append("/"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}
}
