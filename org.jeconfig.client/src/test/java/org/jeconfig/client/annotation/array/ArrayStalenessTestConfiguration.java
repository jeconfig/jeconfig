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

package org.jeconfig.client.annotation.array;

import java.util.Arrays;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.testconfigs.DummyStalenessNotifier;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, DefaultScopeDescriptor.NAME, UserScopeDescriptor.NAME}, stalenessNotfier = DummyStalenessNotifier.class)
public class ArrayStalenessTestConfiguration {
	private int[] field1;
	private int[] field2;

	@ConfigArrayProperty(stalenessSolutionStrategy = StalenessSolutionStrategy.USE_PARENT)
	public int[] getField1() {
		return field1;
	}

	public void setField1(final int[] field1) {
		this.field1 = field1;
	}

	@ConfigArrayProperty(stalenessSolutionStrategy = StalenessSolutionStrategy.MERGE)
	public int[] getField2() {
		return field2;
	}

	public void setField2(final int[] field2) {
		this.field2 = field2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(field1);
		result = prime * result + Arrays.hashCode(field2);
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
		if (!(obj instanceof ArrayStalenessTestConfiguration)) {
			return false;
		}
		final ArrayStalenessTestConfiguration other = (ArrayStalenessTestConfiguration) obj;
		if (!Arrays.equals(field1, other.field1)) {
			return false;
		}
		if (!Arrays.equals(field2, other.field2)) {
			return false;
		}
		return true;
	}

}
