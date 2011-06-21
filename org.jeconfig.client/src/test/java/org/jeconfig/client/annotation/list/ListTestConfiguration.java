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

import java.util.ArrayList;
import java.util.List;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.merging.ListItemMergingStrategy;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.testconfigs.BaseClass;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.MyInterface;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class ListTestConfiguration {

	private List<Integer> intField;

	private List<String> stringField = new ArrayList<String>();

	private List<ComplexSubtype> complex;

	private List<BaseClass> subClass;

	private List<MyInterface> implClass;

	@ConfigListProperty(itemType = Integer.class)
	public List<Integer> getIntField() {
		return intField;
	}

	public void setIntField(final List<Integer> intField) {
		this.intField = intField;
	}

	@ConfigListProperty(itemType = String.class, mergingStrategy = ListItemMergingStrategy.USE_PARENT)
	public List<String> getStringField() {
		return stringField;
	}

	public void setStringField(final List<String> stringField) {
		this.stringField = stringField;
	}

	@ConfigListProperty(itemType = ComplexSubtype.class)
	public List<ComplexSubtype> getComplex() {
		return complex;
	}

	public void setComplex(final List<ComplexSubtype> complex) {
		this.complex = complex;
	}

	@ConfigListProperty(itemType = BaseClass.class, polymorph = true)
	public List<BaseClass> getSubClass() {
		return subClass;
	}

	public void setSubClass(final List<BaseClass> subClass) {
		this.subClass = subClass;
	}

	@ConfigListProperty(polymorph = true, itemType = MyInterface.class)
	public List<MyInterface> getImplClass() {
		return implClass;
	}

	public void setImplClass(final List<MyInterface> implClass) {
		this.implClass = implClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((complex == null) ? 0 : complex.hashCode());
		result = prime * result + ((implClass == null) ? 0 : implClass.hashCode());
		result = prime * result + ((intField == null) ? 0 : intField.hashCode());
		result = prime * result + ((stringField == null) ? 0 : stringField.hashCode());
		result = prime * result + ((subClass == null) ? 0 : subClass.hashCode());
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
		if (!(obj instanceof ListTestConfiguration)) {
			return false;
		}
		final ListTestConfiguration other = (ListTestConfiguration) obj;
		if (complex == null) {
			if (other.complex != null) {
				return false;
			}
		} else if (!complex.equals(other.complex)) {
			return false;
		}
		if (implClass == null) {
			if (other.implClass != null) {
				return false;
			}
		} else if (!implClass.equals(other.implClass)) {
			return false;
		}
		if (intField == null) {
			if (other.intField != null) {
				return false;
			}
		} else if (!intField.equals(other.intField)) {
			return false;
		}
		if (stringField == null) {
			if (other.stringField != null) {
				return false;
			}
		} else if (!stringField.equals(other.stringField)) {
			return false;
		}
		if (subClass == null) {
			if (other.subClass != null) {
				return false;
			}
		} else if (!subClass.equals(other.subClass)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "ListTestConfiguration [complex="
			+ complex
			+ ", implClass="
			+ implClass
			+ ", intField="
			+ intField
			+ ", stringField="
			+ stringField
			+ ", subClass="
			+ subClass
			+ "]";
	}

}
