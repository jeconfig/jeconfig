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

import org.jeconfig.api.annotation.merging.ISimpleValueMergingStrategy;
import org.jeconfig.api.annotation.merging.ItemExistenceStrategy;
import org.jeconfig.api.annotation.merging.ItemMergingStrategy;
import org.jeconfig.api.annotation.merging.MergingStrategies;
import org.jeconfig.api.annotation.merging.StalenessSolutionStrategy;
import org.jeconfig.api.conversion.ISimpleTypeConverter;
import org.jeconfig.api.conversion.NoCustomSimpleTypeConverter;

/**
 * Configures a Map property to be stored in the configuration.<br>
 * The Map can store simple (primitives, String, java.util.Date, Enum) or complex values.
 * keys must be simple types.<br>
 * Nested collections are not supported.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigMapProperty {

	/**
	 * The key type of the map.
	 * Only simple types are allowed as map key types.
	 * Keys are not merged.
	 */
	Class<?> keyType() default String.class;

	/**
	 * Specifies whether this property should handle polymorph entries.<br>
	 * Polymorph maps can hold sub-types of their value type.
	 */
	boolean polymorph() default false;

	/**
	 * The value type of the map entry.<br>
	 * Can be a simple type (primitives and their wrappers, String, java.util.Date, Enum)
	 * or a complex type or an interface if {@link #polymorph()} is <code>true</code>.
	 */
	Class<?> valueType() default String.class;

	/**
	 * Specifies the behavior when a key exists in the parent and in the child set. <br>
	 * <br>
	 * If using simple value types and a MergingStrategy set to {@link ItemMergingStrategy#MERGE},
	 * you can use the {@link #simpleValueMergingStrategy()} to specify custom merging behavior.
	 */
	ItemMergingStrategy mergingStrategy() default ItemMergingStrategy.USE_CHILD;

	/**
	 * The strategy which is used when simple type values must be merged.<br>
	 * It is only used if the map contains simple type values and the {@link #mergingStrategy()} is set to
	 * {@link ItemMergingStrategy#MERGE}. <br>
	 * 
	 * <b> Only supported for Simple Map value types!!!</b>
	 */
	Class<? extends ISimpleValueMergingStrategy<?>> simpleValueMergingStrategy() default MergingStrategies.ChildOverwrites.class;

	/**
	 * Specifies the behavior when an entry does not exist in the child.
	 */
	ItemExistenceStrategy entryRemovedStrategy() default ItemExistenceStrategy.REMOVE;

	/**
	 * Specifies the behavior when an entry only exists in the child.
	 */
	ItemExistenceStrategy entryAddedStrategy() default ItemExistenceStrategy.ADD;

	/**
	 * Specifies how stale properties should be handled.
	 * 
	 * @see StalenessSolutionStrategy
	 */
	StalenessSolutionStrategy stalenessSolutionStrategy() default StalenessSolutionStrategy.USE_PARENT;

	/**
	 * The converter which is used to convert keys of this property into their serialized form.<br>
	 * If no custom converter is set, a converter is searched in the global converter registry of the
	 * configuration setup service. <br>
	 */
	Class<? extends ISimpleTypeConverter<?>> customKeyConverter() default NoCustomSimpleTypeConverter.class;

	/**
	 * The converter which is used to convert simple types of this property into their serialized form.<br>
	 * If no custom converter is set, a converter is searched in the global converter registry of the
	 * configuration setup service. <br>
	 * 
	 * <b> Only supported for Simple Map value types!!!</b>
	 */
	Class<? extends ISimpleTypeConverter<?>> customValueConverter() default NoCustomSimpleTypeConverter.class;
}
