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

package org.jeconfig.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.jeconfig.api.ConfigUnsetter;
import org.jeconfig.client.annotation.array.ArrayTestConfiguration;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.ComplexMapTestConfiguration;
import org.jeconfig.client.annotation.map.MapPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.ComplexSetPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.SetPropertyTestConfiguration;
import org.jeconfig.client.annotation.simple.SimplePropertyTestConfiguration;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.junit.Test;

public class UnsetterTest extends AbstractConfigServiceTest {

	@Test
	public void testUnsetComplexProperty() {
		ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		Assert.assertFalse(configUnsetter.isPropertySet(config, "property")); //$NON-NLS-1$
		config.setProperty(ComplexSubtype.create(getConfigService(), "a", "b")); //$NON-NLS-1$//$NON-NLS-2$
		Assert.assertTrue(configUnsetter.isPropertySet(config, "property")); //$NON-NLS-1$
		getConfigService().save(config);

		config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		configUnsetter.unsetProperties(config, "property"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "property")); //$NON-NLS-1$
		Assert.assertNull(config.getProperty());

		getConfigService().save(config);
		final ComplexPropertyTestConfiguration config2 = getConfigService().load(ComplexPropertyTestConfiguration.class);
		Assert.assertFalse(configUnsetter.isPropertySet(config2, "property")); //$NON-NLS-1$
	}

	@Test
	public void testUnsetNotSupported() {
		final SimplePropertyTestConfiguration config = new SimplePropertyTestConfiguration();
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		Assert.assertFalse(configUnsetter.canUnsetConfig(config));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnsetAllNotSupportedThrowsEx() {
		final SimplePropertyTestConfiguration config = new SimplePropertyTestConfiguration();
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetAllProperties(config);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnsetPropertyNotSupportedThrowsEx() {
		final SimplePropertyTestConfiguration config = new SimplePropertyTestConfiguration();
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "asdf"); //$NON-NLS-1$
	}

	@Test
	public void testUnsetSimpleProperty() {
		SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setIntValue(3);
		getConfigService().save(config);

		config = getConfigService().load(SimplePropertyTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "intValue"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "intValue")); //$NON-NLS-1$
		Assert.assertEquals(new SimplePropertyTestConfiguration().getIntValue(), config.getIntValue());

		getConfigService().save(config);
		final SimplePropertyTestConfiguration config2 = getConfigService().load(SimplePropertyTestConfiguration.class);
		Assert.assertEquals(new SimplePropertyTestConfiguration().getIntValue(), config2.getIntValue());
	}

	@Test
	public void testDontSetCleanConfigDirtyOnUnset() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "intValue"); //$NON-NLS-1$
		getConfigService().save(config);
		Assert.assertEquals(0, getCountingDummyPersister().getSaveCount());
	}

	@Test
	public void testUnsetListProperty() {
		ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final List<String> list = getConfigService().createList();
		list.add("test"); //$NON-NLS-1$
		list.add("svsdfsf"); //$NON-NLS-1$
		config.setStringField(list);
		getConfigService().save(config);

		config = getConfigService().load(ListTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "stringField"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "stringField")); //$NON-NLS-1$
		Assert.assertEquals(new ListTestConfiguration().getStringField(), config.getStringField());

		getConfigService().save(config);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(new ListTestConfiguration().getStringField(), result.getStringField());
	}

	@Test
	public void testUnsetComplexListProperty() {
		ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final List<ComplexSubtype> list = getConfigService().createList(LinkedList.class);
		list.add(ComplexSubtype.create(getConfigService(), "2", "lukas")); //$NON-NLS-1$//$NON-NLS-2$
		list.add(ComplexSubtype.create(getConfigService(), "3", "test")); //$NON-NLS-1$//$NON-NLS-2$
		config.setComplex(list);
		getConfigService().save(config);

		config = getConfigService().load(ListTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "complex"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "complex")); //$NON-NLS-1$
		Assert.assertEquals(new ListTestConfiguration().getComplex(), config.getComplex());

		getConfigService().save(config);
		final ListTestConfiguration result = getConfigService().load(ListTestConfiguration.class);
		Assert.assertEquals(new ListTestConfiguration().getComplex(), result.getComplex());
	}

	@Test
	public void testUnsetMapProperty() {
		MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> map = getConfigService().createMap();
		map.put("lukas", Integer.valueOf(5)); //$NON-NLS-1$
		config.setUserID(map);
		getConfigService().save(config);

		config = getConfigService().load(MapPropertyTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "userID"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "userID")); //$NON-NLS-1$
		Assert.assertEquals(new MapPropertyTestConfiguration().getUserID(), config.getUserID());

		getConfigService().save(config);
		final MapPropertyTestConfiguration result = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(new MapPropertyTestConfiguration().getUserID(), result.getUserID());
	}

	@Test
	public void testUnsetComplexMapProperty() {
		ComplexMapTestConfiguration config = getConfigService().load(ComplexMapTestConfiguration.class);
		final Map<String, ComplexSubtype> map = getConfigService().createMap();
		map.put("test", ComplexSubtype.create(getConfigService(), "4", "fsdxcv")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		config.setProperty(map);
		getConfigService().save(config);

		config = getConfigService().load(ComplexMapTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "property"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "property")); //$NON-NLS-1$
		Assert.assertEquals(new ComplexMapTestConfiguration().getProperty(), config.getProperty());

		getConfigService().save(config);
		final ComplexMapTestConfiguration result = getConfigService().load(ComplexMapTestConfiguration.class);
		Assert.assertEquals(new ComplexMapTestConfiguration().getProperty(), result.getProperty());
	}

	@Test
	public void testUnsetSetProperty() {
		SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> set = getConfigService().createSet();
		set.add("testxv123"); //$NON-NLS-1$
		set.add("xvcvxcv"); //$NON-NLS-1$
		config.setData(set);
		getConfigService().save(config);

		config = getConfigService().load(SetPropertyTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "data"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "data")); //$NON-NLS-1$
		Assert.assertEquals(new SetPropertyTestConfiguration().getData(), config.getData());

		getConfigService().save(config);
		final SetPropertyTestConfiguration result = getConfigService().load(SetPropertyTestConfiguration.class);
		Assert.assertEquals(new SetPropertyTestConfiguration().getData(), result.getData());
	}

	@Test
	public void testUnsetComplexSetProperty() {
		ComplexSetPropertyTestConfiguration config = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		final Set<ComplexSubtype> set = getConfigService().createSet();
		set.add(ComplexSubtype.create(getConfigService(), "5", "fsv")); //$NON-NLS-1$//$NON-NLS-2$
		config.setSubTypeSet(set);

		config = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "subTypeSet"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "subTypeSet")); //$NON-NLS-1$
		Assert.assertEquals(new ComplexSetPropertyTestConfiguration().getSubTypeSet(), config.getSubTypeSet());

		getConfigService().save(config);
		final ComplexSetPropertyTestConfiguration result = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		Assert.assertEquals(new ComplexSetPropertyTestConfiguration().getSubTypeSet(), result.getSubTypeSet());
	}

	@Test
	public void testUnsetArrayProperty() {
		ArrayTestConfiguration config = getConfigService().load(ArrayTestConfiguration.class);
		final int[] intValues = new int[] {3, 4, 5, 6, 12};
		config.setIntField(intValues);
		getConfigService().save(config);

		config = getConfigService().load(ArrayTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "intField"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "intField")); //$NON-NLS-1$
		Assert.assertEquals(new ArrayTestConfiguration().getIntField(), config.getIntField());

		getConfigService().save(config);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertEquals(new ArrayTestConfiguration().getIntField(), result.getIntField());
	}

	@Test
	public void testUnsetComplexArrayProperty() {
		ArrayTestConfiguration config = getConfigService().load(ArrayTestConfiguration.class);
		final ComplexSubtype[] complexValues = new ComplexSubtype[] {ComplexSubtype.create(getConfigService(), "7", "ysxdcfxc")}; //$NON-NLS-1$//$NON-NLS-2$
		config.setComplex(complexValues);
		getConfigService().save(config);

		config = getConfigService().load(ArrayTestConfiguration.class);
		final ConfigUnsetter configUnsetter = getConfigService().getConfigUnsetter();
		configUnsetter.unsetProperties(config, "complex"); //$NON-NLS-1$

		Assert.assertFalse(configUnsetter.isPropertySet(config, "complex")); //$NON-NLS-1$
		Assert.assertEquals(new ArrayTestConfiguration().getComplex(), config.getComplex());

		getConfigService().save(config);
		final ArrayTestConfiguration result = getConfigService().load(ArrayTestConfiguration.class);
		Assert.assertEquals(new ArrayTestConfiguration().getComplex(), result.getComplex());
	}
}
