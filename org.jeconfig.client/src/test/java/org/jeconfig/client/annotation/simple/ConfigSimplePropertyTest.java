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

import java.util.Calendar;

import junit.framework.Assert;

import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.configclass.TestConfiguration;
import org.junit.Test;

public class ConfigSimplePropertyTest extends AbstractConfigServiceTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testCustomSimplePropertyConverterUsed() {
		final CustomConverterConfig config = getConfigService().load(CustomConverterConfig.class);
		config.setProperty("aasdf"); //$NON-NLS-1$
		getConfigService().save(config);
	}

	@Test
	public void testCustomSimplePropertyConverterUsedOnSaveAndLoad() {
		final CustomConverterConfig2 config = getConfigService().load(CustomConverterConfig2.class);
		config.setProperty("aasdf"); //$NON-NLS-1$
		getConfigService().save(config);
		final CustomConverterConfig2 config2 = getConfigService().load(CustomConverterConfig2.class);
		Assert.assertEquals(config.getProperty() + "12", config2.getProperty()); //$NON-NLS-1$
	}

	@Test
	public void testSaveAndLoad() {
		final TestConfiguration config = getConfigService().load(TestConfiguration.class);
		config.setField1("test"); //$NON-NLS-1$
		config.setField2(Integer.valueOf(56));
		getConfigService().save(config);
		final TestConfiguration loadedConfig = getConfigService().load(TestConfiguration.class);
		Assert.assertEquals(config.getField1(), loadedConfig.getField1());
		Assert.assertEquals(config.getField2(), loadedConfig.getField2());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnsupportedType() {
		getConfigService().load(UnsupportedTypeConfigSimplePropertyTestConfiguration.class);
	}

	@Test
	public void testSaveAndLoadDate() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		final Calendar date = Calendar.getInstance();
		date.set(2010, 07, 20, 13, 57, 34);
		config.setDateValue(date.getTime());
		getConfigService().save(config);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getDateValue(), result.getDateValue());
	}

	@Test
	public void testUsingChildMergeInt() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SimplePropertyTestConfiguration.class);
		final IScopePath parentScopePath = scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final SimplePropertyTestConfiguration parentConfig = getConfigService().load(
				SimplePropertyTestConfiguration.class,
				parentScopePath);
		parentConfig.setIntValue(4);
		getConfigService().save(parentConfig);

		final SimplePropertyTestConfiguration childConfig = getConfigService().load(SimplePropertyTestConfiguration.class);
		childConfig.setIntValue(7);
		getConfigService().save(childConfig);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertTrue(result.getIntValue() == 7);
	}

	@Test
	public void testUsingChildMergeBigInt() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SimplePropertyTestConfiguration.class);
		final IScopePath parentScopePath = scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final SimplePropertyTestConfiguration parentConfig = getConfigService().load(
				SimplePropertyTestConfiguration.class,
				parentScopePath);
		parentConfig.setBigIntValue(Integer.valueOf(4));
		getConfigService().save(parentConfig);

		final SimplePropertyTestConfiguration childConfig = getConfigService().load(SimplePropertyTestConfiguration.class);
		childConfig.setBigIntValue(Integer.valueOf(7));
		getConfigService().save(childConfig);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertTrue(result.getBigIntValue().intValue() == 7);
	}

	@Test
	public void testSaveAndLoadInt() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setIntValue(5);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getIntValue(), result.getIntValue());
	}

	@Test
	public void testSaveAndLoadDouble() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setDoubleValue(3);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(Double.valueOf(config.getDoubleValue()), Double.valueOf(result.getDoubleValue()));
	}

	@Test
	public void testSaveAndLoadLong() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setLongValue(7);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(Long.valueOf(config.getLongValue()), Long.valueOf(result.getLongValue()));
	}

	@Test
	public void testSaveAndLoadShort() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setShortValue((short) 4);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(Short.valueOf(config.getShortValue()), Short.valueOf(result.getShortValue()));
	}

	@Test
	public void testSaveAndLoadByte() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setByteValue((byte) 4);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(Byte.valueOf(config.getByteValue()), Byte.valueOf(result.getByteValue()));
	}

	@Test
	public void testSaveAndLoadString() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setStringValue("test"); //$NON-NLS-1$
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getStringValue(), result.getStringValue());
	}

	@Test
	public void testSaveAndLoadFloat() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setFloatValue(0.97f);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(Float.valueOf(config.getFloatValue()), Float.valueOf(result.getFloatValue()));
	}

	@Test
	public void testSaveAndLoadBoolean() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBoolValue(true);
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(Boolean.valueOf(config.isBoolValue()), Boolean.valueOf(result.isBoolValue()));
	}

	@Test
	public void testSaveAndLoadBigInt() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigIntValue(Integer.valueOf(5));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigIntValue(), result.getBigIntValue());
	}

	@Test
	public void testSaveAndLoadBigDouble() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigDoubleValue(Double.valueOf(8));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigDoubleValue(), result.getBigDoubleValue());
	}

	@Test
	public void testSaveAndLoadBigLong() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigLongValue(Long.valueOf(5));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigLongValue(), result.getBigLongValue());
	}

	@Test
	public void testSaveAndLoadBigShort() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigShortValue(Short.valueOf((short) 9));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigShortValue(), result.getBigShortValue());
	}

	@Test
	public void testSaveAndLoadBigByte() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigByteValue(Byte.valueOf((byte) 14));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigByteValue(), result.getBigByteValue());
	}

	@Test
	public void testSaveAndLoadBigFloat() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigFloatValue(Float.valueOf(0.99f));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigFloatValue(), result.getBigFloatValue());
	}

	@Test
	public void testSaveAndLoadBigBoolean() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setBigBoolValue(Boolean.valueOf(true));
		getConfigService().save(config);
		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getBigBoolValue(), result.getBigBoolValue());
	}

	@Test
	public void testSaveAndLoadEnum() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setEnumValue(TestConfigEnum.FAIL);
		getConfigService().save(config);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(config.getEnumValue(), result.getEnumValue());
	}

	@Test
	public void testMergeUseParentIntField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SimplePropertyTestConfiguration.class);
		final IScopePath parentScopePath = scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final SimplePropertyTestConfiguration parentConfig = getConfigService().load(
				SimplePropertyTestConfiguration.class,
				parentScopePath);
		parentConfig.setSomeIntValue1(4);
		getConfigService().save(parentConfig);

		final SimplePropertyTestConfiguration childConfig = getConfigService().load(SimplePropertyTestConfiguration.class);
		childConfig.setSomeIntValue1(7);
		getConfigService().save(childConfig);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertTrue(result.getSomeIntValue1() == 4);
	}

	@Test
	public void testMergeUseMyMergingStrategyIntField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SimplePropertyTestConfiguration.class);
		final IScopePath parentScopePath = scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final SimplePropertyTestConfiguration parentConfig = getConfigService().load(
				SimplePropertyTestConfiguration.class,
				parentScopePath);
		parentConfig.setSomeIntValue2(8);
		getConfigService().save(parentConfig);

		final SimplePropertyTestConfiguration childConfig = getConfigService().load(SimplePropertyTestConfiguration.class);
		childConfig.setSomeIntValue2(3);
		getConfigService().save(childConfig);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertTrue(result.getSomeIntValue2() == 24);
	}

	@Test
	public void testMergeUseParentBigIntField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SimplePropertyTestConfiguration.class);
		final IScopePath parentScopePath = scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final SimplePropertyTestConfiguration parentConfig = getConfigService().load(
				SimplePropertyTestConfiguration.class,
				parentScopePath);
		parentConfig.setSomeBigIntValue1(Integer.valueOf(4));
		getConfigService().save(parentConfig);

		final SimplePropertyTestConfiguration childConfig = getConfigService().load(SimplePropertyTestConfiguration.class);
		childConfig.setSomeBigIntValue1(Integer.valueOf(7));
		getConfigService().save(childConfig);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		if (result.getSomeBigIntValue1() != null) {
			Assert.assertTrue(result.getSomeBigIntValue1().intValue() == 4);
		} else {
			Assert.fail();
		}
	}

	@Test
	public void testMergeUseMyMergingStrategyBigIntField() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SimplePropertyTestConfiguration.class);
		final IScopePath parentScopePath = scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final SimplePropertyTestConfiguration parentConfig = getConfigService().load(
				SimplePropertyTestConfiguration.class,
				parentScopePath);
		parentConfig.setSomeBigIntValue2(Integer.valueOf(8));
		getConfigService().save(parentConfig);

		final SimplePropertyTestConfiguration childConfig = getConfigService().load(SimplePropertyTestConfiguration.class);
		childConfig.setSomeBigIntValue2(Integer.valueOf(3));
		getConfigService().save(childConfig);

		final SimplePropertyTestConfiguration result = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertTrue(result.getSomeBigIntValue2().intValue() == 24);
	}

}
