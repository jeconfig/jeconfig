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
 * A configuration exception handler is used by the ConfigServiceAccessor to handle exceptions.<br>
 * The exception handler should log exceptions and inform the user about them if desired. The exception
 * handler also provides a strategies how to continue after failures.<br>
 * <br>
 * Exception handlers are used in conjunction with the ConfigServiceAccessor. They can be used if you
 * don't want to handle exceptions on each invocation of configuration service methods and if you want to handle
 * exceptions centrally instead.<br>
 * <br>
 * This interface may be implemented by clients.
 */
public interface ConfigExceptionHandler {

	/**
	 * Informs the exception handler that a load operation has failed.<br>
	 * It must provide a strategy how the configuration service should continue after that.
	 * 
	 * @param scopePath the scope path of the configuration which was attempted to load
	 * @param configClass the class of the configuration which was attempted to load
	 * @param e the exception which was thrown on load
	 * @param retryCount indicates how often the load was attempted; can be used to prevent endless loops
	 * @return a strategy how the configuration service should continue
	 */
	LoadFailureSolutionStrategy loadFailed(final ScopePath scopePath, Class<?> configClass, Exception e, int retryCount);

	/**
	 * Informs the exception handler that a save operation has failed.<br>
	 * It must provide a strategy how the configuration service should continue after that.
	 * 
	 * @param scopePath the scope path of the configuration which was attempted to save
	 * @param config the configuration object which was attempted to save
	 * @param e the exception which was thrown on save
	 * @param retryCount indicates how often the load was attempted; can be used to prevent endless loops
	 * @return a strategy how the configuration service should continue
	 */
	SaveFailureSolutionStrategy saveFailed(final ScopePath scopePath, Object config, Exception e, int retryCount);

	/**
	 * Informs the exception handler that a refresh operation has failed.<br>
	 * It must provide a strategy how the configuration service should continue after that.
	 * 
	 * @param scopePath the scope path of the configuration which was attempted to refresh
	 * @param config the configuration object which was attempted to refresh
	 * @param e the exception which was thrown on refresh
	 * @param retryCount indicates how often the load was attempted; can be used to prevent endless loops
	 * @return a strategy how the configuration service should continue
	 */
	RefreshFailureSolutionStrategy refreshFailed(final ScopePath scopePath, Object config, Exception e, int retryCount);
}
