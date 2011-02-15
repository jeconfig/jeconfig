/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel, Lukas Gross
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

package org.jeconfig.exporter.test.configs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.junit.Ignore;

@Ignore
@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME})
public class TestConfiguration {

	private int i;

	private String s;

	private Float f;

	private List<Integer> list;

	private Map<String, Double> map;

	public TestConfiguration() {
		i = 45623;
		s = "dfdf";//$NON-NLS-1$
		f = Float.valueOf(67);
		list = new LinkedList<Integer>();
		map = new HashMap<String, Double>();
	}

	@ConfigSimpleProperty
	public int getI() {
		return i;
	}

	public void setI(final int i) {
		this.i = i;
	}

	@ConfigSimpleProperty
	public String getS() {
		return s;
	}

	public void setS(final String s) {
		this.s = s;
	}

	@ConfigSimpleProperty
	public Float getF() {
		return f;
	}

	public void setF(final Float f) {
		this.f = f;
	}

	@ConfigListProperty(itemType = Integer.class)
	public List<Integer> getList() {
		return list;
	}

	public void setList(final List<Integer> list) {
		this.list = list;
	}

	@ConfigMapProperty(keyType = String.class, valueType = Double.class)
	public Map<String, Double> getMap() {
		return map;
	}

	public void setMap(final Map<String, Double> map) {
		this.map = map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		result = prime * result + i;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((s == null) ? 0 : s.hashCode());
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
		if (!(obj instanceof TestConfiguration)) {
			return false;
		}
		final TestConfiguration other = (TestConfiguration) obj;
		if (f == null) {
			if (other.f != null) {
				return false;
			}
		} else if (!f.equals(other.f)) {
			return false;
		}
		if (i != other.i) {
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
		if (s == null) {
			if (other.s != null) {
				return false;
			}
		} else if (!s.equals(other.s)) {
			return false;
		}
		return true;
	}

}
