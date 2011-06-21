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

import java.io.Serializable;
import java.util.List;

/**
 * Addresses a configuration object.<br>
 * <br>
 * Defines how a configuration object is persisted in the repository and how it is merged with other
 * configurations.<br>
 * <br>
 * 
 * A scope path consists of multiple {@link Scope}s.
 * It has always two mandatory root scopes named 'class' and 'codeDefault'.<br>
 * 
 * A scope path is built up in the form:
 * 
 * <pre>
 * | root scope 'class' | 2nd scope 'codeDefault' | ... | last scope |
 * </pre>
 * 
 * When a configuration is loaded, it is merged with its parent configurations. The parent configurations
 * are the configurations addressed by the sub-paths of the scope path.
 * For example, the parents of the scope path
 * 
 * <pre>
 * | 'class' | 'codeDefault' | 'global' |
 * </pre>
 * 
 * are
 * 
 * <pre>
 * | 'class' | 'codeDefault' |
 * </pre>
 * 
 * and
 * 
 * <pre>
 * | 'class' |.
 * </pre>
 * 
 * When the configuration with this scope path is loaded, the 'global' configuration is merged with its parent
 * configuration which is the 'codeDefault' configuration. If the 'codeDefault' configuration doesn't exist,
 * the 'global' configuration is merged with the 'class' configuration which is the configuration created by the
 * default constructor of the configuration class.<br>
 * Note that the configuration is merged with all parents but only with one of the 'class' or 'codeDefault' configurations.
 * 
 * <br>
 * <br>
 * This interface is not intended to be implemented by clients.<br>
 * <br>
 * 
 * Use ConfigService#getScopePathBuilderFactory() to create scope paths.
 */
public interface ScopePath extends Serializable {

	/**
	 * Returns the list of the scopes of this path in the order 'root scope'...'last scope'.
	 * 
	 * @return the list of scopes of this path
	 */
	List<Scope> getScopes();

	/**
	 * Returns the root scope which is always the 'class' scope.
	 * 
	 * @return the root scope
	 */
	Scope getRootScope();

	/**
	 * Returns the last scope of the path.
	 * 
	 * @return the last scope
	 */
	Scope getLastScope();

	/**
	 * Returns a scope path which consists of all scopes of this path except the last one.
	 * 
	 * @return the parent scope path
	 */
	ScopePath getParentPath();

	/**
	 * Finds the scope with the given name.
	 * 
	 * @param name
	 * @return the scope with the given name or <code>null</code> if not found
	 */
	Scope findScopeByName(String name);

	/**
	 * Indicates whether this scope path starts with the equal scopes and properties as the given path.
	 * 
	 * @param scopePath
	 * @return <code>true</code> if own path starts with equal scopes, has equal
	 *         scope-order and has equal properties and values as the given path
	 */
	boolean startsPathWith(ScopePath scopePath);

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);
}
