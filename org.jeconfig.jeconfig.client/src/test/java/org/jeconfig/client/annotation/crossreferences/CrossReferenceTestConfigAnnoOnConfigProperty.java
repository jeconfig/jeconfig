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

package org.jeconfig.client.annotation.crossreferences;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.scope.GlobalScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME})
public class CrossReferenceTestConfigAnnoOnConfigProperty {

	private CrossReferenceTestConfigReferenced referencedConfig;

	@ConfigComplexProperty
	@ConfigCrossReference
	public CrossReferenceTestConfigReferenced getReferencedConfig() {
		return referencedConfig;
	}

	public void setReferencedConfig(final CrossReferenceTestConfigReferenced referencedConfig) {
		this.referencedConfig = referencedConfig;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((referencedConfig == null) ? 0 : referencedConfig.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CrossReferenceTestConfigAnnoOnConfigProperty)) {
			return false;
		}
		final CrossReferenceTestConfigAnnoOnConfigProperty other = (CrossReferenceTestConfigAnnoOnConfigProperty) obj;
		if (referencedConfig == null) {
			if (other.referencedConfig != null) {
				return false;
			}
		} else if (!referencedConfig.equals(other.referencedConfig)) {
			return false;
		}
		return true;
	}

}
