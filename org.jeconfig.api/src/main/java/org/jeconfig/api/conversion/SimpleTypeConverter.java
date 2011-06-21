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
 * A converter which is able to convert simple types from and to their serialized (String) form.<br>
 * Using custom simple type converters it is possible to
 * <ul>
 * <li>Change the form simple values (e.g. java.util.Date) are persisted</li>
 * <li>Extend the configuration service to save simple types which are not supported out-of-the-box (e.g. java.util.Calendar)
 * <li>Extend the configuration service to save arbitrary complex types in custom String form (not recommended)
 * </ul>
 * 
 * Simple type converters may be registered globally or locally.<br>
 * <ul>
 * <li>Global converters are registered for a specified type and are used when any configuration properties of this type are
 * serialized/deserialized and no local converters are registered. Global converters are registered at the
 * {@link SimpleTypeConverterRegistry} of the ConfigSetupService.</li>
 * <li>Local converters can be annotated at configuration properties (see the various Config*Property-annotations) and are only
 * used to serialize/deserialize the specific properties.</li>
 * </ul>
 * This interface may be implemented by clients.
 * 
 * @param <T> the type of the objects this converter can handle
 */
public interface SimpleTypeConverter<T> {

	/**
	 * Converts the given object into its serialized form.
	 * 
	 * @param object the object to serialize; may be <code>null</code>
	 * @return the serialized for of the object; may be <code>null</code>
	 */
	String convertToSerializedForm(T object);

	/**
	 * Creates an object by parsing its serialized form.
	 * 
	 * @param simpleType the type of the object to return
	 * @param serializedForm the serialized object; may be <code>null</code>
	 * @return the object; may be <code>null</code>
	 */
	T convertToObject(Class<T> simpleType, String serializedForm);
}
