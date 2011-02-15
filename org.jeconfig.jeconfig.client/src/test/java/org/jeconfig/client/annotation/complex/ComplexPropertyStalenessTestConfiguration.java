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

package org.jeconfig.client.annotation.complex;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.DummyStalenessNotifier;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, DefaultScopeDescriptor.NAME, UserScopeDescriptor.NAME}, stalenessNotfier = DummyStalenessNotifier.class)
public class ComplexPropertyStalenessTestConfiguration {
	private ComplexSubtype property1;
	private ComplexSubtype property2;

	@ConfigComplexProperty(stalenessSolutionStrategy = StalenessSolutionStrategy.USE_PARENT)
	public ComplexSubtype getProperty1() {
		return property1;
	}

	public void setProperty1(final ComplexSubtype property1) {
		this.property1 = property1;
	}

	@ConfigComplexProperty(stalenessSolutionStrategy = StalenessSolutionStrategy.MERGE)
	public ComplexSubtype getProperty2() {
		return property2;
	}

	public void setProperty2(final ComplexSubtype property2) {
		this.property2 = property2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((property1 == null) ? 0 : property1.hashCode());
		result = prime * result + ((property2 == null) ? 0 : property2.hashCode());
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
		if (!(obj instanceof ComplexPropertyStalenessTestConfiguration)) {
			return false;
		}
		final ComplexPropertyStalenessTestConfiguration other = (ComplexPropertyStalenessTestConfiguration) obj;
		if (property1 == null) {
			if (other.property1 != null) {
				return false;
			}
		} else if (!property1.equals(other.property1)) {
			return false;
		}
		if (property2 == null) {
			if (other.property2 != null) {
				return false;
			}
		} else if (!property2.equals(other.property2)) {
			return false;
		}
		return true;
	}

}
