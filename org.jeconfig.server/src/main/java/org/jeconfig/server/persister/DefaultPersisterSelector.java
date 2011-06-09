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

package org.jeconfig.server.persister;

import java.util.Collection;

import org.jeconfig.api.persister.IPersisterSelector;
import org.jeconfig.api.scope.IScopePath;

/**
 * Default persister selector implementation.<br>
 * It can be used if only one configuration persister is registered at the persistence service.
 */
public final class DefaultPersisterSelector implements IPersisterSelector {

	@Override
	public String getPersisterId(final IScopePath scope, final Collection<String> configPersisterIds) {
		if (configPersisterIds.size() == 0) {
			throw new IllegalStateException(
				"DefaultPersisterSelector needs exactly one configuration persister! got: " + configPersisterIds.size()); //$NON-NLS-1$
		}
		if (configPersisterIds.size() > 1) {
			throw new IllegalStateException(
				"DefaultPersisterSelector needs exactly one configuration persister! got: " + configPersisterIds.size()); //$NON-NLS-1$
		}
		return configPersisterIds.iterator().next();
	}

}
