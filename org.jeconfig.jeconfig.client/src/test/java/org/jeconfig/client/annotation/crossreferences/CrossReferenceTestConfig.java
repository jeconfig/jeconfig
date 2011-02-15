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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.InstanceScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME})
public class CrossReferenceTestConfig {

	private CrossReferenceTestConfigReferenced referenceConfig;
	private CrossReferenceTestSubConfig complex;
	private CrossReferenceTestSubConfig[] array;
	private List<CrossReferenceTestSubConfig> list;
	private Set<CrossReferenceTestSubConfig> set;
	private Map<String, CrossReferenceTestSubConfig> map;
	private CrossReferenceTestConfigReferenced instanceReferenceConfig;

	@ConfigCrossReference
	public CrossReferenceTestConfigReferenced getReferenceConfig() {
		return referenceConfig;
	}

	public void setReferenceConfig(final CrossReferenceTestConfigReferenced referenceConfig) {
		this.referenceConfig = referenceConfig;
	}

	@ConfigCrossReference(scopePath = {DefaultScopeDescriptor.NAME, GlobalScopeDescriptor.NAME, InstanceScopeDescriptor.NAME}, instanceName = "FirstInstance")
	public CrossReferenceTestConfigReferenced getInstanceReferenceConfig() {
		return instanceReferenceConfig;
	}

	public void setInstanceReferenceConfig(final CrossReferenceTestConfigReferenced instanceReferenceConfig) {
		this.instanceReferenceConfig = instanceReferenceConfig;
	}

	@ConfigComplexProperty
	public CrossReferenceTestSubConfig getComplex() {
		return complex;
	}

	public void setComplex(final CrossReferenceTestSubConfig complex) {
		this.complex = complex;
	}

	@ConfigArrayProperty
	public CrossReferenceTestSubConfig[] getArray() {
		return array;
	}

	public void setArray(final CrossReferenceTestSubConfig[] array) {
		this.array = array;
	}

	@ConfigListProperty(itemType = Object.class, polymorph = true)
	public List<CrossReferenceTestSubConfig> getList() {
		return list;
	}

	public void setList(final List<CrossReferenceTestSubConfig> list) {
		this.list = list;
	}

	@ConfigSetProperty(itemType = CrossReferenceTestSubConfig.class)
	public Set<CrossReferenceTestSubConfig> getSet() {
		return set;
	}

	public void setSet(final Set<CrossReferenceTestSubConfig> set) {
		this.set = set;
	}

	@ConfigMapProperty(valueType = CrossReferenceTestSubConfig.class)
	public Map<String, CrossReferenceTestSubConfig> getMap() {
		return map;
	}

	public void setMap(final Map<String, CrossReferenceTestSubConfig> map) {
		this.map = map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		result = prime * result + ((complex == null) ? 0 : complex.hashCode());
		result = prime * result + ((instanceReferenceConfig == null) ? 0 : instanceReferenceConfig.hashCode());
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((referenceConfig == null) ? 0 : referenceConfig.hashCode());
		result = prime * result + ((set == null) ? 0 : set.hashCode());
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
		if (!(obj instanceof CrossReferenceTestConfig)) {
			return false;
		}
		final CrossReferenceTestConfig other = (CrossReferenceTestConfig) obj;
		if (!Arrays.equals(array, other.array)) {
			return false;
		}
		if (complex == null) {
			if (other.complex != null) {
				return false;
			}
		} else if (!complex.equals(other.complex)) {
			return false;
		}
		if (instanceReferenceConfig == null) {
			if (other.instanceReferenceConfig != null) {
				return false;
			}
		} else if (!instanceReferenceConfig.equals(other.instanceReferenceConfig)) {
			return false;
		}
		if (list == null) {
			if (other.list != null) {
				return false;
			}
		} else if (!list.equals(other.list)) {
			return false;
		}
		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}
		if (referenceConfig == null) {
			if (other.referenceConfig != null) {
				return false;
			}
		} else if (!referenceConfig.equals(other.referenceConfig)) {
			return false;
		}
		if (set == null) {
			if (other.set != null) {
				return false;
			}
		} else if (!set.equals(other.set)) {
			return false;
		}
		return true;
	}

}
