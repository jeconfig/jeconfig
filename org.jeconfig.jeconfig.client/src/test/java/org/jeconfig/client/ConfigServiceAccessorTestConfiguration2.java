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

package org.jeconfig.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME, InstanceScopeDescriptor.NAME})
public class ConfigServiceAccessorTestConfiguration2 {

	private Map<Object, Object> map;
	private Set<Object> set;
	private List<Object> list;

	public ConfigServiceAccessorTestConfiguration2() {
		map = new HashMap<Object, Object>();
		set = new HashSet<Object>();
		list = new LinkedList<Object>();
	}

	@ConfigMapProperty(polymorph = true)
	public Map<Object, Object> getMap() {
		return map;
	}

	public void setMap(final Map<Object, Object> map) {
		this.map = map;
	}

	@ConfigSetProperty(itemType = Object.class, polymorph = true)
	public Set<Object> getSet() {
		return set;
	}

	public void setSet(final Set<Object> set) {
		this.set = set;
	}

	@ConfigListProperty(itemType = Object.class, polymorph = true)
	public List<Object> getList() {
		return list;
	}

	public void setList(final List<Object> list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
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
		if (!(obj instanceof ConfigServiceAccessorTestConfiguration2)) {
			return false;
		}
		final ConfigServiceAccessorTestConfiguration2 other = (ConfigServiceAccessorTestConfiguration2) obj;
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
