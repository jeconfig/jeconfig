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

package org.jeconfig.client.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jeconfig.api.IConfigUnsetter;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.client.internal.mapping.ConfigDTOMapper;
import org.jeconfig.client.internal.merging.ConfigMerger;
import org.jeconfig.client.proxy.IConfigProxy;
import org.jeconfig.client.proxy.IRootConfigProxy;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.common.reflection.PropertyAccessor;

public class ConfigUnsetterImpl implements IConfigUnsetter {
	private final ConfigMerger configMerger;
	private final ConfigDTOMapper configDtoMapper;
	private final PropertyAccessor propertyAccessor = new PropertyAccessor();

	public ConfigUnsetterImpl(final ConfigMerger configMerger, final ConfigDTOMapper configDtoMapper) {
		this.configMerger = configMerger;
		this.configDtoMapper = configDtoMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canUnsetConfig(final Object config) {
		if (config instanceof IConfigProxy) {
			final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) config;
			return !proxy.isDetached() && proxy.getConfigDTOs() != null && proxy.getConfigDTOs().size() > 0;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unsetProperties(final Object config, final String... properties) {
		if (canUnsetConfig(config)) {
			final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) config;
			final Set<String> newUnsetProperties = getNewUnsetProperties(proxy, properties);
			final Set<String> newPropertiesWithDiff = new HashSet<String>(proxy.getPropertiesWithDiff());
			newPropertiesWithDiff.removeAll(newUnsetProperties);

			if (newUnsetProperties.size() > 0) {
				final Object newMergedConfig = getNewMergedConfig(proxy, newPropertiesWithDiff);
				updateNewUnsetPropertiesOfConfig(proxy, newMergedConfig, newUnsetProperties);
				proxy.setPropertiesWithDiff(newPropertiesWithDiff);
				proxy.setDirty();
			}
		} else {
			throw new IllegalArgumentException("config cannot be unset; use canUnsetConfig() to check if unset is supported"); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unsetAllProperties(final Object config) {
		if (canUnsetConfig(config)) {
			final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) config;
			final Set<String> newUnsetProperties = new HashSet<String>(proxy.getPropertiesWithDiff());
			final boolean hasChanges = newUnsetProperties.size() > 0;
			if (hasChanges) {
				final Object newMergedConfig = getNewMergedConfig(proxy, new HashSet<String>());
				updateNewUnsetPropertiesOfConfig(proxy, newMergedConfig, newUnsetProperties);
				proxy.setPropertiesWithDiff(new HashSet<String>());
				proxy.setDirty();
			}
		} else {
			throw new IllegalArgumentException("config cannot be unset; use canUnsetConfig() to check if unset is supported"); //$NON-NLS-1$
		}
	}

	private Set<String> getNewUnsetProperties(final IConfigProxy<ComplexConfigDTO> config, final String[] unsetProperties) {
		final Set<String> result = new HashSet<String>();

		for (final String unsetProperty : unsetProperties) {
			if (config.getPropertiesWithDiff().contains(unsetProperty)) {
				result.add(unsetProperty);
			}
		}

		return result;
	}

	private void updateNewUnsetPropertiesOfConfig(
		final IConfigProxy<ComplexConfigDTO> config,
		final Object newConfig,
		final Set<String> newUnsetProperties) {

		for (final String propertyName : newUnsetProperties) {
			final Object newProperty = propertyAccessor.read(newConfig, propertyName);
			if (newProperty instanceof IConfigProxy) {
				((IConfigProxy<?>) newProperty).setParentProxy(null);
			}

			propertyAccessor.write(config, propertyName, newProperty);
		}
	}

	private Object getNewMergedConfig(final IConfigProxy<ComplexConfigDTO> configProxy, final Set<String> newPropertiesWithDiff) {
		final Class<?> configClass = ProxyUtil.getConfigClass(configProxy.getClass());

		final List<ComplexConfigDTO> originalDtos = configProxy.getConfigDTOs();
		if (configProxy.getScopePath().equals(configProxy.getLeafConfigDTO().getDefiningScopePath())) {
			final ComplexConfigDTO leafDtoCopy = configProxy.getLeafConfigDTO().deepCopy();
			removeNonDeclaredPropertiesFromDto(leafDtoCopy, newPropertiesWithDiff);
			originalDtos.set(originalDtos.size() - 1, leafDtoCopy);
		}

		final ComplexConfigDTO mergedDto = configMerger.mergeWithoutStalenessNotification(originalDtos, configClass);

		if (configProxy instanceof IRootConfigProxy) {
			return configDtoMapper.deserializeRootConfig(configClass, mergedDto, configProxy.getScopePath(), originalDtos);
		}

		final Object result = configDtoMapper.deserializeComplexConfig(
				configClass,
				mergedDto,
				configProxy.getScopePath(),
				originalDtos);
		((IConfigProxy<?>) result).setParentProxy(configProxy.getParentProxy());
		return result;
	}

	private void removeNonDeclaredPropertiesFromDto(final ComplexConfigDTO configDto, final Set<String> newDeclaredProperties) {
		for (final String declaredProperty : configDto.getDeclaredProperties()) {
			if (!newDeclaredProperties.contains(declaredProperty)) {
				configDto.removeProperty(declaredProperty);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isPropertySet(final Object config, final String property) {
		if (canUnsetConfig(config)) {
			final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) config;
			final Set<String> propertiesWithDiff = proxy.getPropertiesWithDiff();
			return propertiesWithDiff.contains(property);
		} else {
			throw new IllegalArgumentException("config cannot be unset; use canUnsetConfig() to check if unset is supported"); //$NON-NLS-1$
		}
	}
}
