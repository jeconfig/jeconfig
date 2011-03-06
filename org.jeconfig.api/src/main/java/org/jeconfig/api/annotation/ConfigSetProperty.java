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

import org.jeconfig.api.annotation.merging.ItemExistenceStrategy;
import org.jeconfig.api.annotation.merging.ItemMergingStrategy;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;
import org.jeconfig.api.conversion.ISimpleTypeConverter;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;

/**
 * Configures a List property to be stored in the configuration.<br>
 * The set can store simple (primitives, String, java.util.Date, Enum) or complex values. <br>
 * Nested collections are not supported.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSetProperty {

	/**
	 * Specifies whether this property should handle polymorph elements.<br>
	 * Polymorph sets can hold sub-types of their item type but the items cannot be merged.
	 */
	boolean polymorph() default false;

	/**
	 * The type of the set elements.<br>
	 * Can be a simple type (primitives and their wrappers, String, java.util.Date, Enum)
	 * or a complex type or an interface if {@link #polymorph()} is <code>true</code>.<br>
	 * <br>
	 * The complex types hold by a set must have an ID property (see @ConfigIdProperty).
	 */
	Class<?> itemType();

	/**
	 * Specifies the behavior when an item exists in the parent and in the child set. <br>
	 * <br> {@link ItemMergingStrategy#MERGE} is only supported for sets with non-polymorph complex items.
	 */
	ItemMergingStrategy mergingStrategy() default ItemMergingStrategy.USE_CHILD;

	/**
	 * Specifies the behavior when an item does not exist in the child set.
	 */
	ItemExistenceStrategy itemRemovedStrategy() default ItemExistenceStrategy.REMOVE;

	/**
	 * Specifies the behavior when an item only exists in the child set.
	 */
	ItemExistenceStrategy itemAddedStrategy() default ItemExistenceStrategy.ADD;

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
	 * <b> Only supported for Simple Set item types!!!</b>
	 */
	Class<? extends ISimpleTypeConverter<?>> customConverter() default NoCustomSimpleTypeConverter.class;
}
