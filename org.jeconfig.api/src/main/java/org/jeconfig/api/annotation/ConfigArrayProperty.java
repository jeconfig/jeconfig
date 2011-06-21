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

import org.jeconfig.api.annotation.merging.ListItemMergingStrategy;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;
import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;

/**
 * Configures an array property to be stored in the configuration. <br>
 * The array can story simple (primitives, String, java.util.Date, Enum) or complex values.<br>
 * <br>
 * The exchange of array elements is not allowed because they can't be tracked by the configuration service.<br>
 * If you want to exchange array elements you must set a new array containing the desired elements by using the setter-method.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigArrayProperty {

	/**
	 * Specifies whether this property should handle polymorph elements.<br>
	 * Polymorph arrays can hold sub-types of their item type.
	 */
	boolean polymorph() default false;

	/**
	 * Specifies the how merging is done.<br>
	 * There is the possibility to specify whether to use the
	 * parent array ({@link ListItemMergingStrategy#USE_PARENT})
	 * or the child array ({@link ListItemMergingStrategy#USE_CHILD}). <br>
	 * <br>
	 * The single items of the array can't be merged with each other.<br>
	 * If single items should be merged, use a set ({@link ConfigSetProperty}) or
	 * a map ({@link ConfigMapProperty}) instead.
	 */
	ListItemMergingStrategy mergingStrategy() default ListItemMergingStrategy.USE_CHILD;

	/**
	 * Specifies how stale properties should be handled.
	 * 
	 * @see StalenessSolutionStrategy
	 */
	StalenessSolutionStrategy stalenessSolutionStrategy() default StalenessSolutionStrategy.USE_PARENT;

	/**
	 * The converter which is used to convert simple types of this property into their serialized form.<br>
	 * If no custom converter is set, a converter is searched in the global converter registry of the
	 * configuration setup service. <br>
	 * 
	 * <b> Only supported for Simple Array component Types!!!</b>
	 */
	Class<? extends SimpleTypeConverter<?>> customConverter() default NoCustomSimpleTypeConverter.class;
}
