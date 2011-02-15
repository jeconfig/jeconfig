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

package org.jeconfig.client.proxy;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.scope.IScopePath;

public interface IConfigProxy<DTO_TYPE extends IConfigDTO> {

	IScopePath getScopePath();

	IConfigProxy<?> getParentProxy();

	void setParentProxy(IConfigProxy<?> parent);

	void setDirty();

	void setReadOnly(boolean readOnly);

	boolean isReadOnly();

	void setReadOnlyCrossReferences(boolean readOnly);

	boolean isReadOnlyCrossReferences();

	boolean isInitializing();

	void setConfigDTOs(final List<DTO_TYPE> dtos);

	List<DTO_TYPE> getConfigDTOs();

	DTO_TYPE getLeafConfigDTO();

	Set<String> getPropertiesWithDiff();

	void setPropertiesWithDiff(Set<String> propertiesWithDiff);

	boolean hasDiff();

	void setDiff(boolean diff);

	boolean isDetached();

	void setInitializingWhile(Runnable runnable);

	void setConfigAnnotation(Annotation annotation);

	Annotation getConfigAnnotation();
}
