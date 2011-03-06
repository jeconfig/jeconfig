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

package org.jeconfig.client.testconfigs;

import org.jeconfig.api.IConfigService;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigSimpleProperty;

@ConfigComplexType
public class SubClass1 extends BaseClass implements IMyInterface, IMyInferfaceForNonPolymorphTest {
	private int someIntValue = 2;
	private String id;

	public SubClass1() {}

	@ConfigSimpleProperty
	public int getSomeIntValue() {
		return someIntValue;
	}

	public void setSomeIntValue(final int someIntValue) {
		this.someIntValue = someIntValue;
	}

	@ConfigSimpleProperty
	@Override
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public void doNothing() {}

	public static SubClass1 create(final IConfigService cs, final String someString, final int someInt) {
		return create(cs, someString, someInt, "1"); //$NON-NLS-1$
	}

	public static SubClass1 create(final IConfigService cs, final String someString, final int someInt, final String id) {
		final SubClass1 sub = cs.createComplexObject(SubClass1.class);
		sub.setSomeString(someString);
		sub.setSomeIntValue(someInt);
		sub.setId(id);
		return sub;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + someIntValue;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof SubClass1)) {
			return false;
		}
		final SubClass1 other = (SubClass1) obj;
		if (someIntValue != other.someIntValue) {
			return false;
		}
		return true;
	}

}
