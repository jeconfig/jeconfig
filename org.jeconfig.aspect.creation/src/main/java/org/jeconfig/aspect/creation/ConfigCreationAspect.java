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

package org.jeconfig.aspect.creation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.IInternalConfigService;

/**
 * Aspect which hooks into constructor invocations of configuration classes (all classes annotated with @ConfigComplexType)
 * and creates a proper configuration object.<br>
 * <br>
 * Normally it's not possible to use configuration objects created with 'new'. You must use the configuration service
 * to create them. But with this aspect the objects can be created using normal constructors.<br>
 * <br>
 * This aspect offers even more functionality than the configuration service:<br>
 * The configuration service is only able to create configuration objects using the default constructor. But this
 * aspect hooks in all constructors and creates proper configuration objects.
 */
@Aspect
public class ConfigCreationAspect {

	@Pointcut("call((@org.jeconfig.api.annotation.ConfigClass *).new (..))")
	public void createRootConfigObject() {}

	@Around("createRootConfigObject()")
	public Object interceptCreateRootConfigObject(final ProceedingJoinPoint pjp) throws Throwable {
		if (pjp.getSignature() instanceof ConstructorSignature) {
			final ConstructorSignature sig = (ConstructorSignature) pjp.getSignature();
			final Class<?> clazz = sig.getDeclaringType();
			final Object[] args = pjp.getArgs();
			if (args != null && args.length > 0) {
				throw new IllegalArgumentException("Config Class must be instantiated by its default constructor: " + sig); //$NON-NLS-1$
			}
			final IInternalConfigService configService = Activator.getInstance().getConfigService();
			final IScopePathBuilderFactory scopePathBuilderFactory = configService.getScopePathBuilderFactory(clazz);
			return configService.load(clazz, scopePathBuilderFactory.annotatedPath().create());
		} else {
			throw new IllegalArgumentException("this should never happen..."); //$NON-NLS-1$
		}
	}

	@Pointcut("call((@org.jeconfig.api.annotation.ConfigComplexType *).new (..))")
	public void createConfigObject() {}

	@Around("createConfigObject()")
	public Object interceptCreateConfigObject(final ProceedingJoinPoint pjp) throws Throwable {
		if (pjp.getSignature() instanceof ConstructorSignature) {
			final ConstructorSignature sig = (ConstructorSignature) pjp.getSignature();
			final Class<?> clazz = sig.getDeclaringType();
			final Object[] args = pjp.getArgs();
			final Class<?>[] types = sig.getParameterTypes();
			final IInternalConfigService configService = Activator.getInstance().getConfigService();
			return configService.createComplexObject(clazz, types, args);
		} else {
			throw new IllegalArgumentException("this should never happen..."); //$NON-NLS-1$
		}
	}
}
