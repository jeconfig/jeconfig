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

package org.jeconfig.api;

import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.exception.IStalenessNotifier;
import org.jeconfig.api.scope.IScopeRegistry;

/**
 * Configuration service setup API.<br>
 * 
 * Implementors must be thread-safe.<br>
 * <br>
 * This interface is not intended to be implemented by clients.
 */
public interface IConfigSetupService {

	/**
	 * Provides the scope registry which holds all scopes which may be used by this configuration service.
	 * 
	 * @return scopeRegistry
	 */
	IScopeRegistry getScopeRegistry();

	/**
	 * Provides the registry which holds the simple type converters.
	 * 
	 * @return converterRegistry
	 */
	ISimpleTypeConverterRegistry getSimpleTypeConverterRegistry();

	/**
	 * Enables or disables the client cache which holds loaded/saved configurations.
	 * 
	 * @param enabled
	 */
	void setClientCacheEnabled(boolean enabled);

	/**
	 * A staleness notifier which is informed when a configuration has been loaded and one or more properties were stale.<br>
	 * Only used if the configuration has no local staleness notifier.
	 * 
	 * @param stalenessNotifier
	 */
	void setGlobalStalenessNotifier(IStalenessNotifier stalenessNotifier);
}
