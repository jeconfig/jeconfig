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

import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.annotation.merging.SimpleValueMergingStrategy;
import org.jeconfig.api.annotation.merging.MergingStrategies;
import org.jeconfig.api.annotation.merging.PropertyMergingParameter;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.common.reflection.ClassInstantiation;

public final class SimplePropertyMerger extends AbstractPropertyMerger {
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final SimpleTypeConverterRegistry converterRegistry;

	public SimplePropertyMerger(final SimpleTypeConverterRegistry converterRegistry) {
		this.converterRegistry = converterRegistry;
	}

	@Override
	public Class<ConfigSimpleProperty> getAnnotationClass() {
		return ConfigSimpleProperty.class;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void merge(
		final ComplexConfigDTO resultDTO,
		final ComplexConfigDTO parentDTO,
		final ComplexConfigDTO childDTO,
		final PropertyDescriptor propertyDescriptor,
		final Map<Class<? extends Annotation>, PropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigSimpleValueDTO parentValueDTO = parentDTO.getSimpleValueProperty(propertyDescriptor.getName());
		final ConfigSimpleValueDTO childValueDTO = childDTO.getSimpleValueProperty(propertyDescriptor.getName());

		ConfigSimpleValueDTO mergeResultDTO = null;
		if (parentValueDTO == null) {
			mergeResultDTO = childValueDTO;
		} else if (childValueDTO == null) {
			mergeResultDTO = parentValueDTO;
		} else {
			final ConfigSimpleProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigSimpleProperty.class);

			if (isChildStale(parentValueDTO, childValueDTO)) {
				switch (annotation.stalenessSolutionStrategy()) {
					case USE_PARENT:
						mergeResultDTO = parentValueDTO;
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

			if (mergeResultDTO == null) {
				final PropertyMergingParameter<?> mergingParam = new PropertyMergingParameter(
					parentValueDTO,
					childValueDTO,
					getConverter(propertyDescriptor),
					parentDTO,
					childDTO,
					propertyDescriptor.getPropertyType(),
					propertyDescriptor.getName());

				SimpleValueMergingStrategy mergingStrategy = classInstantiation.newInstance(annotation.mergingStrategy());
				if (isClassOrCodeDefaultDTO(parentValueDTO)) {
					mergingStrategy = classInstantiation.newInstance(MergingStrategies.ChildOverwrites.class);
				}
				mergeResultDTO = mergingStrategy.merge(mergingParam);
			}
		}

		if (mergeResultDTO != null) {
			resultDTO.addSimpleValueProperty(mergeResultDTO);
		}
	}

	private SimpleTypeConverter<?> getConverter(final PropertyDescriptor propertyDescriptor) {
		final ConfigSimpleProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigSimpleProperty.class);
		final boolean customConverter = !NoCustomSimpleTypeConverter.class.equals(annotation.customConverter());
		if (customConverter) {
			return classInstantiation.newInstance(annotation.customConverter());
		}
		return converterRegistry.getConverter(propertyDescriptor.getPropertyType());
	}
}
