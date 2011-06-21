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
import org.jeconfig.client.annotation.simple.TestConverter2;

@ConfigComplexType
public class CustomIdConverterSubConfig {
	private String id;
	private String value;

	public CustomIdConverterSubConfig() {}

	public CustomIdConverterSubConfig(final String id, final String value) {
		this.id = id;
		this.value = value;
	}

	@ConfigIdProperty()
	@ConfigSimpleProperty(customConverter = TestConverter2.class)
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@ConfigSimpleProperty(customConverter = TestConverter2.class)
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public static CustomIdConverterSubConfig create(final ConfigService cs, final String id, final String value) {
		final CustomIdConverterSubConfig cfg = cs.createComplexObject(CustomIdConverterSubConfig.class);
		cfg.setId(id);
		cfg.setValue(value);
		return cfg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (!(obj instanceof CustomIdConverterSubConfig)) {
			return false;
		}
		final CustomIdConverterSubConfig other = (CustomIdConverterSubConfig) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
}
