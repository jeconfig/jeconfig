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

package org.jeconfig.client.internal.merging;

import java.util.HashSet;
import java.util.Set;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;

public abstract class AbstractPropertyMerger implements IPropertyMerger {

	protected boolean isChildStale(final IConfigDTO parentDTO, final IConfigDTO childDTO) {
		final boolean stale = !parentDTO.getDefiningScopePath().getLastScope().getName().equals(childDTO.getParentScopeName())
			|| parentDTO.getVersion() != childDTO.getParentVersion();

		return stale;
	}

	protected boolean isClassOrCodeDefaultDTO(final IConfigDTO configDTO) {
		if (ClassScopeDescriptor.NAME.equals(configDTO.getDefiningScopePath().getLastScope().getName())
			|| CodeDefaultScopeDescriptor.NAME.equals(configDTO.getDefiningScopePath().getLastScope().getName())) {
			return true;
		}
		return false;
	}

	protected IConfigDTO createChildWithMissingProperties(final IConfigDTO parentDTO, final IConfigDTO childDTO) {
		if (parentDTO instanceof ComplexConfigDTO) {
			final ComplexConfigDTO complexParentDTO = (ComplexConfigDTO) parentDTO;
			final ComplexConfigDTO result = ((ComplexConfigDTO) childDTO).deepCopy();
			final Set<String> missingProperties = new HashSet<String>(complexParentDTO.getDeclaredProperties());
			missingProperties.removeAll(result.getDeclaredProperties());
			if (missingProperties.size() > 0) {
				for (final String missingProperty : missingProperties) {
					result.addProperty(complexParentDTO.getProperty(missingProperty));
				}
			}
			return result;
		}

		return childDTO;
	}
}
