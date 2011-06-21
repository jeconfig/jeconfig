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
import org.jeconfig.api.annotation.merging.ListItemMergingStrategy;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.testconfigs.BaseClass;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.MyInterface;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class ArrayTestConfiguration {

	private int[] intField;

	private String[] stringField;

	private String[] stringField2;

	private ComplexSubtype[] complex;

	private BaseClass[] subClass;

	private MyInterface[] implClass;

	@ConfigArrayProperty
	public int[] getIntField() {
		return intField;
	}

	public void setIntField(final int[] intField) {
		this.intField = intField;
	}

	@ConfigArrayProperty(mergingStrategy = ListItemMergingStrategy.USE_PARENT)
	public String[] getStringField() {
		return stringField;
	}

	public void setStringField(final String[] stringField) {
		this.stringField = stringField;
	}

	@ConfigArrayProperty
	public String[] getStringField2() {
		return stringField2;
	}

	public void setStringField2(final String[] stringField2) {
		this.stringField2 = stringField2;
	}

	@ConfigArrayProperty
	public ComplexSubtype[] getComplex() {
		return complex;
	}

	public void setComplex(final ComplexSubtype[] complex) {
		this.complex = complex;
	}

	@ConfigArrayProperty(polymorph = true)
	public BaseClass[] getSubClass() {
		return subClass;
	}

	public void setSubClass(final BaseClass[] subClass) {
		this.subClass = subClass;
	}

	@ConfigArrayProperty(polymorph = true)
	public MyInterface[] getImplClass() {
		return implClass;
	}

	public void setImplClass(final MyInterface[] implClass) {
		this.implClass = implClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(complex);
		result = prime * result + Arrays.hashCode(implClass);
		result = prime * result + Arrays.hashCode(intField);
		result = prime * result + Arrays.hashCode(stringField);
		result = prime * result + Arrays.hashCode(stringField2);
		result = prime * result + Arrays.hashCode(subClass);
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
		if (!(obj instanceof ArrayTestConfiguration)) {
			return false;
		}
		final ArrayTestConfiguration other = (ArrayTestConfiguration) obj;
		if (!Arrays.equals(complex, other.complex)) {
			return false;
		}
		if (!Arrays.equals(implClass, other.implClass)) {
			return false;
		}
		if (!Arrays.equals(intField, other.intField)) {
			return false;
		}
		if (!Arrays.equals(stringField, other.stringField)) {
			return false;
		}
		if (!Arrays.equals(stringField2, other.stringField2)) {
			return false;
		}
		if (!Arrays.equals(subClass, other.subClass)) {
			return false;
		}
		return true;
	}

}
