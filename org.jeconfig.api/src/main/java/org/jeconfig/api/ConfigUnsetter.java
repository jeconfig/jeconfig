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

/**
 * Utility class to unset configuration properties.<br>
 * <br>
 * ConfigUnsetter instances can be obtained from the {@link ConfigService}.<br>
 * <br>
 * This interface is not intended to be implemented by clients.
 */
public interface ConfigUnsetter {

	/**
	 * Indicates whether unset is possible on the given configuration object.<br>
	 * This method should be invoked before all unset operations.
	 * 
	 * @param config
	 * @return <code>true</code> if unset is possible
	 */
	boolean canUnsetConfig(Object config);

	/**
	 * Unsets the given properties of the given configuration object.<br>
	 * The values of the current scope path are discarded and the merged parent values are set on the object.
	 * 
	 * @param config
	 * @param properties
	 */
	void unsetProperties(Object config, final String... properties);

	/**
	 * Unsets all properties of the given configuration object.<br>
	 * The values of the current scope path are discarded and the merged parent values are set on the object.
	 * 
	 * @param config
	 */
	void unsetAllProperties(Object config);

	/**
	 * Indicates whether a property is set at the current scope path or whether the merged parent value is currently used.
	 * 
	 * @param config
	 * @param property
	 * @return <code>true</code> if the property is set; <code>false</code> if the parent value is used
	 */
	boolean isPropertySet(Object config, final String property);

}
