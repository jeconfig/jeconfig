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

package org.jeconfig.client.internal.mapping.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.client.internal.ConfigIdPropertyUtil;

public class SetDTOSerializer extends AbstractDTOSerializer {
	private final SimpleTypeConverterRegistry simpleTypeConverterRegistry;
	private final ConfigIdPropertyUtil configIdPropertyUtil = new ConfigIdPropertyUtil();

	public SetDTOSerializer(final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		this.simpleTypeConverterRegistry = simpleTypeConverterRegistry;
	}

	public ConfigSetDTO createSetDTO(
		final Set<?> set,
		final List<ConfigSetDTO> originalDTOs,
		final Class<?> propertyType,
		final String propertyName,
		final boolean complex,
		final boolean polymorph,
		final ScopePath scopePath,
		final Class<?> itemType,
		final boolean shouldCreateWholeSubtree,
		final ComplexDTOSerializer complexDTOSerializer,
		final SimpleDTOSerializer simpleDTOSerializer,
		final SimpleTypeConverter<Object> customConverter) {

		final ConfigSetDTO result = new ConfigSetDTO(propertyType.getName(), propertyName, polymorph, scopePath);

		if (set != null) {
			final Set<ConfigDTO> items = new HashSet<ConfigDTO>();

			if (complex && !polymorph) {
				// merging is supported

				final String idPropertyName = new ConfigIdPropertyUtil().getIdPropertyName(itemType);
				final List<Map<String, ComplexConfigDTO>> dtoMaps = createDTOMaps(originalDTOs, idPropertyName);

				for (final Object item : set) {
					items.add(complexDTOSerializer.createConfigDTO(
							item,
							getItemOriginalDTOs(item, dtoMaps, idPropertyName),
							itemType,
							null,
							polymorph,
							scopePath,
							shouldCreateWholeSubtree));
				}
			} else {
				for (final Object item : set) {
					if (polymorph) {
						// no merging supported (whole subtree must be saved)
						items.add(complexDTOSerializer.createConfigDTO(
								item,
								null,
								getValueType(item),
								null,
								polymorph,
								scopePath,
								true));
					} else {
						items.add(simpleDTOSerializer.createSimpleValueDTO(
								item,
								null,
								itemType,
								propertyName,
								scopePath,
								customConverter));
					}
				}
			}
			result.setItems(items);
		}

		updateVersionsAndParent(result, originalDTOs, scopePath);

		return result;
	}

	private List<Map<String, ComplexConfigDTO>> createDTOMaps(final List<ConfigSetDTO> originalDTOs, final String idPropertyName) {
		if (originalDTOs == null) {
			return null;
		}

		final List<Map<String, ComplexConfigDTO>> result = new ArrayList<Map<String, ComplexConfigDTO>>();

		for (final ConfigSetDTO setDTO : originalDTOs) {
			if (setDTO.getItems() != null && !setDTO.getItems().isEmpty()) {
				final Map<String, ComplexConfigDTO> dtoMap = new HashMap<String, ComplexConfigDTO>();
				for (final ConfigDTO item : setDTO.getItems()) {
					final ComplexConfigDTO complexItem = (ComplexConfigDTO) item;
					final ConfigSimpleValueDTO idProperty = complexItem.getSimpleValueProperty(idPropertyName);
					if (idProperty == null) {
						throw new IllegalArgumentException("Found item which has no ID set: " + item); //$NON-NLS-1$
					}
					dtoMap.put(idProperty.getValue(), complexItem);
				}
				result.add(dtoMap);
			}
		}

		return result;
	}

	private List<ComplexConfigDTO> getItemOriginalDTOs(
		final Object item,
		final List<Map<String, ComplexConfigDTO>> dtoMaps,
		final String idPropertyName) {
		if (dtoMaps == null) {
			return null;
		}

		final String id = configIdPropertyUtil.getIdPropertyValueAsString(item, idPropertyName, simpleTypeConverterRegistry);
		final List<ComplexConfigDTO> result = new ArrayList<ComplexConfigDTO>();
		for (final Map<String, ComplexConfigDTO> dtoMap : dtoMaps) {
			final ComplexConfigDTO itemDTO = dtoMap.get(id);
			if (itemDTO != null) {
				result.add(itemDTO);
			}
		}

		return result;
	}
}
