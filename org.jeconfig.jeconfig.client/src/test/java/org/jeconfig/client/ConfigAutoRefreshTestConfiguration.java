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

import java.util.LinkedList;
import java.util.List;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME, InstanceScopeDescriptor.NAME})
public class ConfigAutoRefreshTestConfiguration {

	private int field1;
	private String test;
	private List<Boolean> list;

	public ConfigAutoRefreshTestConfiguration() {
		list = new LinkedList<Boolean>();
		list.add(Boolean.TRUE);
		test = "sdfsdf"; //$NON-NLS-1$
		field1 = 7;
	}

	@ConfigSimpleProperty
	public int getField1() {
		return field1;
	}

	public void setField1(final int field1) {
		this.field1 = field1;
	}

	@ConfigSimpleProperty
	public String getTest() {
		return test;
	}

	public void setTest(final String test) {
		this.test = test;
	}

	@ConfigListProperty(itemType = Boolean.class)
	public List<Boolean> getList() {
		return list;
	}

	public void setList(final List<Boolean> list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + field1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((test == null) ? 0 : test.hashCode());
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
		if (!(obj instanceof ConfigAutoRefreshTestConfiguration)) {
			return false;
		}
		final ConfigAutoRefreshTestConfiguration other = (ConfigAutoRefreshTestConfiguration) obj;
		if (field1 != other.field1) {
			return false;
		}
		if (list == null) {
			if (other.list != null) {
				return false;
			}
		} else if (!list.equals(other.list)) {
			return false;
		}
		if (test == null) {
			if (other.test != null) {
				return false;
			}
		} else if (!test.equals(other.test)) {
			return false;
		}
		return true;
	}

}
