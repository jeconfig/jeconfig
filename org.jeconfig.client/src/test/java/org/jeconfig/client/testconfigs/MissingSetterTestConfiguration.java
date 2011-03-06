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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class MissingSetterTestConfiguration {

	private int intValue;
	private String stringValue;
	private Double doubleValue;
	private Long longValue;
	private final BaseClass baseClass;
	private Set<Integer> set;
	private Map<String, Integer> map;
	private List<Double> list;
	private final short[] array;

	public MissingSetterTestConfiguration() {
		array = new short[5];
		baseClass = new BaseClass();
	}

	@ConfigSimpleProperty
	public int getIntValue() {
		return intValue;
	}

	@ConfigSimpleProperty
	public String getStringValue() {
		return stringValue;
	}

	@ConfigSimpleProperty
	public Double getDoubleValue() {
		return doubleValue;
	}

	@ConfigSimpleProperty
	public Long getLongValue() {
		return longValue;
	}

	@ConfigComplexProperty
	public BaseClass getBaseClass() {
		return baseClass;
	}

	@ConfigSetProperty(itemType = Integer.class)
	public Set<Integer> getSet() {
		return set;
	}

	@ConfigMapProperty
	public Map<String, Integer> getMap() {
		return map;
	}

	@ConfigListProperty(itemType = Double.class)
	public List<Double> getList() {
		return list;
	}

	@ConfigArrayProperty
	public short[] getArray() {
		return array;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		result = prime * result + ((baseClass == null) ? 0 : baseClass.hashCode());
		result = prime * result + ((doubleValue == null) ? 0 : doubleValue.hashCode());
		result = prime * result + intValue;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((longValue == null) ? 0 : longValue.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((set == null) ? 0 : set.hashCode());
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
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
		if (!(obj instanceof MissingSetterTestConfiguration)) {
			return false;
		}
		final MissingSetterTestConfiguration other = (MissingSetterTestConfiguration) obj;
		if (!Arrays.equals(array, other.array)) {
			return false;
		}
		if (baseClass == null) {
			if (other.baseClass != null) {
				return false;
			}
		} else if (!baseClass.equals(other.baseClass)) {
			return false;
		}
		if (doubleValue == null) {
			if (other.doubleValue != null) {
				return false;
			}
		} else if (!doubleValue.equals(other.doubleValue)) {
			return false;
		}
		if (intValue != other.intValue) {
			return false;
		}
		if (list == null) {
			if (other.list != null) {
				return false;
			}
		} else if (!list.equals(other.list)) {
			return false;
		}
		if (longValue == null) {
			if (other.longValue != null) {
				return false;
			}
		} else if (!longValue.equals(other.longValue)) {
			return false;
		}
		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}
		if (set == null) {
			if (other.set != null) {
				return false;
			}
		} else if (!set.equals(other.set)) {
			return false;
		}
		if (stringValue == null) {
			if (other.stringValue != null) {
				return false;
			}
		} else if (!stringValue.equals(other.stringValue)) {
			return false;
		}
		return true;
	}
}
