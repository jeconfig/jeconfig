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

package org.jeconfig.client.internal.validation;

import java.util.HashSet;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigMigration;
import org.jeconfig.api.annotation.ConfigTransformer;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;

/**
 * Validates @ConfigMigration Annotations.
 */
public final class MigrationValidator {

	public void validate(final Class<?> configClass) {

		final ConfigMigration annotation = configClass.getAnnotation(ConfigMigration.class);
		if (annotation != null) {
			checkContainsConfigTransformers(configClass);
			checkSameSourceAndDestinationVersion(configClass);
		}
	}

	public void checkSameSourceAndDestinationVersion(final Class<?> configClass) {
		final ConfigMigration annotation = AnnotationUtil.getAnnotation(configClass, ConfigMigration.class);
		final Set<LongTuple> set = new HashSet<LongTuple>();
		if (annotation != null) {
			final ConfigTransformer[] transformers = annotation.value();
			for (final ConfigTransformer cT : transformers) {
				final LongTuple tuple = new LongTuple(Long.valueOf(cT.sourceVersion()), Long.valueOf(cT.destinationVersion()));
				if (set.contains(tuple)) {
					throw new IllegalArgumentException(ProxyUtil.getConfigClass(configClass).getName()
						+ ": Different transformers for the same sourceversion and destinationversion are not allowed."); //$NON-NLS-1$
				}
				set.add(tuple);

			}
		}
	}

	public void checkContainsConfigTransformers(final Class<?> configClass) {
		final ConfigMigration annotation = AnnotationUtil.getAnnotation(configClass, ConfigMigration.class);
		if (annotation != null) {
			final ConfigTransformer[] transformers = annotation.value();
			if (transformers.length == 0) {
				throw new IllegalArgumentException(ProxyUtil.getConfigClass(configClass).getName() + ": No transformer is set."); //$NON-NLS-1$
			}
		}
	}

	private static class LongTuple {
		private final Long first;
		private final Long second;

		public LongTuple(final Long first, final Long second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((first == null) ? 0 : first.hashCode());
			result = prime * result + ((second == null) ? 0 : second.hashCode());
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			final LongTuple other = (LongTuple) obj;
			if (first == null) {
				if (other.first != null) {
					return false;
				}
			} else if (!first.equals(other.first)) {
				return false;
			}
			if (second == null) {
				if (other.second != null) {
					return false;
				}
			} else if (!second.equals(other.second)) {
				return false;
			}
			return true;
		}
	}
}
