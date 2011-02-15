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

package org.jeconfig.client.annotation.array;

import java.util.Arrays;

import junit.framework.Assert;

import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.list.BaseClassFalseBaseClassTestConfig;
import org.jeconfig.client.annotation.list.SubClassFalseBaseClassTestConfig;
import org.jeconfig.client.testconfigs.BaseClass;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.IMyInferfaceForNonPolymorphTest;
import org.jeconfig.client.testconfigs.IMyInterface;
import org.jeconfig.client.testconfigs.SubClass1;
import org.jeconfig.client.testconfigs.SubClass2;
import org.junit.Test;

@SuppressWarnings("nls")
public class ArrayPropertyTest extends AbstractConfigServiceTest {

	@Test
	public void testCustomConverterOnLoadAndSave() {
		final ArrayCustomConverterTestConfig config = getConfigService().load(ArrayCustomConverterTestConfig.class);
		config.setArray(new String[] {"asdf"});
		getConfigService().save(config);
		final ArrayCustomConverterTestConfig config2 = getConfigService().load(ArrayCustomConverterTestConfig.class);
		Assert.assertEquals(config.getArray()[0] + "12", config2.getArray()[0]);
	}

	@Test
	public void testPrimitiveField() {
		final ArrayTestConfiguration configuration = getConfigService().load(ArrayTestConfiguration.class);
		configuration.setIntField(new int[] {1, 2, 3});
		getConfigService().save(configuration);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(configuration.getIntField(), result.getIntField()));
	}

	@Test
	public void testNullPrimitiveField() {
		final ArrayTestConfiguration configuration = getConfigService().load(ArrayTestConfiguration.class);
		configuration.setIntField(null);
		getConfigService().save(configuration);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(configuration.getIntField(), result.getIntField()));
	}

	@Test
	public void testEmptyPrimitiveField() {
		final ArrayTestConfiguration configuration = getConfigService().load(ArrayTestConfiguration.class);
		configuration.setIntField(new int[] {});
		getConfigService().save(configuration);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(configuration.getIntField(), result.getIntField()));
		Assert.assertTrue(result.getIntField().length == 0);
	}

	@Test
	public void testMergeUseChildPrimitiveField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(ArrayTestConfiguration.class);

		final ArrayTestConfiguration globalConfig = getConfigService().load(
				ArrayTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		globalConfig.setIntField(new int[] {1, 2, 4, 5});
		getConfigService().save(globalConfig);

		final ArrayTestConfiguration userConfig = getConfigService().load(ArrayTestConfiguration.class);
		userConfig.setIntField(new int[] {5, 2, 3});

		getConfigService().save(userConfig);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(userConfig.getIntField(), result.getIntField()));
	}

	@Test
	public void testMergeUseParentSimpleField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(ArrayTestConfiguration.class);

		final ArrayTestConfiguration globalConfig = getConfigService().load(
				ArrayTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		globalConfig.setStringField(new String[] {"3333", "343", "dfg"});
		getConfigService().save(globalConfig);

		final ArrayTestConfiguration userConfig = getConfigService().load(ArrayTestConfiguration.class);
		userConfig.setStringField(new String[] {"adf", "fdas"});
		getConfigService().save(userConfig);

		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(globalConfig.getStringField(), result.getStringField()));
	}

	@Test
	public void testMergeUseChildComplexTypeField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(ArrayTestConfiguration.class);

		final ArrayTestConfiguration globalConfig = getConfigService().load(
				ArrayTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());

		globalConfig.setComplex(new ComplexSubtype[] {
				ComplexSubtype.create(getConfigService(), "1", "global2"),
				ComplexSubtype.create(getConfigService(), "2", "global2"),
				ComplexSubtype.create(getConfigService(), "3", "global3")});
		getConfigService().save(globalConfig);

		final ArrayTestConfiguration userConfig = getConfigService().load(ArrayTestConfiguration.class);
		userConfig.setComplex(new ComplexSubtype[] {
				ComplexSubtype.create(getConfigService(), "1", "user1"), ComplexSubtype.create(getConfigService(), "2", "user2")});

		getConfigService().save(userConfig);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(userConfig.getComplex(), result.getComplex()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPolymorphPrimitive() {
		final ArrayTestConfigurationPolymorphPrimitive config = getConfigService().load(
				ArrayTestConfigurationPolymorphPrimitive.class);
		getConfigService().save(config);
	}

	@Test
	public void testPolymorphTrueBaseClassField() {
		final ArrayTestConfiguration testConfig = getConfigService().load(ArrayTestConfiguration.class);
		testConfig.setSubClass(new BaseClass[] {
				SubClass1.create(getConfigService(), "data1", 0), SubClass2.create(getConfigService(), "data2", 0.0f)});
		getConfigService().save(testConfig);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(testConfig.getSubClass(), result.getSubClass()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPolymorphFalseBaseClassField() {
		final ArrayTestConfigurationFalseBaseClass testConfig = getConfigService().load(
				ArrayTestConfigurationFalseBaseClass.class);
		testConfig.setSubClass2(new BaseClassFalseBaseClassTestConfig[] {
				SubClassFalseBaseClassTestConfig.create(getConfigService(), 0),
				SubClassFalseBaseClassTestConfig.create(getConfigService(), 0)});
		getConfigService().save(testConfig);
	}

	@Test
	public void testPolymorphTrueIMyInterfaceField() {
		final ArrayTestConfiguration testConfig = getConfigService().load(ArrayTestConfiguration.class);
		testConfig.setImplClass(new IMyInterface[] {
				SubClass1.create(getConfigService(), "data1", 0), SubClass2.create(getConfigService(), "data2", 0.0f)});
		getConfigService().save(testConfig);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(testConfig.getImplClass(), result.getImplClass()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPolymorphFalseIMyInterfaceField() {
		final ArrayTestConfigurationNonPolymorph testConfig = getConfigService().load(ArrayTestConfigurationNonPolymorph.class);
		testConfig.setImplClass2(new IMyInferfaceForNonPolymorphTest[] {
				SubClass1.create(getConfigService(), "data1", 0), SubClass2.create(getConfigService(), "data2", 0.0f)});
		getConfigService().save(testConfig);
	}

	@Test
	public void testNullElementPrimitiveType() {
		final ArrayTestConfiguration configuration = getConfigService().load(ArrayTestConfiguration.class);
		configuration.setStringField2((new String[] {null}));
		getConfigService().save(configuration);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertTrue(Arrays.equals(configuration.getStringField2(), result.getStringField2()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingSetter() {
		final ArrayTestConfigurationMissingSetter config = getConfigService().load(ArrayTestConfigurationMissingSetter.class);
		getConfigService().save(config);
	}
}
