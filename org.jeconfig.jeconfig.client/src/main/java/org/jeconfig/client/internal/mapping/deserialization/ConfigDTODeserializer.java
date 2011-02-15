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

package org.jeconfig.client.internal.mapping.deserialization;

import java.util.List;

import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.client.proxy.IConfigObjectFactory;
import org.jeconfig.client.proxy.ProxyUpdater;
import org.jeconfig.client.proxy.ProxyUtil;

public final class ConfigDTODeserializer {
	private final IConfigObjectFactory proxyFactory;
	private final ComplexDTODeserializer complexDTODeserializer;

	public ConfigDTODeserializer(
		final ISimpleTypeConverterRegistry simpleTypeConverterRegistry,
		final IConfigObjectFactory proxyFactory,
		final ProxyUpdater proxyUpdater) {
		complexDTODeserializer = new ComplexDTODeserializer(proxyFactory, simpleTypeConverterRegistry, proxyUpdater);
		this.proxyFactory = proxyFactory;
	}

	public <T> T derserializeRootConfig(
		final Class<T> configClass,
		final ComplexConfigDTO mergedConfigDTO,
		final IScopePath scopePath,
		final List<ComplexConfigDTO> dtos) {

		final T config = proxyFactory.createRootConfigProxy(ProxyUtil.getConfigClass(configClass), scopePath);
		complexDTODeserializer.processConfig(configClass, mergedConfigDTO, config, dtos, scopePath);

		return config;
	}

	public <T> T deserializeComplexConfig(
		final Class<T> configClass,
		final ComplexConfigDTO mergedConfigDTO,
		final IScopePath scopePath,
		final List<ComplexConfigDTO> dtos) {

		final T config = proxyFactory.createComplexProperty(ProxyUtil.getConfigClass(configClass));
		complexDTODeserializer.processConfig(configClass, mergedConfigDTO, config, dtos, scopePath);

		return config;
	}
}
