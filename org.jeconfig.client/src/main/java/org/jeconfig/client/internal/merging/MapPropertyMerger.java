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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.merging.SimpleValueMergingStrategy;
import org.jeconfig.api.annotation.merging.ItemExistenceStrategy;
import org.jeconfig.api.annotation.merging.ItemMergingStrategy;
import org.jeconfig.api.annotation.merging.PropertyMergingParameter;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.common.reflection.ClassInstantiation;

public final class MapPropertyMerger extends AbstractPropertyMerger {
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final SimpleTypeConverterRegistry converterRegistry;

	public MapPropertyMerger(final SimpleTypeConverterRegistry converterRegistry) {
		this.converterRegistry = converterRegistry;
	}

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return ConfigMapProperty.class;
	}

	@Override
	public void merge(
		final ComplexConfigDTO resultConfigDTO,
		final ComplexConfigDTO parentConfigDTO,
		final ComplexConfigDTO childConfigDTO,
		final PropertyDescriptor propertyDescriptor,
		final Map<Class<? extends Annotation>, PropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigMapDTO parentMapDTO = parentConfigDTO.getMapProperty(propertyDescriptor.getName());
		final ConfigMapDTO childMapDTO = childConfigDTO.getMapProperty(propertyDescriptor.getName());

		ConfigMapDTO resultMapDTO = null;
		if (parentMapDTO == null) {
			resultMapDTO = childMapDTO;
		} else if (childMapDTO == null) {
			resultMapDTO = parentMapDTO;
		} else {
			final ConfigMapProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigMapProperty.class);

			if (isChildStale(parentMapDTO, childMapDTO)) {
				switch (annotation.stalenessSolutionStrategy()) {
					case USE_PARENT:
						resultMapDTO = parentMapDTO;
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

			if (resultMapDTO == null) {
				final Map<String, ConfigDTO> parentMap = parentMapDTO.getMap();
				final Map<String, ConfigDTO> childMap = childMapDTO.getMap();
				Map<String, ConfigDTO> resultMap = null;
				if (parentMap != null || childMap != null) {
					resultMap = new HashMap<String, ConfigDTO>();
					mergeAddedEntries(resultMap, parentMap, childMap, annotation.entryAddedStrategy());
					mergeRemovedEntries(resultMap, parentMap, childMap, annotation.entryRemovedStrategy());
					mergeExistingEntries(
							resultMap,
							parentMap,
							childMap,
							mergers,
							complexTypeMerger,
							propertyDescriptor,
							parentConfigDTO,
							childConfigDTO,
							mergingResult);
				}

				resultMapDTO = childMapDTO.flatCopy();
				resultMapDTO.setMap(resultMap);
			}
		}

		if (resultMapDTO != null) {
			resultConfigDTO.addMapProperty(resultMapDTO);
		}
	}

	private void mergeAddedEntries(
		final Map<String, ConfigDTO> resultMap,
		final Map<String, ConfigDTO> parentMap,
		final Map<String, ConfigDTO> childMap,
		final ItemExistenceStrategy itemExistanceStrategy) {

		if (childMap != null) {
			for (final Entry<String, ConfigDTO> entry : childMap.entrySet()) {
				if (parentMap == null || !parentMap.containsKey(entry.getKey())) {
					switch (itemExistanceStrategy) {
						case ADD:
							resultMap.put(entry.getKey(), entry.getValue());
							break;
						case REMOVE:
							// nothing to do
							break;
						default:
							throw new IllegalArgumentException("Got unknown ItemExistenceStrategy: " + itemExistanceStrategy); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private void mergeRemovedEntries(
		final Map<String, ConfigDTO> resultMap,
		final Map<String, ConfigDTO> parentMap,
		final Map<String, ConfigDTO> childMap,
		final ItemExistenceStrategy itemExistanceStrategy) {

		if (parentMap != null) {
			for (final Entry<String, ConfigDTO> entry : parentMap.entrySet()) {
				if (childMap == null || !childMap.containsKey(entry.getKey())) {
					switch (itemExistanceStrategy) {
						case ADD:
							resultMap.put(entry.getKey(), entry.getValue());
							break;
						case REMOVE:
							// nothing to do
							break;
						default:
							throw new IllegalArgumentException("Got unknown ItemExistenceStrategy: " + itemExistanceStrategy); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private void mergeExistingEntries(
		final Map<String, ConfigDTO> resultMap,
		final Map<String, ConfigDTO> parentMap,
		final Map<String, ConfigDTO> childMap,
		final Map<Class<? extends Annotation>, PropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final PropertyDescriptor propertyDescriptor,
		final ComplexConfigDTO parentConfigDTO,
		final ComplexConfigDTO childConfigDTO,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigMapProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigMapProperty.class);
		ItemMergingStrategy mergingStrategy = annotation.mergingStrategy();
		if (isClassOrCodeDefaultDTO(parentConfigDTO)) {
			if (annotation.polymorph()) {
				mergingStrategy = ItemMergingStrategy.USE_CHILD;
			} else {
				mergingStrategy = ItemMergingStrategy.MERGE;
			}
		}

		if (parentMap != null && childMap != null) {
			for (final Entry<String, ConfigDTO> entry : parentMap.entrySet()) {
				if (childMap.containsKey(entry.getKey())) {
					final ConfigDTO parentValueDTO = entry.getValue();
					final ConfigDTO childValueDTO = childMap.get(entry.getKey());
					switch (mergingStrategy) {
						case USE_CHILD:
							resultMap.put(entry.getKey(), createChildWithMissingProperties(parentValueDTO, childValueDTO));
							break;
						case USE_PARENT:
							resultMap.put(entry.getKey(), parentValueDTO);
							break;
						case MERGE:
							if (annotation.polymorph()) {
								throw new IllegalArgumentException(
									"Merging strategy 'MERGE' is not supported for polymorph fields! " //$NON-NLS-1$
										+ "Use 'USE_CHILD' or 'USE_PARENT' instead"); //$NON-NLS-1$
							}
							resultMap.put(
									entry.getKey(),
									mergeValue(
											parentValueDTO,
											childValueDTO,
											mergers,
											complexTypeMerger,
											propertyDescriptor,
											parentConfigDTO,
											childConfigDTO,
											mergingResult));
							break;
						default:
							throw new IllegalArgumentException("Got unknown ItemMergingStrategy: " + annotation.mergingStrategy()); //$NON-NLS-1$
					}
				}
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private ConfigDTO mergeValue(
		final ConfigDTO parentValueDTO,
		final ConfigDTO childValueDTO,
		final Map<Class<? extends Annotation>, PropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final PropertyDescriptor propertyDescriptor,
		final ComplexConfigDTO parentConfigDTO,
		final ComplexConfigDTO childConfigDTO,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigMapProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigMapProperty.class);
		final Class<?> valueType = annotation.valueType();
		final ConfigComplexType complexTypeAnnotation = AnnotationUtil.getAnnotation(valueType, ConfigComplexType.class);

		if (complexTypeAnnotation != null) {
			if (childValueDTO != null && !(childValueDTO instanceof ComplexConfigDTO)) {
				throw new IllegalArgumentException("Got non-complex value for complex map"); //$NON-NLS-1$
			}
			if (parentValueDTO != null && !(parentValueDTO instanceof ComplexConfigDTO)) {
				throw new IllegalArgumentException("Got non-complex value for complex map"); //$NON-NLS-1$
			}

			return complexTypeMerger.merge(
					(ComplexConfigDTO) parentValueDTO,
					(ComplexConfigDTO) childValueDTO,
					(Class<Object>) valueType,
					mergers,
					mergingResult);
		} else {
			final SimpleValueMergingStrategy strategy = classInstantiation.newInstance(annotation.simpleValueMergingStrategy());
			if (childValueDTO != null && !(childValueDTO instanceof ConfigSimpleValueDTO)) {
				throw new IllegalArgumentException("Got non-simple value for simple map"); //$NON-NLS-1$
			}
			if (parentValueDTO != null && !(parentValueDTO instanceof ConfigSimpleValueDTO)) {
				throw new IllegalArgumentException("Got non-simple value for simple map"); //$NON-NLS-1$
			}

			final PropertyMergingParameter mergingParameter = new PropertyMergingParameter(
				(ConfigSimpleValueDTO) parentValueDTO,
				(ConfigSimpleValueDTO) childValueDTO,
				getConverter(annotation.valueType(), annotation.customValueConverter()),
				parentConfigDTO,
				childConfigDTO,
				propertyDescriptor.getPropertyType(),
				propertyDescriptor.getName());

			return strategy.merge(mergingParameter);
		}
	}

	private SimpleTypeConverter<?> getConverter(
		final Class<?> propertyType,
		final Class<? extends SimpleTypeConverter<?>> customConverterClass) {
		final boolean customConverter = !NoCustomSimpleTypeConverter.class.equals(customConverterClass);
		if (customConverter) {
			return classInstantiation.newInstance(customConverterClass);
		}
		return converterRegistry.getConverter(propertyType);
	}
}
