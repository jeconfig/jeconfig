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
import java.util.List;

import org.jeconfig.api.conversion.ISimpleTypeConverter;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.scope.IScopePath;

public class ListDTOSerializer extends AbstractDTOSerializer {

	public ConfigListDTO createListDTO(
		final List<?> list,
		final List<ConfigListDTO> originalDTOs,
		final Class<?> propertyType,
		final String propertyName,
		final boolean complex,
		final boolean polymorph,
		final IScopePath scopePath,
		final Class<?> itemType,
		final ComplexDTOSerializer complexDTOSerializer,
		final SimpleDTOSerializer simpleDTOSerializer,
		final ISimpleTypeConverter<Object> customConverter) {

		final ConfigListDTO result = new ConfigListDTO();
		result.setPropertyType(propertyType.getName());
		result.setPropertyName(propertyName);
		result.setPolymorph(polymorph);
		result.setDefiningScopePath(scopePath);

		if (list != null) {
			final List<IConfigDTO> items = new ArrayList<IConfigDTO>();
			for (final Object item : list) {
				if (complex || polymorph) {
					items.add(complexDTOSerializer.createConfigDTO(
							item,
							null,
							getValueType(item),
							null,
							polymorph,
							scopePath,
							true));
				} else {
					items.add(simpleDTOSerializer.createSimpleValueDTO(item, null, itemType, null, scopePath, customConverter));
				}
			}

			result.setItems(items);
		}

		updateVersionsAndParent(result, originalDTOs, scopePath);

		return result;
	}

}
