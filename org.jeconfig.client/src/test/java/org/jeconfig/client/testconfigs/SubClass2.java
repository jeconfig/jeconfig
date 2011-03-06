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
public class SubClass2 extends BaseClass implements IMyInterface, IMyInferfaceForNonPolymorphTest {
	private float someFloatValue = 0.99f;
	private String id;

	public SubClass2() {}

	@ConfigSimpleProperty
	public float getSomeFloatValue() {
		return someFloatValue;
	}

	public void setSomeFloatValue(final float someFloatValue) {
		this.someFloatValue = someFloatValue;
	}

	@Override
	public void doNothing() {}

	@ConfigSimpleProperty
	@Override
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public static SubClass2 create(final IConfigService cs, final String someString, final float someFloat) {
		return create(cs, someString, someFloat, "1"); //$NON-NLS-1$
	}

	public static SubClass2 create(final IConfigService cs, final String someString, final float someFloat, final String id) {
		final SubClass2 sub = cs.createComplexObject(SubClass2.class);
		sub.setSomeString(someString);
		sub.setSomeFloatValue(someFloat);
		sub.setId(id);
		return sub;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(someFloatValue);
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
		if (!(obj instanceof SubClass2)) {
			return false;
		}
		final SubClass2 other = (SubClass2) obj;
		if (Float.floatToIntBits(someFloatValue) != Float.floatToIntBits(other.someFloatValue)) {
			return false;
		}
		return true;
	}

}
