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

package org.jeconfig.client.annotation.set;

import org.jeconfig.api.ConfigService;
import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigIdProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.annotation.merging.MergingStrategies;

@ConfigComplexType
public class ConfigIdComplexTypeTestConfiguration4 {
	private int id;
	private String name;

	public ConfigIdComplexTypeTestConfiguration4() {}

	public ConfigIdComplexTypeTestConfiguration4(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	@ConfigIdProperty
	@ConfigSimpleProperty(mergingStrategy = MergingStrategies.ChildOverwrites.class)
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@ConfigSimpleProperty
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public static ConfigIdComplexTypeTestConfiguration4 create(final ConfigService cs, final int id, final String name) {
		final ConfigIdComplexTypeTestConfiguration4 cfg = cs.createComplexObject(ConfigIdComplexTypeTestConfiguration4.class);
		cfg.setId(id);
		cfg.setName(name);
		return cfg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof ConfigIdComplexTypeTestConfiguration4)) {
			return false;
		}
		final ConfigIdComplexTypeTestConfiguration4 other = (ConfigIdComplexTypeTestConfiguration4) obj;
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
