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

package org.jeconfig.client.annotation.configclass;

import java.util.ArrayList;
import java.util.List;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.annotation.merging.MergingStrategies;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {DefaultScopeDescriptor.NAME, GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME}, defaultConfigFactory = TestConfigurationDefaultFactory.class)
public class TestConfiguration {

	private String field1 = "test"; //$NON-NLS-1$

	private Integer field2 = Integer.valueOf(1);

	private List<Integer> simpleList = new ArrayList<Integer>();

	@ConfigSimpleProperty
	public String getField1() {
		return field1;
	}

	public void setField1(final String field1) {
		this.field1 = field1;
	}

	@ConfigSimpleProperty(mergingStrategy = MergingStrategies.ParentOverwrites.class)
	public Integer getField2() {
		return field2;
	}

	public void setField2(final Integer field2) {
		this.field2 = field2;
	}

	@ConfigListProperty(itemType = Integer.class)
	public List<Integer> getSimpleList() {
		return simpleList;
	}

	public void setSimpleList(final List<Integer> simpleList) {
		this.simpleList = simpleList;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "TestConfiguration [field1=" + field1 + ", field2=" + field2 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field1 == null) ? 0 : field1.hashCode());
		result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
		result = prime * result + ((simpleList == null) ? 0 : simpleList.hashCode());
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
		if (field1 == null) {
			if (other.field1 != null) {
				return false;
			}
		} else if (!field1.equals(other.field1)) {
			return false;
		}
		if (field2 == null) {
			if (other.field2 != null) {
				return false;
			}
		} else if (!field2.equals(other.field2)) {
			return false;
		}
		if (simpleList == null) {
			if (other.simpleList != null) {
				return false;
			}
		} else if (!simpleList.equals(other.simpleList)) {
			return false;
		}
		return true;
	}

}
