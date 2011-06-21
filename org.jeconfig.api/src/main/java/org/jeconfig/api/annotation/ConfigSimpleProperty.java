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

package org.jeconfig.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jeconfig.api.annotation.merging.SimpleValueMergingStrategy;
import org.jeconfig.api.annotation.merging.MergingStrategies;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;

/**
 * Configures a simple type property to be stored in the configuration.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSimpleProperty {

	/**
	 * The converter which is used to convert simple types of this property into their serialized form.<br>
	 * If no custom converter is set, a converter is searched in the global converter registry of the
	 * configuration setup service.
	 */
	Class<? extends SimpleTypeConverter<?>> customConverter() default NoCustomSimpleTypeConverter.class;

	/**
	 * The strategy which is used when values of the property must be merged.
	 */
	Class<? extends SimpleValueMergingStrategy<?>> mergingStrategy() default MergingStrategies.ChildOverwrites.class;

	/**
	 * Specifies how stale properties should be handled.
	 * 
	 * @see StalenessSolutionStrategy
	 */
	StalenessSolutionStrategy stalenessSolutionStrategy() default StalenessSolutionStrategy.USE_PARENT;

}
