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

package org.jeconfig.api.annotation.merging;

import org.jeconfig.api.conversion.ISimpleTypeConverter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.util.Assert;

/**
 * Parameter for {@link ISimpleValueMergingStrategy}. Holds all information which may
 * be needed to decide which value should be the result of the merge step.<br>
 * 
 * The merging strategy may return the parent value DTO or the child value DTO as the merge result.
 * It may even decide to create a new result DTO using the {@link #createResultDTO(Object)}-method
 * with an arbitrary value of type T.
 * 
 * @param <T> the type of the simple type to merge
 */
public final class PropertyMergingParameter<T> {
	private final ConfigSimpleValueDTO parentValueDTO;
	private final ConfigSimpleValueDTO childValueDTO;
	private final ISimpleTypeConverter<T> converter;
	private final ComplexConfigDTO parentConfigDTO;
	private final Class<T> propertyType;
	private final ComplexConfigDTO childConfigDTO;

	public PropertyMergingParameter(
		final ConfigSimpleValueDTO parentValueDTO,
		final ConfigSimpleValueDTO childValueDTO,
		final ISimpleTypeConverter<T> converter,
		final ComplexConfigDTO parentConfigDTO,
		final ComplexConfigDTO childConfigDTO,
		final Class<T> propertyType,
		final String propertyName) {

		Assert.paramNotNull(parentValueDTO, "parentValueDTO"); //$NON-NLS-1$
		Assert.paramNotNull(childValueDTO, "childValueDTO"); //$NON-NLS-1$
		Assert.paramNotNull(converter, "converter"); //$NON-NLS-1$
		Assert.paramNotNull(parentConfigDTO, "parentConfigDTO"); //$NON-NLS-1$
		Assert.paramNotNull(childConfigDTO, "childConfigDTO"); //$NON-NLS-1$
		Assert.paramNotNull(propertyType, "propertyType"); //$NON-NLS-1$
		Assert.paramNotNull(propertyName, "propertyName"); //$NON-NLS-1$

		this.parentValueDTO = parentValueDTO;
		this.childValueDTO = childValueDTO;
		this.converter = converter;
		this.parentConfigDTO = parentConfigDTO;
		this.childConfigDTO = childConfigDTO;
		this.propertyType = propertyType;
	}

	/**
	 * The parent configuration DTO which is the source of the parent value DTO.
	 * 
	 * @return the parentConfigDTO; never <code>null</code>
	 */
	public ComplexConfigDTO getParentConfigDTO() {
		return parentConfigDTO;
	}

	/**
	 * The child configuration DTO which is the source of the child value DTO.
	 * 
	 * @return the childConfigDTO; never <code>null</code>
	 */
	public ComplexConfigDTO getChildConfigDTO() {
		return childConfigDTO;
	}

	/**
	 * The parent value DTO to merge. May be returned as the merge result.
	 * 
	 * @return the parentValueDTO; never <code>null</code>
	 */
	public ConfigSimpleValueDTO getParentValueDTO() {
		return parentValueDTO;
	}

	/**
	 * The child value DTO to merge. May be returned as the merge result.
	 * 
	 * @return the childValueDTO; never <code>null</code>
	 */
	public ConfigSimpleValueDTO getChildValueDTO() {
		return childValueDTO;
	}

	/**
	 * @return the parent value converted to T
	 */
	public T getParentValue() {
		if (parentValueDTO.getValue() != null) {
			return converter.convertToObject(propertyType, parentValueDTO.getValue());
		}
		return null;
	}

	/**
	 * @return the child value converted to T
	 */
	public T getChildValue() {
		if (childValueDTO.getValue() != null) {
			return converter.convertToObject(propertyType, childValueDTO.getValue());
		}
		return null;
	}

	/**
	 * Creates a new value DTO which may be used as the result of the merging strategie's merge step.
	 * 
	 * @param resultValue
	 * @return a new value DTO which holds the result value
	 */
	public ConfigSimpleValueDTO createResultDTO(final T resultValue) {
		return childValueDTO.flatCopy(resultValue != null ? converter.convertToSerializedForm(resultValue) : null);
	}
}
