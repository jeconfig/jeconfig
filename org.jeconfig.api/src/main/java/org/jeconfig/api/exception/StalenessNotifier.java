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

/**
 * A {@link StalenessNotifier} is informed when a configuration has been loaded and
 * one or more properties were stale. It could notify the user about this fact.<br>
 * <br>
 * Staleness notifiers can be registered globally or locally:
 * <ul>
 * <li>One global staleness notifier can be registered at the ConfigSetupService. It is used when no local notifier is registered
 * for a configuration class.</li>
 * <li>For each configuration class a local notifier can be registered using the @ConfigClass annotation.</li>
 * </ul>
 */
public interface StalenessNotifier {

	/**
	 * Informs the notifier that a configuration has been loaded and merged which
	 * had at least one stale property. The user could be informed about this fact now.<br>
	 * The merging result holds information which configuration has been loaded and how the
	 * stale properties were handled.
	 * 
	 * @param mergingResult holds some information about the load/merge step
	 */
	void loadedStaleConfig(StalePropertiesMergingResult mergingResult);
}
