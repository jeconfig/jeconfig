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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigComplexType;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.merging.ItemExistenceStrategy;
import org.jeconfig.api.annotation.merging.ItemMergingStrategy;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.client.internal.AnnotationUtil;

public final class SetPropertyMerger extends AbstractPropertyMerger {

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return ConfigSetProperty.class;
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

		final ConfigSetDTO parentSetDTO = parentDTO.getSetProperty(propertyDescriptor.getName());
		final ConfigSetDTO childSetDTO = childDTO.getSetProperty(propertyDescriptor.getName());

		ConfigSetDTO resultSetDTO = null;
		if (parentSetDTO == null) {
			resultSetDTO = childSetDTO;
		} else if (childSetDTO == null) {
			resultSetDTO = parentSetDTO;
		} else {
			final ConfigSetProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigSetProperty.class);

			if (isChildStale(parentSetDTO, childSetDTO)) {
				switch (annotation.stalenessSolutionStrategy()) {
					case USE_PARENT:
						resultSetDTO = parentSetDTO;
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

			if (resultSetDTO == null) {
				Set<IConfigDTO> resultSet = null;
				if (parentSetDTO.getItems() != null || childSetDTO.getItems() != null) {
					resultSet = new HashSet<IConfigDTO>();
					final Map<ElementContainer, IConfigDTO> parentContainerMap = createContainerMap(parentSetDTO.getItems());
					final Map<ElementContainer, IConfigDTO> childContainerMap = createContainerMap(childSetDTO.getItems());

					mergeAddedSimpleElements(resultSet, parentContainerMap, childContainerMap, annotation.itemAddedStrategy());
					mergeRemovedSimpleElements(resultSet, parentContainerMap, childContainerMap, annotation.itemRemovedStrategy());
					mergeExistingEntries(
							resultSet,
							parentContainerMap,
							childContainerMap,
							mergers,
							complexTypeMerger,
							propertyDescriptor,
							parentDTO,
							childDTO,
							mergingResult);
				}

				resultSetDTO = childSetDTO.flatCopy();
				resultSetDTO.setItems(resultSet);
			}
		}

		if (resultSetDTO != null) {
			resultDTO.addSetProperty(resultSetDTO);
		}
	}

	private Map<ElementContainer, IConfigDTO> createContainerMap(final Set<IConfigDTO> set) {
		if (set != null) {
			final Map<ElementContainer, IConfigDTO> result = new HashMap<ElementContainer, IConfigDTO>();

			for (final IConfigDTO element : set) {
				result.put(new ElementContainer(element), element);
			}

			return result;
		}
		return null;
	}

	private void mergeExistingEntries(
		final Set<IConfigDTO> resultSet,
		final Map<ElementContainer, IConfigDTO> parentMap,
		final Map<ElementContainer, IConfigDTO> childMap,
		final Map<Class<? extends Annotation>, IPropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final PropertyDescriptor propertyDescriptor,
		final ComplexConfigDTO parentConfigDTO,
		final ComplexConfigDTO childConfigDTO,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigSetProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigSetProperty.class);
		final Class<?> valueType = annotation.itemType();
		final ConfigComplexType complexTypeAnnotation = AnnotationUtil.getAnnotation(valueType, ConfigComplexType.class);
		ItemMergingStrategy mergingStrategy = annotation.mergingStrategy();
		if (isClassOrCodeDefaultDTO(parentConfigDTO)) {
			if (annotation.polymorph() || complexTypeAnnotation == null) {
				mergingStrategy = ItemMergingStrategy.USE_CHILD;
			} else {
				mergingStrategy = ItemMergingStrategy.MERGE;
			}
		}

		if (parentMap != null && childMap != null) {
			for (final ElementContainer elementContainer : parentMap.keySet()) {
				if (childMap.containsKey(elementContainer)) {
					final IConfigDTO parentItemDTO = elementContainer.getElement();
					final IConfigDTO childItemDTO = childMap.get(elementContainer);

					switch (mergingStrategy) {
						case USE_CHILD:
							resultSet.add(createChildWithMissingProperties(parentItemDTO, childItemDTO));
							break;
						case USE_PARENT:
							resultSet.add(parentItemDTO);
							break;
						case MERGE:
							if (annotation.polymorph()) {
								throw new IllegalArgumentException(
									"Merging strategy 'MERGE' is not supported for polymorph fields! " //$NON-NLS-1$
										+ "Use 'USE_CHILD' or 'USE_PARENT' instead"); //$NON-NLS-1$
							}
							resultSet.add(mergeItem(
									parentItemDTO,
									childItemDTO,
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

	@SuppressWarnings("unchecked")
	private IConfigDTO mergeItem(
		final IConfigDTO parentItemDTO,
		final IConfigDTO childItemDTO,
		final Map<Class<? extends Annotation>, IPropertyMerger> mergers,
		final ComplexTypeMerger complexTypeMerger,
		final PropertyDescriptor propertyDescriptor,
		final ComplexConfigDTO parentConfigDTO,
		final ComplexConfigDTO childConfigDTO,
		final StalePropertiesMergingResultImpl mergingResult) {

		final ConfigSetProperty annotation = propertyDescriptor.getReadMethod().getAnnotation(ConfigSetProperty.class);
		final Class<?> valueType = annotation.itemType();
		final ConfigComplexType complexTypeAnnotation = AnnotationUtil.getAnnotation(valueType, ConfigComplexType.class);

		if (complexTypeAnnotation != null) {
			if (childItemDTO != null && !(childItemDTO instanceof ComplexConfigDTO)) {
				throw new IllegalArgumentException("Got non-complex item for complex set"); //$NON-NLS-1$
			}
			if (parentItemDTO != null && !(parentItemDTO instanceof ComplexConfigDTO)) {
				throw new IllegalArgumentException("Got non-complex item for complex set"); //$NON-NLS-1$
			}
			return complexTypeMerger.merge(
					(ComplexConfigDTO) parentItemDTO,
					(ComplexConfigDTO) childItemDTO,
					(Class<Object>) valueType,
					mergers,
					mergingResult);
		} else {
			throw new IllegalArgumentException("Merge is not supported for sets with non-complex items!"); //$NON-NLS-1$
		}
	}

	private void mergeAddedSimpleElements(
		final Set<IConfigDTO> resultSet,
		final Map<ElementContainer, IConfigDTO> parentMap,
		final Map<ElementContainer, IConfigDTO> childMap,
		final ItemExistenceStrategy itemExistanceStrategy) {

		if (childMap != null) {
			for (final ElementContainer elementContainer : childMap.keySet()) {
				if (parentMap == null || !parentMap.containsKey(elementContainer)) {
					switch (itemExistanceStrategy) {
						case ADD:
							resultSet.add(elementContainer.getElement());
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

	private void mergeRemovedSimpleElements(
		final Set<IConfigDTO> resultSet,
		final Map<ElementContainer, IConfigDTO> parentMap,
		final Map<ElementContainer, IConfigDTO> childMap,
		final ItemExistenceStrategy itemExistanceStrategy) {

		if (parentMap != null) {
			for (final ElementContainer elementContainer : parentMap.keySet()) {
				if (childMap == null || !childMap.containsKey(elementContainer)) {
					switch (itemExistanceStrategy) {
						case ADD:
							resultSet.add(elementContainer.getElement());
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

	private static class ElementContainer {
		private final IConfigDTO element;
		private Object key;

		public ElementContainer(final IConfigDTO element) {
			this.element = element;

			if (element instanceof ConfigSimpleValueDTO) {
				key = element;
			} else if (element instanceof ComplexConfigDTO) {
				final ComplexConfigDTO complexElement = (ComplexConfigDTO) element;
				if (complexElement.getIdPropertyName() != null) {
					final ConfigSimpleValueDTO simpleValueProperty = complexElement.getSimpleValueProperty(complexElement.getIdPropertyName());
					if (simpleValueProperty != null) {
						key = simpleValueProperty.getValue();
					} else {
						throw new IllegalArgumentException(
							"ID property '" + complexElement.getIdPropertyName() + " is not set at element '" + element.getPropertyType()); //$NON-NLS-1$//$NON-NLS-2$
					}
				} else {
					// the complex properties of polymorph sets may not have ID properties
					key = element;
				}
			}
		}

		public IConfigDTO getElement() {
			return element;
		}

		@SuppressWarnings("nls")
		@Override
		public String toString() {
			return "Element[key=" + key + ", element=" + element + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
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
			final ElementContainer other = (ElementContainer) obj;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			return true;
		}
	}

}
