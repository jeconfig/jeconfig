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

package org.jeconfig.api.scope;

import java.util.Map;

/**
 * Builder to create scope path instances.<br>
 * Implements the builder-pattern (method chaining and final create()).<br>
 * <br>
 * This interface is not intended to be implemented by clients!
 */
public interface ScopePathBuilder {

	/**
	 * Appends a scope with the given name to the current scope path.<br>
	 * <br>
	 * The new scope is initialized with the registered scope property provider of the scope.
	 * 
	 * @param scopeName the name of the scope to append
	 * @return the builder itself for method chaining
	 */
	ScopePathBuilder append(String scopeName);

	/**
	 * Appends a child to the given scope path.<br>
	 * <br>
	 * The child scope is initialized with the given properties and the properties of the registered
	 * scope property provider of the scope. The given properties will overwrite the properties of the
	 * property provider if they conflict.
	 * 
	 * @param scopeName the name of the new child scope; never <code>null</code>
	 * @param properties the properties of the new child scope; can be empty but not <code>null</code>
	 * @return the builder itself for method chaining
	 */
	ScopePathBuilder append(final String scopeName, final Map<String, String> properties);

	/**
	 * Appends multiple scopes the the given scope path in the order of the array items.<br>
	 * <br>
	 * The new scopes are initialized with the registered scope property providers of the scopes.
	 * 
	 * @param scopeNames the names of the scopes to append
	 * @return the builder itself for method chaining
	 */
	ScopePathBuilder appendAll(String[] scopeNames);

	/**
	 * Adds a property to the previously added scope with the specified name.<br>
	 * The scope must exist!
	 * 
	 * @param scopeName
	 * @param propertyName
	 * @param propertyValue
	 * @return the builder itself for method chaining
	 */
	ScopePathBuilder addPropertyToScope(String scopeName, String propertyName, String propertyValue);

	/**
	 * Finally creates the scope path.
	 * 
	 * @return the new scope path consisting of the initial scopes and the appended scopes
	 */
	ScopePath create();
}
