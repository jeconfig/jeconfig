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

import org.jeconfig.api.ConfigService;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigSimpleProperty;

@ConfigComplexType
public class SubClass3 extends AbstractClass {

	private String test;
	private int value;

	public static SubClass3 create(final ConfigService cs, final String test, final int value) {
		final SubClass3 sub = cs.createComplexObject(SubClass3.class);
		sub.setTest(test);
		sub.setValue(value);
		return sub;
	}

	@ConfigSimpleProperty
	public String getTest() {
		return test;
	}

	public void setTest(final String test) {
		this.test = test;
	}

	@ConfigSimpleProperty
	public int getValue() {
		return value;
	}

	public void setValue(final int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((test == null) ? 0 : test.hashCode());
		result = prime * result + value;
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
		if (!(obj instanceof SubClass3)) {
			return false;
		}
		final SubClass3 other = (SubClass3) obj;
		if (test == null) {
			if (other.test != null) {
				return false;
			}
		} else if (!test.equals(other.test)) {
			return false;
		}
		if (value != other.value) {
			return false;
		}
		return true;
	}
}
