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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jeconfig.api.exception.DefaultConfigExceptionHandler;
import org.jeconfig.api.exception.IConfigExceptionHandler;

/**
 * Marks a field to determine that the configuration injection aspect has to inject a configuration into it.<br>
 * The field type is used as the configuration type to load.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectConfig {

	/**
	 * The scope path of the configuration which should be injected.
	 */
	String[] scopePath() default {};

	/**
	 * The instance name of the configuration to use.<br>
	 * Used in conjunction with scope paths which contain the 'instance' scope. This
	 * property is automatically added to the 'instance' scope.
	 */
	String instanceName() default "";

	/**
	 * The instance name provider class of the configuration to use.<br>
	 * Used in conjunction with scope paths which contain the 'instance' scope. The value returned
	 * by the provider is automatically added to the 'instance' scope. When {@link InjectConfig#instanceName()} is set, this
	 * property is ignored.
	 */
	Class<? extends IInstanceNameProvider> instanceNameProvider() default NullInstanceNameProvider.class;

	/**
	 * <b>use with caution!!! no transactions are supported.</b><br>
	 * Determines whether the injected configuration should be automatically saved by the ConfigAutoSaveService when
	 * it became modified.
	 */
	boolean autoSave() default false;

	/**
	 * The exception handler which is notified if errors occur during load. It is also used by the
	 * ConfigAutoSaveService if {@link #autoSave()} is set to <code>true</code>.
	 */
	Class<? extends IConfigExceptionHandler> exceptionHandler() default DefaultConfigExceptionHandler.class;
}
