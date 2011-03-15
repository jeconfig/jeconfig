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

package org.jeconfig.client.internal.validation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.IConfigService;
import org.jeconfig.api.IConfigSetupService;
import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigCrossReference;
import org.jeconfig.api.annotation.ConfigIdProperty;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.conversion.ISimpleTypeConverterRegistry;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopeRegistry;
import org.jeconfig.api.util.Assert;
import org.jeconfig.client.internal.AnnotationUtil;
import org.jeconfig.client.proxy.ProxyUtil;

@SuppressWarnings("nls")
public final class ConfigValidator {
	private final Map<Class<? extends Annotation>, IPropertyValidator<?>> validators;

	private final IScopeRegistry scopeRegistry;
	private final ISimpleTypeConverterRegistry converterRegistry;
	private final ComplexTypeValidator complexTypeValidator;
	private final CrossReferencesCycleDetector cycleDetector;
	private final MigrationValidator migrationValidator;

	public ConfigValidator(final IConfigService configService, final IConfigSetupService configSetupService) {

		this.scopeRegistry = configSetupService.getScopeRegistry();
		this.converterRegistry = configSetupService.getSimpleTypeConverterRegistry();
		complexTypeValidator = new ComplexTypeValidator(configSetupService.getSimpleTypeConverterRegistry());
		cycleDetector = new CrossReferencesCycleDetector(configService);
		migrationValidator = new MigrationValidator();

		validators = new HashMap<Class<? extends Annotation>, IPropertyValidator<?>>();
		validators.put(ConfigArrayProperty.class, new ArrayPropertyValidator());
		validators.put(ConfigComplexProperty.class, new ComplexPropertyValidator());
		validators.put(ConfigIdProperty.class, new IdPropertyValidator());
		validators.put(ConfigListProperty.class, new ListPropertyValidator());
		validators.put(ConfigMapProperty.class, new MapPropertyValidator());
		validators.put(ConfigSetProperty.class, new SetPropertyValidator());
		validators.put(ConfigSimpleProperty.class, new SimplePropertyValidator());
		validators.put(ConfigCrossReference.class, new CrossReferencePropertyValidator());
	}

	public void validate(final Object config, final IScopePath scopePath) {
		Assert.paramNotNull(config, "config"); //$NON-NLS-1$
		validate(config.getClass(), config, scopePath);
	}

	public void validate(final Class<?> configClass, final IScopePath scopePath) {
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$
		validate(configClass, null, scopePath);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void validate(final Class<?> configClass, final Object config, final IScopePath scopePath) {
		final ConfigClass configClassAnnotation = AnnotationUtil.getAnnotation(configClass, ConfigClass.class);
		if (configClassAnnotation == null) {
			throw new IllegalArgumentException("A configuration class must be annotated with the @"
				+ ConfigClass.class.getSimpleName()
				+ " annotation!");
		}

		final String scopeClassName = getClassNameFromScope(scopePath);
		final Class<?> nonProxyConfigClass = ProxyUtil.getConfigClass(configClass);
		if (!nonProxyConfigClass.getName().equals(scopeClassName)) {
			throw new IllegalArgumentException("scope and config class do not match");
		}

		validateScopePath(configClass, configClassAnnotation);

		final Set<Class<?>> validatedComplexTypes = new HashSet<Class<?>>();
		complexTypeValidator.validate(configClass, config, (Map) validators, converterRegistry, validatedComplexTypes);

		migrationValidator.validate(configClass);

		// detect for cross reference cycles
		cycleDetector.detectCycles(configClass, scopePath);
	}

	private String getClassNameFromScope(final IScopePath scopePath) {
		final IScope classScope = scopePath.findScopeByName(ClassScopeDescriptor.NAME);
		if (classScope != null) {
			return classScope.getProperty(ClassScopeDescriptor.PROP_CLASS_NAME);
		}
		throw new IllegalArgumentException("Didn't find Class Scope");
	}

	private void validateScopePath(final Class<?> configClass, final ConfigClass annotation) {
		final Set<String> usedScopeNames = new HashSet<String>();
		for (final String scopeName : annotation.scopePath()) {
			if (usedScopeNames.contains(scopeName)) {
				throw new IllegalArgumentException("The config class '"
					+ configClass
					+ "' uses the scope '"
					+ scopeName
					+ "' twice in its path which is not allowed!");
			}
			usedScopeNames.add(scopeName);

			if (scopeRegistry.getScopeDescriptor(scopeName) == null) {
				throw new IllegalArgumentException("The scope '"
					+ scopeName
					+ "' is not registered at the scope registry of the configuration service!");
			}

			validateIsNoDefaultScope(configClass, scopeName, ClassScopeDescriptor.NAME);
			validateIsNoDefaultScope(configClass, scopeName, CodeDefaultScopeDescriptor.NAME);
		}
	}

	private void validateIsNoDefaultScope(final Class<?> configClass, final String usedScopeName, final String defaultScopeName) {

		if (defaultScopeName.equals(usedScopeName)) {
			throw new IllegalArgumentException("The scope '"
				+ defaultScopeName
				+ "' is automatically added to the scope path. "
				+ "Please remove it from the path of the config class '"
				+ configClass
				+ "'!");
		}
	}
}
