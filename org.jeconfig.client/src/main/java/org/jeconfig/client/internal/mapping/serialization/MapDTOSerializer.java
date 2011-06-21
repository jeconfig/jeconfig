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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigDTO;
import org.jeconfig.api.scope.ScopePath;

public class MapDTOSerializer extends AbstractDTOSerializer {
	private final SimpleTypeConverterRegistry simpleTypeConverterRegistry;

	public MapDTOSerializer(final SimpleTypeConverterRegistry simpleTypeConverterRegistry) {
		this.simpleTypeConverterRegistry = simpleTypeConverterRegistry;
	}

	public ConfigMapDTO createMapDTO(
		final Map<?, ?> map,
		final List<ConfigMapDTO> originalDTOs,
		final Class<?> propertyType,
		final String propertyName,
		final boolean complex,
		final boolean polymorph,
		final ScopePath scopePath,
		final Class<?> keyType,
		final Class<?> valueType,
		final boolean shouldCreateWholeSubtree,
		final ComplexDTOSerializer complexDTOSerializer,
		final SimpleDTOSerializer simpleDTOSerializer,
		final SimpleTypeConverter<Object> customValueConverter,
		final SimpleTypeConverter<Object> customKeyConverter) {

		final ConfigMapDTO result = new ConfigMapDTO();
		result.setPropertyType(propertyType.getName());
		result.setPropertyName(propertyName);
		result.setPolymorph(polymorph);
		result.setDefiningScopePath(scopePath);

		if (!polymorph && !complex && customValueConverter == null && !simpleTypeConverterRegistry.isTypeSupported(valueType)) {
			throw new RuntimeException("Didn't find converter for simple value type: " + valueType); //$NON-NLS-1$
		}
		if (customKeyConverter == null && !simpleTypeConverterRegistry.isTypeSupported(keyType)) {
			throw new RuntimeException("Didn't find converter for simple key type: " + keyType); //$NON-NLS-1$
		}

		if (map != null) {
			final Map<String, ConfigDTO> items = new HashMap<String, ConfigDTO>();

			for (final Entry<?, ?> entry : map.entrySet()) {
				String keyString = null;
				if (customKeyConverter != null) {
					keyString = customKeyConverter.convertToSerializedForm(entry.getKey());
				} else {
					keyString = simpleTypeConverterRegistry.convertToSerializedForm(entry.getKey());
				}
				final Object value = entry.getValue();
				ConfigDTO valueDTO;
				if (complex && !polymorph) {
					// merging is supported
					valueDTO = complexDTOSerializer.createConfigDTO(
							value,
							getValueOriginalDTOs(keyString, originalDTOs),
							valueType,
							null,
							polymorph,
							scopePath,
							shouldCreateWholeSubtree);

				} else if (polymorph) {
					// no merging supported (whole subtree must be saved)
					valueDTO = complexDTOSerializer.createConfigDTO(
							value,
							null,
							getValueType(value),
							null,
							polymorph,
							scopePath,
							true);

				} else {
					valueDTO = simpleDTOSerializer.createSimpleValueDTO(
							value,
							null,
							valueType,
							null,
							scopePath,
							customValueConverter);
				}
				items.put(keyString, valueDTO);
			}

			result.setMap(items);
		}

		updateVersionsAndParent(result, originalDTOs, scopePath);

		return result;
	}

	private List<ComplexConfigDTO> getValueOriginalDTOs(final String key, final List<ConfigMapDTO> originalDTOs) {
		if (originalDTOs == null) {
			return null;
		}

		final List<ComplexConfigDTO> result = new ArrayList<ComplexConfigDTO>();
		for (final ConfigMapDTO mapDTO : originalDTOs) {
			if (mapDTO.getMap() != null) {
				final ComplexConfigDTO valueDTO = (ComplexConfigDTO) mapDTO.getMap().get(key);
				if (valueDTO != null) {
					result.add(valueDTO);
				}
			}
		}

		return result;
	}

}
