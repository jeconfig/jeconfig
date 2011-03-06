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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;

@ConfigClass(scopePath = {DefaultScopeDescriptor.NAME, GlobalScopeDescriptor.NAME})
public class CrossReferenceTestConfigReferenced {

	private String serverIp;
	private String serverPort;
	private Map<String, String> map;
	private Set<String> set;
	private List<String> list;

	@ConfigMapProperty()
	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(final Map<String, String> map) {
		this.map = map;
	}

	@ConfigSetProperty(itemType = String.class)
	public Set<String> getSet() {
		return set;
	}

	public void setSet(final Set<String> set) {
		this.set = set;
	}

	@ConfigListProperty(itemType = String.class)
	public List<String> getList() {
		return list;
	}

	public void setList(final List<String> list) {
		this.list = list;
	}

	@ConfigSimpleProperty
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(final String serverIp) {
		this.serverIp = serverIp;
	}

	@ConfigSimpleProperty
	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(final String serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((serverIp == null) ? 0 : serverIp.hashCode());
		result = prime * result + ((serverPort == null) ? 0 : serverPort.hashCode());
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
		if (!(obj instanceof CrossReferenceTestConfigReferenced)) {
			return false;
		}
		final CrossReferenceTestConfigReferenced other = (CrossReferenceTestConfigReferenced) obj;
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
		if (serverIp == null) {
			if (other.serverIp != null) {
				return false;
			}
		} else if (!serverIp.equals(other.serverIp)) {
			return false;
		}
		if (serverPort == null) {
			if (other.serverPort != null) {
				return false;
			}
		} else if (!serverPort.equals(other.serverPort)) {
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
