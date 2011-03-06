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

package org.jeconfig.client.annotation.list;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.simple.TestConfigEnum;
import org.jeconfig.client.testconfigs.BaseClass;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.IMyInferfaceForNonPolymorphTest;
import org.jeconfig.client.testconfigs.IMyInterface;
import org.jeconfig.client.testconfigs.SubClass1;
import org.jeconfig.client.testconfigs.SubClass2;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigListPropertyTest extends AbstractConfigServiceTest {

	@Test
	public void testCustomConverterOnLoadAndSave() {
		final CustomConverterListTestConfig config = getConfigService().load(CustomConverterListTestConfig.class);
		final List<String> list = getConfigService().createList();
		list.add("asdf");
		config.setList(list);
		getConfigService().save(config);
		final CustomConverterListTestConfig config2 = getConfigService().load(CustomConverterListTestConfig.class);
		Assert.assertEquals(config.getList().get(0) + "12", config2.getList().get(0));
	}

	@Test
	public void testEnumField() {
		EnumListTextConfiguration config = getConfigService().load(EnumListTextConfiguration.class);
		config.getList().add(TestConfigEnum.ERROR);
		config.getList().add(TestConfigEnum.FAIL);
		config.getList().add(TestConfigEnum.SUCCESS);
		getConfigService().save(config);

		config = getConfigService().load(EnumListTextConfiguration.class);
		Assert.assertEquals(3, config.getList().size());
		Assert.assertEquals(TestConfigEnum.ERROR, config.getList().get(0));
		Assert.assertEquals(TestConfigEnum.FAIL, config.getList().get(1));
		Assert.assertEquals(TestConfigEnum.SUCCESS, config.getList().get(2));
	}

	@Test
	public void testPrimitiveField() {
		final ListTestConfiguration configuration = getConfigService().load(ListTestConfiguration.class);
		final Integer[] array = new Integer[] {Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)};
		final List<Integer> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		configuration.setIntField(list);
		getConfigService().save(configuration);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(configuration.getIntField(), result.getIntField());
	}

	@Test
	public void testNullPrimitiveField() {
		final ListTestConfiguration configuration = getConfigService().load(ListTestConfiguration.class);
		configuration.setIntField(null);
		getConfigService().save(configuration);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(configuration.getIntField(), result.getIntField());
	}

	@Test
	public void testEmptyPrimitiveField() {
		final ListTestConfiguration configuration = getConfigService().load(ListTestConfiguration.class);
		final List<Integer> list = getConfigService().createList();
		configuration.setIntField(list);
		getConfigService().save(configuration);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(configuration.getIntField(), result.getIntField());
		Assert.assertTrue(result.getIntField().size() == 0);
	}

	@Test
	public void testMergeUseChildPrimitiveField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(ListTestConfiguration.class);

		final ListTestConfiguration globalConfig = getConfigService().load(
				ListTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Integer[] array2 = new Integer[] {Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(4), Integer.valueOf(5)};
		final List<Integer> list2 = getConfigService().createList();
		list2.addAll(Arrays.asList(array2));
		globalConfig.setIntField(list2);
		getConfigService().save(globalConfig);

		final ListTestConfiguration userConfig = getConfigService().load(ListTestConfiguration.class);
		final Integer[] array = new Integer[] {Integer.valueOf(5), Integer.valueOf(2), Integer.valueOf(3)};
		final List<Integer> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		userConfig.setIntField(list);
		getConfigService().save(userConfig);

		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(userConfig.getIntField(), result.getIntField());
	}

	@Test
	public void testMergeUseParentSimpleField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(ListTestConfiguration.class);

		final ListTestConfiguration globalConfig = getConfigService().load(
				ListTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final String[] array2 = new String[] {"3333", "343", "dfg"};
		final List<String> list = getConfigService().createList();
		list.addAll(Arrays.asList(array2));
		globalConfig.setStringField(list);
		getConfigService().save(globalConfig);

		final ListTestConfiguration userConfig = getConfigService().load(ListTestConfiguration.class);
		final String[] array = new String[] {"adf", "fdas"};
		final List<String> list2 = getConfigService().createList();
		list2.addAll(Arrays.asList(array));
		userConfig.setStringField(list2);
		getConfigService().save(userConfig);

		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(globalConfig.getStringField(), result.getStringField());
	}

	@Test
	public void testMergeUseChildComplexTypeField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(ListTestConfiguration.class);

		final ListTestConfiguration globalConfig = getConfigService().load(
				ListTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final ComplexSubtype[] array2 = new ComplexSubtype[] {
				ComplexSubtype.create(getConfigService(), "1", "global1"),
				ComplexSubtype.create(getConfigService(), "2", "global2"),
				ComplexSubtype.create(getConfigService(), "3", "global3")};
		final List<ComplexSubtype> list = getConfigService().createList();
		list.addAll(Arrays.asList(array2));
		globalConfig.setComplex(list);
		getConfigService().save(globalConfig);

		final ListTestConfiguration userConfig = getConfigService().load(ListTestConfiguration.class);
		final ComplexSubtype[] array = new ComplexSubtype[] {
				ComplexSubtype.create(getConfigService(), "1", "user1"), ComplexSubtype.create(getConfigService(), "2", "user2")};
		final List<ComplexSubtype> list2 = getConfigService().createList();
		list2.addAll(Arrays.asList(array));
		userConfig.setComplex(list2);
		getConfigService().save(userConfig);

		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(userConfig.getComplex(), result.getComplex());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongItemType() {
		final ListTestConfigurationWrongItemType config = getConfigService().load(ListTestConfigurationWrongItemType.class);
		final Integer[] array = new Integer[] {Integer.valueOf(1)};
		final List<Integer> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		config.setIntField(list);
		// wrong item type can only be detected when the list contains a value
		getConfigService().save(config);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPolymorphPrimitive() {
		final ConfigListTestConfigurationPolymorphPrimitive config = getConfigService().load(
				ConfigListTestConfigurationPolymorphPrimitive.class);
		getConfigService().save(config);
	}

	@Test
	public void testPolymorphTrueBaseClassField() {
		final ListTestConfiguration testConfig = getConfigService().load(ListTestConfiguration.class);
		final BaseClass[] array = new BaseClass[] {
				SubClass1.create(getConfigService(), "data1", 0), SubClass2.create(getConfigService(), "data2", 0.0f)};
		final List<BaseClass> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		testConfig.setSubClass(list);
		getConfigService().save(testConfig);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(testConfig.getSubClass(), result.getSubClass());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPolymorphFalseBaseClassField() {
		final ListTestConfigurationFalseBaseClass testConfig = getConfigService().load(ListTestConfigurationFalseBaseClass.class);
		final BaseClassFalseBaseClassTestConfig[] array = new BaseClassFalseBaseClassTestConfig[] {
				SubClassFalseBaseClassTestConfig.create(getConfigService(), 0),
				SubClassFalseBaseClassTestConfig.create(getConfigService(), 0)};
		final List<BaseClassFalseBaseClassTestConfig> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		testConfig.setSubClass2(list);
		getConfigService().save(testConfig);
	}

	@Test
	public void testPolymorphTrueIMyInterfaceField() {
		final ListTestConfiguration testConfig = getConfigService().load(ListTestConfiguration.class);
		final IMyInterface[] array = new IMyInterface[] {
				SubClass1.create(getConfigService(), "data1", 0), SubClass2.create(getConfigService(), "data2", 0.0f)};
		final List<IMyInterface> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		testConfig.setImplClass(list);
		getConfigService().save(testConfig);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(testConfig.getImplClass(), result.getImplClass());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPolymorphFalseIMyInterfaceField() {
		final ListTestConfigurationNonPolymorph testConfig = getConfigService().load(ListTestConfigurationNonPolymorph.class);
		final IMyInferfaceForNonPolymorphTest[] array = new IMyInferfaceForNonPolymorphTest[] {
				SubClass1.create(getConfigService(), "data1", 0), SubClass2.create(getConfigService(), "data2", 0.0f)};
		final List<IMyInferfaceForNonPolymorphTest> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		testConfig.setImplClass2(list);
		getConfigService().save(testConfig);
	}

	@Test
	public void testNullElementPrimitiveType() {
		final ListTestConfiguration configuration = getConfigService().load(ListTestConfiguration.class);
		final Integer[] array = new Integer[] {null};
		final List<Integer> list = getConfigService().createList();
		list.addAll(Arrays.asList(array));
		configuration.setIntField(list);
		getConfigService().save(configuration);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(configuration, result);
	}
}
