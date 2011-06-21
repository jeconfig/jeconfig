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

/**
 * Registry for scope contributions.<br>
 * <br>
 * The scope registry can be obtained from the ConfigSetupService.<br>
 * Implementations of this interface must be thread-safe.<br>
 * <br>
 * 
 * This interface is not intended to be implemented by clients.
 */
public interface ScopeRegistry {

	/**
	 * Returns the descriptor of the scope with the given name.
	 * 
	 * @param scopeName
	 * @return the scope descriptor or <code>null</code> if not found
	 */
	ScopeDescriptor getScopeDescriptor(String scopeName);

	/**
	 * Returns the scope property provider for the scope with the given name if exists.
	 * 
	 * @param scopeName
	 * @return the property provider or <code>null</code> if not exists
	 */
	ScopePropertyProvider getScopePropertyProvider(String scopeName);

	/**
	 * Adds a new scope descriptor. After that scopes described by it can be used.<br>
	 * Only one scope descriptor can be added per scope name.
	 * 
	 * @param scopeDescriptor
	 */
	void addScopeDescriptor(ScopeDescriptor scopeDescriptor);

	/**
	 * Removes the given scope descriptor. Scopes described by it can not be longer used.
	 * 
	 * @param scopeDescriptor
	 */
	void removeScopeDescriptor(ScopeDescriptor scopeDescriptor);

	/**
	 * Adds a new scope property provider.<br>
	 * If the a new instance of the target scope of the provider is created it is filled with
	 * the properties of the provider.<br>
	 * Only one property provider can be added per scope.
	 * 
	 * @param provider
	 */
	void addScopePropertyProvider(ScopePropertyProvider provider);

	/**
	 * Removes the given property provider.
	 * 
	 * @param provider
	 */
	void removeScopePropertyProvider(ScopePropertyProvider provider);

}
