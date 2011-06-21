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

package org.jeconfig.api.exception;

import org.jeconfig.api.scope.ScopePath;

/**
 * Indicates that a configuration could not be loaded or saved because it is stale.<br>
 * <br>
 * A configuration is stale if
 * <ul>
 * <li>the class version of a loaded configuration is older than the current class version and no suitable migration transformers
 * are configured</li>
 * <li>a new configuration should be saved but meanwhile the configuration exists in the repository (somebody else also created
 * it)</li>
 * <li>the version of a configuration to save is <= the version of the configuration of the repository (somebody else has updated
 * it)</li>
 * <li>a configuration should be saved but it doesn't longer exist in the repository (somebody else has deleted it)</li>
 * </ul>
 */
public final class StaleConfigException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final ScopePath scopePath;

	public StaleConfigException(final ScopePath scope, final String message, final Throwable cause) {
		super(message, cause);
		this.scopePath = scope;
	}

	public StaleConfigException(final ScopePath scope, final String message) {
		super(message);
		this.scopePath = scope;
	}

	/**
	 * @return the scope path of the configuration which is stale
	 */
	public ScopePath getScopePath() {
		return scopePath;
	}

}
