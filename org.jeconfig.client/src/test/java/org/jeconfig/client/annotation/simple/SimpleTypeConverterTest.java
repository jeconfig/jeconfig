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

package org.jeconfig.client.annotation.simple;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.persister.ConfigPersister;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.junit.Test;

public class SimpleTypeConverterTest extends AbstractConfigServiceTest {

	@Test
	public void testOverwriteofDateTypeConverter() {
		final ConfigPersister persister = getPersister();
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				OverwriteDateTypeConverterTestConfiguration.class);
		final OverwriteDateTypeConverterTestConfiguration config = getConfigService().load(
				OverwriteDateTypeConverterTestConfiguration.class);
		final SimpleDateConverter converter = new SimpleDateConverter();

		getConfigSetupService().getSimpleTypeConverterRegistry().addConverter(Date.class, converter);
		config.setDate(new Date(Long.parseLong("1234235234"))); //$NON-NLS-1$
		getConfigService().save(config);

		final ComplexConfigDTO configDTO = persister.loadConfiguration(factory.annotatedPath().create());
		final ConfigSimpleValueDTO valueDTO = configDTO.getSimpleValueProperty("date"); //$NON-NLS-1$
		final String value = valueDTO.getValue();
		Assert.assertTrue(value.equals("01/15/1970")); //$NON-NLS-1$
		Assert.assertTrue(converter.isHasConvertedToSerializedForm());

		final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); //$NON-NLS-1$
		final OverwriteDateTypeConverterTestConfiguration result = getConfigService().load(
				OverwriteDateTypeConverterTestConfiguration.class);
		Assert.assertEquals(format.format(config.getDate()), format.format(result.getDate()));
		Assert.assertTrue(converter.isHasConvertedToObject());
	}

	@Test
	public void testConvertNewSimpleType() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				NewSimpleTypeTestConfiguration.class);
		final ConfigPersister persister = getPersister();
		final NewSimpleTypeConverter converter = new NewSimpleTypeConverter();
		getConfigSetupService().getSimpleTypeConverterRegistry().addConverter(NewSimpleType.class, converter);
		final NewSimpleTypeTestConfiguration config = getConfigService().load(NewSimpleTypeTestConfiguration.class);

		final NewSimpleType type = new NewSimpleType();
		type.setName("test"); //$NON-NLS-1$
		config.setSimpleType(type);
		getConfigService().save(config);

		final ComplexConfigDTO configDTO = persister.loadConfiguration(factory.annotatedPath().create());
		final ConfigSimpleValueDTO valueDTO = configDTO.getSimpleValueProperty("simpleType"); //$NON-NLS-1$
		final String value = valueDTO.getValue();
		Assert.assertTrue(value.equals("test")); //$NON-NLS-1$
		Assert.assertTrue(converter.isHasConvertedToSerializedForm());

		final NewSimpleTypeTestConfiguration result = getConfigService().load(NewSimpleTypeTestConfiguration.class);
		Assert.assertEquals(config.getSimpleType().getName(), result.getSimpleType().getName());
		Assert.assertTrue(converter.isHasConvertedToObject());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoConverterForNewType() {
		final NewSimpleTypeTestConfiguration config = getConfigService().load(NewSimpleTypeTestConfiguration.class);
		config.setSimpleType(new NewSimpleType());
		getConfigService().save(config);
	}
}
