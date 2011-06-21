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

package org.jeconfig.client.proxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.array.ArrayTestConfiguration;
import org.jeconfig.client.annotation.array.ConstructorInitializedArrayTestConfiguration;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.list.ConstructorInitializedListTestConfiguration;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.ConstructorInitializedMapTestConfiguration;
import org.jeconfig.client.annotation.map.MapPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.ConstructorInitializedSetTestConfiguration;
import org.jeconfig.client.annotation.set.SetPropertyTestConfiguration;
import org.jeconfig.client.annotation.simple.SimplePropertyTestConfiguration;
import org.jeconfig.client.proxy.ConfigDirtyStateListener;
import org.jeconfig.client.proxy.ConfigProxy;
import org.jeconfig.client.proxy.RootConfigProxy;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigProxyTest extends AbstractConfigServiceTest {

	private boolean dirtyStateListenerInformed = false;
	private final ConfigDirtyStateListener dirtyStateListener = new ConfigDirtyStateListener() {
		@Override
		public void dirtyStateChanged(final RootConfigProxy configProxy) {
			dirtyStateListenerInformed = true;
		}
	};

	@Override
	@Before
	public void setUp() {
		super.setUp();
		dirtyStateListenerInformed = false;
	}

	/* internal utility method */
	private <T> void testConstructorInitialization(final Class<T> configClass) {
		final T config = getConfigService().load(configClass);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);

		Assert.assertFalse(proxy.isDirty());
		Assert.assertFalse(proxy.hasDiff());
		Assert.assertFalse(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@Test
	public void testSimplePropertyModification() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);

		config.setStringValue("leet");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains("stringValue"));
	}

	@Test
	public void testComplexPropertyModification() {
		final ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);

		config.setProperty(ComplexSubtype.create(getConfigService(), "id", "name"));
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains("property"));
	}

	@Test
	public void testSetConstructorInitialization() {
		testConstructorInitialization(ConstructorInitializedSetTestConfiguration.class);
	}

	@Test
	public void testSetExchange() {
		final SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final Set<String> set = getConfigService().createSet();
		config.setData(set);
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains("data"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetAddition() {
		final SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigSetDTO> setProxy = (ConfigProxy<ConfigSetDTO>) config.getData();

		config.getData().add("test");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(setProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetRemoval() {
		final ConstructorInitializedSetTestConfiguration config = getConfigService().load(
				ConstructorInitializedSetTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigSetDTO> setProxy = (ConfigProxy<ConfigSetDTO>) config.getData();

		Assert.assertFalse(proxy.isDirty());
		Assert.assertFalse(proxy.hasDiff());
		Assert.assertFalse(setProxy.hasDiff());

		config.getData().remove("item");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(setProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@Test
	public void testArrayConstructorInitialization() {
		testConstructorInitialization(ConstructorInitializedArrayTestConfiguration.class);
	}

	@Test
	public void testArrayExchange() {
		final ArrayTestConfiguration config = getConfigService().load(ArrayTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);

		config.setIntField(new int[] {1, 2, 3});
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains("intField"));
	}

	@Test
	public void testArrayModification() {
		final ConstructorInitializedArrayTestConfiguration config = getConfigService().load(
				ConstructorInitializedArrayTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);

		config.getIntField()[0] = 2;

		// modifications of array elements can't be tracked - it is not allowed!
		Assert.assertFalse(proxy.isDirty());
		Assert.assertFalse(proxy.hasDiff());
		Assert.assertFalse(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@Test
	public void testListConstructorInitialization() {
		testConstructorInitialization(ConstructorInitializedListTestConfiguration.class);
	}

	@Test
	public void testListExchange() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final List<String> list = getConfigService().createList();
		config.setStringField(list);
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains("stringField"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListAddition() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigListDTO> listProxy = (ConfigProxy<ConfigListDTO>) config.getStringField();

		config.getStringField().add("test");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(listProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListRemoval() {
		final ConstructorInitializedListTestConfiguration config = getConfigService().load(
				ConstructorInitializedListTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigListDTO> listProxy = (ConfigProxy<ConfigListDTO>) config.getStringList();

		Assert.assertFalse(proxy.isDirty());
		Assert.assertFalse(proxy.hasDiff());
		Assert.assertFalse(listProxy.hasDiff());

		config.getStringList().remove("item");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(listProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@Test
	public void testMapConstructorInitialization() {
		testConstructorInitialization(ConstructorInitializedMapTestConfiguration.class);
	}

	@Test
	public void testMapExchange() {
		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final Map<String, Integer> map = getConfigService().createMap();
		config.setUserID(map);
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains("userID"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMapAddition() {
		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigMapDTO> mapProxy = (ConfigProxy<ConfigMapDTO>) config.getUserID();

		config.getUserID().put("key", Integer.valueOf(1));
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(mapProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMapRemoval() {
		final ConstructorInitializedMapTestConfiguration config = getConfigService().load(
				ConstructorInitializedMapTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigMapDTO> mapProxy = (ConfigProxy<ConfigMapDTO>) config.getMapProperty();

		Assert.assertFalse(proxy.isDirty());
		Assert.assertFalse(proxy.hasDiff());
		Assert.assertFalse(mapProxy.hasDiff());

		config.getMapProperty().remove("key");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(mapProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMapValueExchange() {
		final ConstructorInitializedMapTestConfiguration config = getConfigService().load(
				ConstructorInitializedMapTestConfiguration.class);
		final RootConfigProxy proxy = (RootConfigProxy) config;
		proxy.addDirtyStateListener(dirtyStateListener);
		final ConfigProxy<ConfigMapDTO> mapProxy = (ConfigProxy<ConfigMapDTO>) config.getMapProperty();

		Assert.assertFalse(proxy.isDirty());
		Assert.assertFalse(proxy.hasDiff());
		Assert.assertFalse(mapProxy.hasDiff());

		config.getMapProperty().put("key", "value2");
		Assert.assertTrue(proxy.isDirty());
		Assert.assertTrue(proxy.hasDiff());
		Assert.assertTrue(mapProxy.hasDiff());
		Assert.assertTrue(dirtyStateListenerInformed);
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
	}

}
