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

import java.util.List;

import org.jeconfig.api.dto.IConfigDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.proxy.ProxyUtil;

public abstract class AbstractDTOSerializer {
	protected static final long INITIAL_CONFIG_VERSION = 1;

	protected Class<?> getValueType(final Object value) {
		if (value != null) {
			return ProxyUtil.getConfigClass(value.getClass());
		}
		return null;
	}

	private <T extends IConfigDTO> long getNewVersion(final T configDTO, final List<T> originalDTOs, final IScopePath scopePath) {
		final T lastOriginalDTO = getLastOriginalDTO(originalDTOs);
		if (lastOriginalDTO != null && lastOriginalDTO.getDefiningScopePath().equals(scopePath)) {
			if (configDTO.equals(lastOriginalDTO)) {
				return lastOriginalDTO.getVersion();
			}
			return lastOriginalDTO.getVersion() + 1;
		}
		return INITIAL_CONFIG_VERSION;
	}

	private <T> T getLastOriginalDTO(final List<T> originalDTOs) {
		if (originalDTOs != null && originalDTOs.size() > 0) {
			return originalDTOs.get(originalDTOs.size() - 1);
		}
		return null;
	}

	private <T> T getSecondLastOriginalDTO(final List<T> originalDTOs) {
		if (originalDTOs != null && originalDTOs.size() > 1) {
			return originalDTOs.get(originalDTOs.size() - 2);
		}
		return null;
	}

	private <T extends IConfigDTO> T getParentDTO(final List<T> originalDTOs, final IScopePath scopePath) {
		final T lastOriginalDTO = getLastOriginalDTO(originalDTOs);
		if (lastOriginalDTO != null) {
			if (lastOriginalDTO.getDefiningScopePath().equals(scopePath)) {
				return getSecondLastOriginalDTO(originalDTOs);
			} else {
				return lastOriginalDTO;
			}
		}
		return null;
	}

	protected <T extends IConfigDTO> void updateVersionsAndParent(
		final T configDTO,
		final List<T> originalDTOs,
		final IScopePath scopePath) {

		final T parentDTO = getParentDTO(originalDTOs, scopePath);
		final long parentVersion = parentDTO != null ? parentDTO.getVersion() : 0;
		final String parentScopeName = parentDTO != null ? parentDTO.getDefiningScopePath().getLastScope().getName() : null;

		configDTO.setVersion(getNewVersion(configDTO, originalDTOs, scopePath));
		configDTO.setParentVersion(parentVersion);
		configDTO.setParentScopeName(parentScopeName);
	}
}
