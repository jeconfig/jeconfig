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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.IStalenessNotifier;
import org.jeconfig.api.exception.NoStalenessNotifier;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.common.reflection.ClassInstantiation;

/**
 * Merges the list of configurations into one new configuration using the
 * merging strategies of the configuration type and its sub-types.
 * 
 * This class is thread-safe.
 */
public final class ConfigMerger {
	private final Map<Class<? extends Annotation>, IPropertyMerger> mergers;
	private final ComplexTypeMerger complexTypeMerger;

	public ConfigMerger(final ISimpleTypeConverterRegistry converterRegistry) {
		final ConfigMergerInitializer mergerInitializer = new ConfigMergerInitializer(converterRegistry);
		mergers = new HashMap<Class<? extends Annotation>, IPropertyMerger>(mergerInitializer.getFieldMergers());
		complexTypeMerger = new ComplexTypeMerger();
	}

	/**
	 * Merges the list of configurations into one new configuration using the
	 * merging strategies of the configuration type and its sub-types.
	 * 
	 * @param configs the configuration objects to be merged with their scopes
	 * @param configClass the type of the objects (containing the annotations)
	 * @return a merged new configuration object
	 */
	public ComplexConfigDTO mergeWithoutStalenessNotification(final List<ComplexConfigDTO> configs, final Class<?> configClass) {
		return merge(null, configs, configClass, null, false);
	}

	/**
	 * Merges the list of configurations into one new configuration using the
	 * merging strategies of the configuration type and its sub-types.
	 * 
	 * @param scopePath
	 * @param configs the configuration objects to be merged with their scopes
	 * @param configClass the type of the objects (containing the annotations)
	 * @param globalStalenessNotifier
	 * @return a merged new configuration object
	 */
	public ComplexConfigDTO merge(
		final IScopePath scopePath,
		final List<ComplexConfigDTO> configs,
		final Class<?> configClass,
		final IStalenessNotifier globalStalenessNotifier) {
		return merge(scopePath, configs, configClass, globalStalenessNotifier, true);
	}

	private ComplexConfigDTO merge(
		final IScopePath scopePath,
		final List<ComplexConfigDTO> configs,
		final Class<?> configClass,
		final IStalenessNotifier globalStalenessNotifier,
		final boolean notifyStaleness) {
		Assert.paramNotEmpty(configs, "configs"); //$NON-NLS-1$
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$

		final ConfigClass annotation = AnnotationUtil.getAnnotation(configClass, ConfigClass.class);
		if (annotation == null) {
			throw new IllegalArgumentException(
				"The configuration class must be annotated with @" + ConfigClass.class.getSimpleName()); //$NON-NLS-1$
		}

		if (configs.size() == 0) {
			throw new IllegalArgumentException("Got no configs!"); //$NON-NLS-1$
		}

		final StalePropertiesMergingResultImpl stalePropertiesMergingResult = new StalePropertiesMergingResultImpl(scopePath);
		final ComplexConfigDTO result = mergeConfigs(getConfigsToMerge(configs), configClass, stalePropertiesMergingResult);

		if (notifyStaleness && stalePropertiesMergingResult.hasProperties()) {
			IStalenessNotifier stalenessNotifier = globalStalenessNotifier;
			if (annotation.stalenessNotfier() != NoStalenessNotifier.class) {
				stalenessNotifier = new ClassInstantiation().newInstance(annotation.stalenessNotfier());
			}
			if (stalenessNotifier != null) {
				stalenessNotifier.loadedStaleConfig(stalePropertiesMergingResult);
			}
		}

		return result;
	}

	private ComplexConfigDTO mergeConfigs(
		final List<ComplexConfigDTO> configs,
		final Class<?> configClass,
		final StalePropertiesMergingResultImpl stalePropertiesMergingResult) {

		ComplexConfigDTO result = configs.get(0);
		for (int i = 1; i < configs.size(); i++) {
			final ComplexConfigDTO parentConfig = result;
			final ComplexConfigDTO childConfig = configs.get(i);

			result = complexTypeMerger.merge(parentConfig, childConfig, configClass, mergers, stalePropertiesMergingResult);
		}
		return result;
	}

	private ComplexConfigDTO getCodeDefaultConfig(final List<ComplexConfigDTO> configs) {
		for (final ComplexConfigDTO config : configs) {
			if (config != null && CodeDefaultScopeDescriptor.NAME.equals(config.getDefiningScopePath().getLastScope().getName())) {
				return config;
			}
		}
		return null;
	}

	private ComplexConfigDTO getClassConfig(final List<ComplexConfigDTO> configs) {
		for (final ComplexConfigDTO entry : configs) {
			if (ClassScopeDescriptor.NAME.equals(entry.getDefiningScopePath().getLastScope().getName())) {
				return entry;
			}
		}
		throw new IllegalArgumentException("Didn't find a class configuration in the configurations to merge!"); //$NON-NLS-1$
	}

	private List<ComplexConfigDTO> getConfigsToMerge(final List<ComplexConfigDTO> configs) {
		final List<ComplexConfigDTO> result = new ArrayList<ComplexConfigDTO>();

		final ComplexConfigDTO codeDefaultConfig = getCodeDefaultConfig(configs);
		if (codeDefaultConfig != null) {
			result.add(codeDefaultConfig);
		} else {
			result.add(getClassConfig(configs));
		}

		for (final ComplexConfigDTO config : configs) {
			// class and code-default configurations are not merged with the
			// other configurations
			if (config != null
				&& !ClassScopeDescriptor.NAME.equals(config.getDefiningScopePath().getLastScope().getName())
				&& !CodeDefaultScopeDescriptor.NAME.equals(config.getDefiningScopePath().getLastScope().getName())) {
				result.add(config);
			}
		}

		return result;
	}

}
