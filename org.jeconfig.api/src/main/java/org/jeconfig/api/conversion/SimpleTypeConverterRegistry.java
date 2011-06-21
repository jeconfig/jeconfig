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

package org.jeconfig.api.conversion;

/**
 * Registry which holds all global {@link SimpleTypeConverter}s.<br>
 * The configuration service requests converters when simple type configuration properties
 * are serialized/deserialized.
 */
public interface SimpleTypeConverterRegistry {

	/**
	 * Registers a converter to be used when configuration properties of the given type are serialized/deserialized.<br>
	 * If there is already a converter registered for the type, it is replaced by the new one.
	 * 
	 * @param <T> the type of the properties the converter should be applied to
	 * @param simpleType must not be <code>null</code>
	 * @param converter the converter which should be used to convert the type; must not be <code>null</code>
	 */
	<T> void addConverter(final Class<T> simpleType, final SimpleTypeConverter<T> converter);

	/**
	 * Un-registers the given converter.
	 * 
	 * @param <T>
	 * @param simpleType the type of the properties the converter is applied to; must not be <code>null</code>
	 * @param converter the converter to remove; must not be <code>null</code>
	 */
	<T> void removeConverter(final Class<T> simpleType, final SimpleTypeConverter<T> converter);

	/**
	 * Returns the converter which is registered for the given type.
	 * 
	 * @param <T> the type
	 * @param simpleType must not be <code>null</code>
	 * @return the registered converter or <code>null</code> if no converter is registered for the type
	 */
	<T> SimpleTypeConverter<T> getConverter(final Class<T> simpleType);

	/**
	 * Converts the given object into its serialized form using the converter which is registered
	 * for the type of the object.<br>
	 * This method must only be invoked if a converter is registered for the object type.
	 * 
	 * @param <T>
	 * @param object the object to serialize
	 * @return the serialized object or <code>null</code> if the object is null
	 */
	<T> String convertToSerializedForm(T object);

	/**
	 * Converts the given String into its object form using the converter which is registered for the type
	 * of the object.<br>
	 * This method must only be invoked if a converter is registered for the object type.
	 * 
	 * @param <T>
	 * @param objectClass the type of the object to create
	 * @param serializedForm the serialized object
	 * @return the object form
	 */
	<T> T convertToObject(final Class<T> objectClass, String serializedForm);

	/**
	 * Indicates whether a converter is registered for the given type.
	 * 
	 * @param type
	 * @return <code>true</code> if a converter is registered for the type; else <code>false</code>
	 */
	boolean isTypeSupported(final Class<?> type);
}
