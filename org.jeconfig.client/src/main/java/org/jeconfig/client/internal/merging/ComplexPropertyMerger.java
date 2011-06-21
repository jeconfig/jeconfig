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

package org.jeconfig.client.internal.merging;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.merging.ItemMergingStrategy;
import org.jeconfig.api.dto.ComplexConfigDTO;

public final class ComplexPropertyMerger extends AbstractPropertyMerger {

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return ConfigComplexProperty.class;
	}

	@Override
	public void merge(
		final ComplexConfigDTO resultDTO,
		final ComplexConfigDTO parentDTO,
		final ComplexConfigDTO childDTO,
		final PropertyDescriptor propertyDescriptor,
		final Map<Class<? extends Annotation>, PropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ComplexConfigDTO parentPropertyDTO = parentDTO.getComplexProperty(propertyDescriptor.getName());
		final ComplexConfigDTO childPropertyDTO = childDTO.getComplexProperty(propertyDescriptor.getName());

		ComplexConfigDTO mergedPropertyDTO = null;
		if (parentPropertyDTO == null) {
			mergedPropertyDTO = childPropertyDTO;
		} else if (childPropertyDTO == null) {
			mergedPropertyDTO = parentPropertyDTO;
		} else {
			final ConfigComplexProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigComplexProperty.class);

			if (isChildStale(parentPropertyDTO, childPropertyDTO)) {
				switch (annotation.stalenessSolutionStrategy()) {
					case USE_PARENT:
						mergedPropertyDTO = parentPropertyDTO;
						mergingResult.discardedStaleProperty();
						break;
					case MERGE:
						mergingResult.mergedStaleProperty();
						// continue with merge
						break;
					default:
						throw new IllegalArgumentException(
							"Got unknown staleness solution strategy: " + annotation.stalenessSolutionStrategy()); //$NON-NLS-1$
				}
			}

			if (mergedPropertyDTO == null) {
				ItemMergingStrategy mergingStrategy = annotation.mergingStrategy();
				if (isClassOrCodeDefaultDTO(parentPropertyDTO)) {
					if (annotation.polymorph()) {
						mergingStrategy = ItemMergingStrategy.USE_CHILD;
					} else {
						mergingStrategy = ItemMergingStrategy.MERGE;
					}
				}
				switch (mergingStrategy) {
					case USE_CHILD:
						mergedPropertyDTO = childPropertyDTO;
						break;
					case USE_PARENT:
						mergedPropertyDTO = parentPropertyDTO;
						break;
					case MERGE:
						if (annotation.polymorph()) {
							throw new IllegalArgumentException("Merging strategy 'MERGE' is not supported for polymorph fields! " //$NON-NLS-1$
								+ "Use 'USE_CHILD' or 'USE_PARENT' instead"); //$NON-NLS-1$
						}
						mergedPropertyDTO = complexTypeMerger.merge(
								parentPropertyDTO,
								childPropertyDTO,
								propertyDescriptor.getPropertyType(),
								mergers,
								mergingResult);
						break;
					default:
						throw new IllegalArgumentException("Got unknown merging strategy: " + mergingStrategy); //$NON-NLS-1$
				}
			}
		}

		if (mergedPropertyDTO != null) {
			resultDTO.addComplexProperty(mergedPropertyDTO);
		}
	}

}
