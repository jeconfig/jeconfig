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

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.merging.ListItemMergingStrategy;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;

public final class ArrayPropertyMerger extends AbstractPropertyMerger {

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return ConfigArrayProperty.class;
	}

	@Override
	public void merge(
		final ComplexConfigDTO resultDTO,
		final ComplexConfigDTO parentDTO,
		final ComplexConfigDTO childDTO,
		final PropertyDescriptor propertyDescriptor,
		final Map<Class<? extends Annotation>, IPropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigListDTO parentListDTO = parentDTO.getListProperty(propertyDescriptor.getName());
		final ConfigListDTO childListDTO = childDTO.getListProperty(propertyDescriptor.getName());

		ConfigListDTO resultListDTO = null;
		if (parentListDTO == null) {
			resultListDTO = childListDTO;
		} else if (childListDTO == null) {
			resultListDTO = parentListDTO;
		} else {
			final ConfigArrayProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigArrayProperty.class);

			if (isChildStale(parentListDTO, childListDTO)) {
				switch (annotation.stalenessSolutionStrategy()) {
					case USE_PARENT:
						resultListDTO = parentListDTO;
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

			if (resultListDTO == null) {
				ListItemMergingStrategy mergingStrategy = annotation.mergingStrategy();
				if (isClassOrCodeDefaultDTO(parentListDTO)) {
					mergingStrategy = ListItemMergingStrategy.USE_CHILD;
				}
				switch (mergingStrategy) {
					case USE_CHILD:
						resultListDTO = childListDTO;
						break;
					case USE_PARENT:
						resultListDTO = parentListDTO;
						break;
					default:
						throw new IllegalArgumentException("Got unknown merging strategy: " + annotation.mergingStrategy()); //$NON-NLS-1$
				}
			}
		}

		if (resultListDTO != null) {
			resultDTO.addListProperty(resultListDTO);
		}
	}

}
