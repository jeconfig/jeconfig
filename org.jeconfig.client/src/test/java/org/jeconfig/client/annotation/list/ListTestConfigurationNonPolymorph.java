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

package org.jeconfig.client.annotation.list;

import java.util.List;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.testconfigs.MyInferfaceForNonPolymorphTest;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class ListTestConfigurationNonPolymorph {

	private List<MyInferfaceForNonPolymorphTest> implClass2;

	@ConfigListProperty(itemType = MyInferfaceForNonPolymorphTest.class)
	public List<MyInferfaceForNonPolymorphTest> getImplClass2() {
		return implClass2;
	}

	public void setImplClass2(final List<MyInferfaceForNonPolymorphTest> implClass2) {
		this.implClass2 = implClass2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((implClass2 == null) ? 0 : implClass2.hashCode());
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
		if (!(obj instanceof ListTestConfigurationNonPolymorph)) {
			return false;
		}
		final ListTestConfigurationNonPolymorph other = (ListTestConfigurationNonPolymorph) obj;
		if (implClass2 == null) {
			if (other.implClass2 != null) {
				return false;
			}
		} else if (!implClass2.equals(other.implClass2)) {
			return false;
		}
		return true;
	}

}
