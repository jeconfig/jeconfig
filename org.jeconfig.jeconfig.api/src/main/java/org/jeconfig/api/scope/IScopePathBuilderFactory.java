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
 * Creates {@link IScopePath}s. <br>
 * <br>
 * A scope factory is always created for a configuration class annotated with @ConfigClass.<br>
 * It can only create scopes for this single class.<br>
 * This means that every scope created with the factory has a root scope with the name "class" which
 * is attached to the configuration class. <br>
 * <br>
 * 
 * There are several ways to create a scope:
 * <ul>
 * <li>Use the complete scope of the @ConfigClass annotation of the configuration using {@link #annotatedPath()}</li>
 * <li>Use only a part of the scope of the @ConfigClass annotation of the configuration using {@link #annotatedPathUntil(String)}</li>
 * <li>Create a completely new scope using {@link #stub()}
 * </ul>
 * You can append new scope parts to any of these scopes using {@link IScopePathBuilder}.
 */
public interface IScopePathBuilderFactory {

	/**
	 * Creates a new scope path which only consists of the two mandatory root scopes 'class' and 'codeDefault'. <br>
	 * You can append new children to the stub using {@link IScopePathBuilder}. <br>
	 * <br>
	 * The 'class' scope is attached to the configuration class this factory has been created for.
	 * 
	 * @return a new scope path
	 */
	IScopePathBuilder stub();

	/**
	 * Creates a new scope path which consists of the two mandatory root scopes 'class' and 'codeDefault' and
	 * all scopes of the @ConfigClass annotation of the configuration class this factory has been created for.<br>
	 * <br>
	 * The 'class' scope is attached for the configuration class this factory has been created for. <br>
	 * <br>
	 * All scopes are initialized with the properties of the registered scope property providers.
	 * 
	 * @return a new scope path
	 */
	IScopePathBuilder annotatedPath();

	/**
	 * Creates a new scope path which consists of the two mandatory root scopes 'class' and 'codeDefault' and
	 * all scopes of the @ConfigClass annotation of the configuration class this factory has been created for
	 * until the scope with the 'lastScope' name.<br>
	 * <br>
	 * The 'class' scope is attached for the configuration class this factory has been created for. <br>
	 * <br>
	 * All scopes are initialized with the properties of the registered scope property providers.
	 * 
	 * @param lastScope the name of the scope which should be the last scope of the new scope
	 * @return a new scope path
	 */
	IScopePathBuilder annotatedPathUntil(final String lastScope);

}
