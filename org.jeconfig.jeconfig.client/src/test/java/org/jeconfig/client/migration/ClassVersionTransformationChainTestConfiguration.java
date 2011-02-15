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

package org.jeconfig.client.migration;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigMigration;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.annotation.ConfigTransformer;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;

@ConfigClass(scopePath = {GlobalScopeDescriptor.NAME, UserScopeDescriptor.NAME}, classVersion = 3)
@ConfigMigration({
		@ConfigTransformer(sourceVersion = 1, destinationVersion = 2, transformer = MyConfigTransformerV1V2.class),
		@ConfigTransformer(sourceVersion = 1, destinationVersion = 3, transformer = MyConfigTransformerV1V3.class),
		@ConfigTransformer(sourceVersion = 1, destinationVersion = 5, transformer = MyConfigTransformerV1V5.class),
		@ConfigTransformer(sourceVersion = 2, destinationVersion = 4, transformer = MyConfigTransformerV2V4.class),
		@ConfigTransformer(sourceVersion = 2, destinationVersion = 5, transformer = MyConfigTransformerV2V5.class),
		@ConfigTransformer(sourceVersion = 3, destinationVersion = 4, transformer = MyConfigTransformerV3V4.class),
		@ConfigTransformer(sourceVersion = 4, destinationVersion = 6, transformer = MyConfigTransformerV4V6.class),
		@ConfigTransformer(sourceVersion = 5, destinationVersion = 7, transformer = MyConfigTransformerV5V7.class)})
public class ClassVersionTransformationChainTestConfiguration {

	private String name;

	private int id;

	public ClassVersionTransformationChainTestConfiguration() {
		name = "Lukas"; //$NON-NLS-1$
		id = 1;
	}

	@ConfigSimpleProperty
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@ConfigSimpleProperty
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
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
		if (!(obj instanceof ClassVersionTransformationChainTestConfiguration)) {
			return false;
		}
		final ClassVersionTransformationChainTestConfiguration other = (ClassVersionTransformationChainTestConfiguration) obj;
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
