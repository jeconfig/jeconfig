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

/**
 * Configuration persister which manages all concrete configuration persisters.<br>
 * Uses a {@link IPersisterSelector} to decide which concrete persister(s) should be used to
 * perform a request and delegates the request to it/them.<br>
 * <br>
 * In a client-server environment, this service should be made remotely accessible for the clients.
 * The configuration service should get this service as its configuration persister.<br>
 * <br>
 * To set up this service all desired concrete configuration persisters should be registered.<br>
 * If more than one persisters should be used, a custom persister selector must be set which decides
 * which persister should be used per scope path.
 */
public interface IConfigPersistenceService extends IConfigPersister {

	/**
	 * Sets the selector which decides which concrete configuration persister
	 * should be used per scope path.
	 * 
	 * @param persisterSelector selector which decides which configuration persister
	 *            should be used per scope path
	 */
	void setPersisterSelector(IPersisterSelector persisterSelector);

	/**
	 * Adds a configuration persister.
	 * 
	 * @param configPersister
	 */
	void addConfigPersister(IConfigPersister configPersister);

	/**
	 * Removes a configuration persister.
	 * 
	 * @param configPersister
	 */
	void removeConfigPersister(IConfigPersister configPersister);

	/**
	 * Enables or disables the cache which holds loaded/saved configurations.<br>
	 * Caching is enabled by default.<br>
	 * <br>
	 * <b>When caching is enabled {@link #loadConfiguration} might not
	 * return the latest configurations if they were modified directly in the data store!</b>
	 * 
	 * @param enabled
	 */
	void setCacheEnabled(boolean enabled);
}
