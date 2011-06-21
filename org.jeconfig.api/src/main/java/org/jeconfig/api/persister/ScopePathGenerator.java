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

package org.jeconfig.api.persister;

import java.util.Collection;
import java.util.Map;

import org.jeconfig.api.scope.ScopePath;

/**
 * Transforms IScopePaths into String form and vice versa.<br>
 * Needed by {@link ConfigPersister}s to serialize/deserialize IScopePaths.<br>
 * <br>
 * This interface may be implemented to match the requirements of a specific persister.
 * For example, a persister which persists files needs the ScopePath transformed into directory/file paths. <br>
 * <br>
 * This interface may be implemented by clients.
 */
public interface ScopePathGenerator {

	/**
	 * Creates a string with the value of 'className' property
	 * from 'classScope' inside the given scopePath.
	 * 
	 * @param scopePath the scopePath to look for the 'className' property
	 * @return the created name
	 */
	String createName(ScopePath scopePath);

	/**
	 * Creates a path (e.g. filePath) from the given scopePath
	 * 
	 * @param scopePath the scopePath to convert into path
	 * @return the path created from the given scopePath
	 */
	String getPathFromScopePath(ScopePath scopePath);

	/**
	 * Combines the given scopeName and properties.
	 * e.g. scopeName = user and property = username-hugo
	 * Result: user{File.separator}username-hugo
	 * 
	 * @param scopeName the name of the scope to combine
	 * @param properties the properties of the scope to combine
	 * @return a path with the scopeName and the Properties
	 */
	String buildScopeWithProperty(String scopeName, Map<String, String> properties);

	/**
	 * Creates scopePaths from the scopes inside the given paths,
	 * if a path contains the searched scope and properties.
	 * 
	 * @param paths the paths to create scopePaths from it
	 * @param scopeName the name of the searched scope
	 * @param properties the properties of the searched scope
	 * @return Collection of IScopePaths
	 */
	Collection<ScopePath> createScopePaths(Collection<String> paths, String scopeName, Map<String, String> properties);
}
