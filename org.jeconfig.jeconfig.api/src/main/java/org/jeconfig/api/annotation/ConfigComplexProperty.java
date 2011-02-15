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

import org.jeconfig.api.annotation.merging.ItemMergingStrategy;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;

/**
 * Configures a complex type property to be stored in the configuration.<br>
 * <br>
 * Note that the complex type must be annotated with {@link ConfigComplexType}.<br>
 * Exception: the declared types of polymorph properties don't have to be
 * annotated but the types of the stored instances have to be annotated.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigComplexProperty {

	/**
	 * Specifies whether this property should handle polymorph elements.<br>
	 * Polymorph properties can hold sub-types of their declared property type.
	 */
	boolean polymorph() default false;

	/**
	 * Specifies the merging strategy for the complex property.<br>
	 * Either the parent object can be used or the child object can be used
	 * or both objects can be merged with each other using the merging strategies of their properties.
	 */
	ItemMergingStrategy mergingStrategy() default ItemMergingStrategy.MERGE;

	/**
	 * Specifies how stale properties should be handled.
	 * 
	 * @see StalenessSolutionStrategy
	 */
	StalenessSolutionStrategy stalenessSolutionStrategy() default StalenessSolutionStrategy.MERGE;
}
