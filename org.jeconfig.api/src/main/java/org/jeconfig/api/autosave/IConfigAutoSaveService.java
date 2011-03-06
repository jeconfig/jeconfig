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

package org.jeconfig.api.autosave;

import org.jeconfig.api.exception.IConfigExceptionHandler;
import org.jeconfig.api.scope.IScopePath;

/**
 * <b>use with caution!!! no transactions are supported</b><br>
 * 
 * Service which is able to save configurations automatically at a certain interval.
 */
public interface IConfigAutoSaveService {

	// TODO do we have to handle concurrency issues? A configuration could be accessed by client during save.

	/**
	 * Sets the interval this service should save dirty configurations.
	 * 
	 * @param millis the save interval in milli seconds
	 */
	void setAutoSaveInterval(long millis);

	/**
	 * <b>use with caution!!! no transactions are supported.</b><br>
	 * Registers a configuration to be auto-saved if it is modified.<br>
	 * <br>
	 * There is no possibility to unregister configurations because configurations
	 * are not referenced by this service while they are not modified.
	 * 
	 * @param <T> the type of the configuration
	 * @param config the configuration which should be auto-saved
	 * @param exceptionHandler handles exceptions if errors occur during save
	 */
	<T> void manageConfig(T config, IConfigExceptionHandler exceptionHandler);

	/**
	 * Determines whether a configuration with the given scope path is managed by this service and is modified.
	 * 
	 * @param scopePath the scope path of the configuration to check
	 * @return <code>true</code> if a modified configuration of the scope path is managed
	 */
	boolean hasDirtyConfig(IScopePath scopePath);

	/**
	 * Immediately saves all managed configuration which are currently modified.
	 */
	void flush();

	/**
	 * Saves currently modified managed configurations and closes this service.
	 */
	void close();
}
