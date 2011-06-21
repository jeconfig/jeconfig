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
 * Describes the scope which addresses the 'class' instance of a configuration.<br>
 * The class scope is automatically used as the root of each scope path.<br>
 * <br>
 * The 'class' instances of the configurations are never persisted in the repositories. It is the
 * object which is created by the default constructor of the configuration class.
 */
public final class ClassScopeDescriptor implements ScopeDescriptor {
	public static final String NAME = "class"; //$NON-NLS-1$

	/**
	 * The name of the mandatory property which holds the name of the configuration class of the configuration.
	 */
	public static final String PROP_CLASS_NAME = "className"; //$NON-NLS-1$

	private static final String[] MANDATORY_PROPERTIES = new String[] {PROP_CLASS_NAME};

	@Override
	public String[] getMandatoryProperties() {
		return MANDATORY_PROPERTIES;
	}

	@Override
	public String getScopeName() {
		return NAME;
	}

}
