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

package org.jeconfig.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jeconfig.api.ConfigServiceAccessor;
import org.jeconfig.api.ConfigService;
import org.jeconfig.api.annotation.ConfigClass;
import org.jeconfig.api.autosave.ConfigAutoSaveService;
import org.jeconfig.api.exception.ConfigExceptionHandler;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.aspect.internal.Activator;
import org.jeconfig.common.reflection.ClassInstantiation;
import org.jeconfig.common.reflection.FieldAccessor;

/**
 * Injects configurations into fields which are annotated with @InjectConfig.<br>
 * Doesn't allow modifications of these fields.<br>
 * <br>
 * Note that this aspect only hooks into objects of bundles which depend on this bundle!<br>
 */
@Aspect
public final class ConfigInjectAspect {
	private final ClassInstantiation classInstantiation = new ClassInstantiation();
	private final FieldAccessor fieldAccessor = new FieldAccessor();

	@Pointcut("this(configUser) && get(@org.jeconfig.aspect.InjectConfig * *) && @annotation(injectConfig)")
	public void readConfigField(final Object configUser, final InjectConfig injectConfig) {}

	@Pointcut("this(configUser) && set(@org.jeconfig.aspect.InjectConfig * *) && @annotation(injectConfig)")
	public void writeConfigField(final Object configUser, final InjectConfig injectConfig) {}

	@Around("readConfigField(configUser, injectConfig)")
	public Object interceptReadConfigField(final ProceedingJoinPoint pjp, final Object configUser, final InjectConfig injectConfig) throws Throwable {

		Object result = pjp.proceed();
		if (result == null) {
			final ConfigExceptionHandler exceptionHandler = classInstantiation.newInstance(injectConfig.exceptionHandler());
			final ConfigService configService = new ConfigServiceAccessor(
				Activator.getInstance().getConfigService(),
				exceptionHandler);
			final ConfigAutoSaveService configAutoSaveService = Activator.getInstance().getConfigAutoSaveService();

			final String fieldName = pjp.getSignature().getName();
			final Class<?> configClass = fieldAccessor.getFieldType(configUser.getClass(), fieldName);

			final ScopePath scopePath = buildScopePath(configService, configUser, configClass, injectConfig);
			if (configAutoSaveService.hasDirtyConfig(scopePath)) {
				configAutoSaveService.flush();
			}

			result = configService.load(configClass, scopePath);
			if (injectConfig.autoSave()) {
				configAutoSaveService.manageConfig(result, exceptionHandler);
			}
			fieldAccessor.write(configUser, fieldName, result);
		}
		return result;
	}

	private ScopePath buildScopePath(
		final ConfigService configService,
		final Object configUser,
		final Class<?> configClass,
		final InjectConfig injectConfigAnnotation) {

		final ScopePathBuilderFactory scopePathFactory = configService.getScopePathBuilderFactory(configClass);
		final ScopePathBuilder scopePathBuilder;
		String[] scopeNames;
		if (injectConfigAnnotation.scopePath().length > 0) {
			scopeNames = injectConfigAnnotation.scopePath();
		} else {
			scopeNames = configClass.getAnnotation(ConfigClass.class).scopePath();
		}
		scopePathBuilder = scopePathFactory.stub().appendAll(scopeNames);
		if (!injectConfigAnnotation.instanceName().isEmpty()) {
			scopePathBuilder.addPropertyToScope(
					InstanceScopeDescriptor.NAME,
					InstanceScopeDescriptor.PROP_INSTANCE_NAME,
					injectConfigAnnotation.instanceName());
		} else {
			final InstanceNameProvider instanceNameProvider = classInstantiation.newInstance(injectConfigAnnotation.instanceNameProvider());
			final String instanceName = instanceNameProvider.getInstanceName(configUser);
			if (instanceName != null) {
				scopePathBuilder.addPropertyToScope(
						InstanceScopeDescriptor.NAME,
						InstanceScopeDescriptor.PROP_INSTANCE_NAME,
						instanceName);
			}
		}

		return scopePathBuilder.create();
	}

	@Around("writeConfigField(configUser, injectConfig)")
	public void interceptWriteConfigField(final ProceedingJoinPoint pjp, final Object configUser, final InjectConfig injectConfig) throws Throwable {

		throw new UnsupportedOperationException("The field '" //$NON-NLS-1$
			+ pjp.getSignature()
			+ "' is automatically filled by the config injection aspect and cannot be set manually!"); //$NON-NLS-1$
	}
}
