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
import java.util.Map;

/**
 * Single logical part of an {@link IScopePath} which addresses a configuration instance.<br>
 */
public interface IScope extends Serializable {

	/**
	 * The scope name, e.g. "user".
	 * 
	 * @return the scope name
	 */
	String getName();

	/**
	 * Returns the value of the property with the given key.
	 * 
	 * @param key the property key
	 * @return the property value
	 */
	String getProperty(String key);

	/**
	 * Returns a new map holding the currently set properties of the scope.
	 * 
	 * @return a new map holding the properties
	 */
	Map<String, String> getProperties();

	/**
	 * Indicates whether this scope has all of the given properties.
	 * 
	 * @param properties the properties
	 * @return <code>true</code> if the scope contains the properties with equal keys and values
	 */
	boolean containsAllProperties(Map<String, String> properties);

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);
}
