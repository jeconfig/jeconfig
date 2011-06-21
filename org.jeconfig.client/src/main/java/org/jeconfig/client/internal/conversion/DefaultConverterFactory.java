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

package org.jeconfig.client.internal.conversion;

import java.util.Date;

import org.jeconfig.api.conversion.SimpleTypeConverter;
import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;

public final class DefaultConverterFactory {

	public void createConverters(final SimpleTypeConverterRegistry converterRegistry) {
		converterRegistry.addConverter(Integer.class, new IntConverter());
		converterRegistry.addConverter(int.class, new IntConverter());
		converterRegistry.addConverter(Long.class, new LongConverter());
		converterRegistry.addConverter(long.class, new LongConverter());
		converterRegistry.addConverter(Short.class, new ShortConverter());
		converterRegistry.addConverter(short.class, new ShortConverter());
		converterRegistry.addConverter(Byte.class, new ByteConverter());
		converterRegistry.addConverter(byte.class, new ByteConverter());
		converterRegistry.addConverter(Float.class, new FloatConverter());
		converterRegistry.addConverter(float.class, new FloatConverter());
		converterRegistry.addConverter(Double.class, new DoubleConverter());
		converterRegistry.addConverter(double.class, new DoubleConverter());
		converterRegistry.addConverter(Boolean.class, new BooleanConverter());
		converterRegistry.addConverter(boolean.class, new BooleanConverter());
		converterRegistry.addConverter(String.class, new StringConverter());
		converterRegistry.addConverter(Date.class, new DateConverter());
		converterRegistry.addConverter(Enum.class, new EnumConverter());
	}

	private static class IntConverter implements SimpleTypeConverter<Integer> {
		@Override
		public String convertToSerializedForm(final Integer object) {
			return object.toString();
		}

		@Override
		public Integer convertToObject(final Class<Integer> simpleType, final String serializedForm) {
			return Integer.valueOf(Integer.parseInt(serializedForm));
		}
	}

	private static class LongConverter implements SimpleTypeConverter<Long> {
		@Override
		public String convertToSerializedForm(final Long object) {
			return object.toString();
		}

		@Override
		public Long convertToObject(final Class<Long> simpleType, final String serializedForm) {
			return Long.valueOf(Long.parseLong(serializedForm));
		}
	}

	private static class ShortConverter implements SimpleTypeConverter<Short> {
		@Override
		public String convertToSerializedForm(final Short object) {
			return object.toString();
		}

		@Override
		public Short convertToObject(final Class<Short> simpleType, final String serializedForm) {
			return Short.valueOf(Short.parseShort(serializedForm));
		}
	}

	private static class ByteConverter implements SimpleTypeConverter<Byte> {
		@Override
		public String convertToSerializedForm(final Byte object) {
			return object.toString();
		}

		@Override
		public Byte convertToObject(final Class<Byte> simpleType, final String serializedForm) {
			return Byte.valueOf(Byte.parseByte(serializedForm));
		}
	}

	private static class FloatConverter implements SimpleTypeConverter<Float> {
		@Override
		public String convertToSerializedForm(final Float object) {
			return object.toString();
		}

		@Override
		public Float convertToObject(final Class<Float> simpleType, final String serializedForm) {
			return Float.valueOf(Float.parseFloat(serializedForm));
		}
	}

	private static class DoubleConverter implements SimpleTypeConverter<Double> {
		@Override
		public String convertToSerializedForm(final Double object) {
			return object.toString();
		}

		@Override
		public Double convertToObject(final Class<Double> simpleType, final String serializedForm) {
			return Double.valueOf(Double.parseDouble(serializedForm));
		}
	}

	private static class BooleanConverter implements SimpleTypeConverter<Boolean> {
		@Override
		public String convertToSerializedForm(final Boolean object) {
			return object.toString();
		}

		@Override
		public Boolean convertToObject(final Class<Boolean> simpleType, final String serializedForm) {
			return Boolean.valueOf(Boolean.parseBoolean(serializedForm));
		}
	}

	private static class StringConverter implements SimpleTypeConverter<String> {
		@Override
		public String convertToSerializedForm(final String object) {
			return object;
		}

		@Override
		public String convertToObject(final Class<String> simpleType, final String serializedForm) {
			return serializedForm;
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static class EnumConverter implements SimpleTypeConverter<Enum> {
		@Override
		public Enum<?> convertToObject(final Class<Enum> simpleType, final String serializedForm) {
			return Enum.valueOf((Class) simpleType, serializedForm);
		}

		@Override
		public String convertToSerializedForm(final Enum object) {
			return object.name();
		}
	}

	private static class DateConverter implements SimpleTypeConverter<Date> {
		@Override
		public Date convertToObject(final Class<Date> simpleType, final String serializedForm) {
			return new Date(Long.parseLong(serializedForm));
		}

		@Override
		public String convertToSerializedForm(final Date object) {
			return String.valueOf(object.getTime());
		}
	}
}
