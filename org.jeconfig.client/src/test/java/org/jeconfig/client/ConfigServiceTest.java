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

import org.jeconfig.api.IScopePathListener;
import org.jeconfig.api.exception.InvalidConfigException;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.configclass.TestConfiguration;
import org.jeconfig.client.annotation.configclass.TestConfigurationDefaultFactory;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.ComplexMapTestConfiguration;
import org.jeconfig.client.annotation.map.MapPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.ComplexSetPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.SetPropertyTestConfiguration;
import org.jeconfig.client.testconfigs.BeanValidationTestConfig;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.FinalClassTestConfiguration;
import org.jeconfig.client.testconfigs.MissingSetterTestConfiguration;
import org.jeconfig.client.testconfigs.ProxyTestConfiguration;
import org.jeconfig.client.testconfigs.UpdateTestConfiguration;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigServiceTest extends AbstractConfigServiceTest {

	private static class TestScopePathListener implements IScopePathListener {
		private int saveCount = 0;
		private int refreshCount = 0;

		@Override
		public void configSaved(final IScopePath scopePath) {
			saveCount++;
		}

		@Override
		public void configRefreshed(final IScopePath scopePath) {
			refreshCount++;
		}
	}

	@Test
	public void testSelfReferenceConfig() {
		getConfigService().load(SelfReferenceRootConfig.class);
	}

	@Test
	public void testScopePathListenerRefresh() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		final IScopePath scope = getConfigService().getScopePathBuilderFactory(TestConfiguration.class).annotatedPath().create();
		final TestScopePathListener listener = new TestScopePathListener();
		getConfigService().addScopePathListener(scope, listener);
		getConfigService().refresh(globalConfiguration);
		Assert.assertEquals(1, listener.refreshCount);
		getConfigService().removeScopePathListener(scope, listener);
		getConfigService().refresh(globalConfiguration);
		Assert.assertEquals(1, listener.refreshCount);
	}

	@Test
	public void testScopePathListenerSave() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		final IScopePath scope = getConfigService().getScopePathBuilderFactory(TestConfiguration.class).annotatedPath().create();
		final TestScopePathListener listener = new TestScopePathListener();
		getConfigService().addScopePathListener(scope, listener);
		globalConfiguration.setField1("asdf");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, listener.saveCount);
		getConfigService().removeScopePathListener(scope, listener);
		globalConfiguration.setField1("asdf2");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, listener.saveCount);
	}

	@Test(expected = InvalidConfigException.class)
	public void testBeanValidation() {
		final BeanValidationTestConfig config = getConfigService().load(BeanValidationTestConfig.class);
		config.setValue(1338);
		getConfigService().save(config);
	}

	@Test
	public void testSaveNewUnmodifiedConfig() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(0, getCountingDummyPersister().getSaveCount());
	}

	@Test
	public void testSaveNewModifiedConfig() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("asdf");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
	}

	@Test
	public void testSaveToScopePathIntoNonSourceScope() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);

		final IScopePath scope = getConfigService().getScopePathBuilderFactory(TestConfiguration.class).annotatedPathUntil(
				GlobalScopeDescriptor.NAME).create();
		getConfigService().copyToScopePath(globalConfiguration, scope);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveToScopePathIntoSourceScope() {
		final IScopePath scope = getConfigService().getScopePathBuilderFactory(TestConfiguration.class).annotatedPathUntil(
				GlobalScopeDescriptor.NAME).create();
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class, scope);
		getConfigService().copyToScopePath(globalConfiguration, scope);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoadFinalConfigClass() {
		getConfigService().load(FinalClassTestConfiguration.class);
	}

	@Test
	public void testUpdateNoChanges() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("a");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(0, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateWithChanges() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("a");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		globalConfiguration.setField1("asdf");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testDirtyStatusResetOnSave() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("a");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
	}

	@Test
	public void testDirtyStatusResetOnUpdate() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("a");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		globalConfiguration.setField1("b");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());

		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateSimpleChanges() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("a");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		globalConfiguration.setField1("test2"); //$NON-NLS-1$

		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateListAddition() {
		final TestConfiguration globalConfiguration = getConfigService().load(TestConfiguration.class);
		globalConfiguration.setField1("a");
		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		globalConfiguration.getSimpleList().add(Integer.valueOf(10));

		getConfigService().save(globalConfiguration);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateSetAddition() {
		final UpdateTestConfiguration config = getConfigService().load(UpdateTestConfiguration.class);
		config.setArray(null);
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		config.getSet().add("test"); //$NON-NLS-1$
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateMapAddition() {
		final UpdateTestConfiguration config = getConfigService().load(UpdateTestConfiguration.class);
		config.setArray(null);
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		config.getMap().put("test", Integer.valueOf(7)); //$NON-NLS-1$
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateComplexTypeChanges() {
		final UpdateTestConfiguration config = getConfigService().load(UpdateTestConfiguration.class);
		config.setArray(null);
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		config.getBaseClass().setSomeString("test"); //$NON-NLS-1$
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testUpdateArrayChanges() {
		final UpdateTestConfiguration config = getConfigService().load(UpdateTestConfiguration.class);
		config.setMap(null);
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());

		final Double[] array = config.getArray();
		array[0] = Double.valueOf(99);
		config.setArray(array);
		getConfigService().save(config);
		Assert.assertEquals(1, getCountingDummyPersister().getSaveCount());
		Assert.assertEquals(1, getCountingDummyPersister().getUpdateCount());
	}

	@Test
	public void testLoadSetWithProxy() {
		final ProxyTestConfiguration config = getConfigService().load(ProxyTestConfiguration.class);
		config.getM().clear();
		getConfigService().save(config);
		final ProxyTestConfiguration result = getConfigService().load(ProxyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testLoadArrayWithProxy() {
		final ProxyTestConfiguration config = getConfigService().load(ProxyTestConfiguration.class);
		getConfigService().save(config);
		final ProxyTestConfiguration result = getConfigService().load(ProxyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testLoadListWithProxy() {
		final ProxyTestConfiguration config = getConfigService().load(ProxyTestConfiguration.class);
		getConfigService().save(config);
		final ProxyTestConfiguration result = getConfigService().load(ProxyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testLoadMapWithProxy() {
		final ProxyTestConfiguration config = getConfigService().load(ProxyTestConfiguration.class);
		config.getO().clear();
		getConfigService().save(config);
		final ProxyTestConfiguration result = getConfigService().load(ProxyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testLoadWithProxiesPrimitiveTypes() {
		final ProxyTestConfiguration config = getConfigService().load(ProxyTestConfiguration.class);
		getConfigService().save(config);
		final ProxyTestConfiguration result = getConfigService().load(ProxyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingSetter() {
		final MissingSetterTestConfiguration config = getConfigService().load(MissingSetterTestConfiguration.class);
		getConfigService().save(config);
	}

	@Test
	public void testRefreshSimpleType() {
		final TestConfiguration configOld = getConfigService().load(TestConfiguration.class);
		final TestConfiguration configNew = getConfigService().load(TestConfiguration.class);
		configNew.setField1("huhu"); //$NON-NLS-1$
		configNew.setField2(Integer.valueOf(100));
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testRefreshComplexType() {
		final ComplexPropertyTestConfiguration configOld = getConfigService().load(ComplexPropertyTestConfiguration.class);
		final ComplexPropertyTestConfiguration configNew = getConfigService().load(ComplexPropertyTestConfiguration.class);
		configNew.setProperty(ComplexSubtype.create(getConfigService(), "345", "vxcv"));
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testRefreshMap() {
		final MapPropertyTestConfiguration configOld = getConfigService().load(MapPropertyTestConfiguration.class);
		final MapPropertyTestConfiguration configNew = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> map = getConfigService().createMap();
		map.put("vxcvsdf", Integer.valueOf(5));
		configNew.setUserID(map);
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);

	}

	@Test
	public void testRefreshComplexMap() {
		final ComplexMapTestConfiguration configOld = getConfigService().load(ComplexMapTestConfiguration.class);
		final ComplexMapTestConfiguration configNew = getConfigService().load(ComplexMapTestConfiguration.class);
		final Map<String, ComplexSubtype> map = getConfigService().createMap();
		map.put("vxcvsdf", ComplexSubtype.create(getConfigService(), "4565", "erzerz"));
		configNew.setProperty(map);
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testRefreshSet() {
		final SetPropertyTestConfiguration configOld = getConfigService().load(SetPropertyTestConfiguration.class);
		final SetPropertyTestConfiguration configNew = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> set = getConfigService().createSet();
		configNew.setData(set);
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testRefreshComplexSet() {
		final ComplexSetPropertyTestConfiguration configOld = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		final ComplexSetPropertyTestConfiguration configNew = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		final Set<ComplexSubtype> set = getConfigService().createSet();
		set.add(ComplexSubtype.create(getConfigService(), "5677", "xvcvxc"));
		configNew.setSubTypeSet(set);
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testRefreshList() {
		final ListTestConfiguration configOld = getConfigService().load(ListTestConfiguration.class);
		final ListTestConfiguration configNew = getConfigService().load(ListTestConfiguration.class);
		final List<Integer> list = getConfigService().createList(LinkedList.class);
		configNew.setIntField(list);
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testRefreshComplexList() {
		final ListTestConfiguration configOld = getConfigService().load(ListTestConfiguration.class);
		final ListTestConfiguration configNew = getConfigService().load(ListTestConfiguration.class);
		final List<ComplexSubtype> list = getConfigService().createList(LinkedList.class);
		list.add(ComplexSubtype.create(getConfigService(), "476345", "vxcvxcvsd"));
		configNew.setComplex(list);
		getConfigService().refresh(configNew);
		Assert.assertEquals(configNew, configOld);
	}

	@Test
	public void testDeletionWithoutChildDeletion() {
		final IScopePathBuilderFactory builderFactory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final IScopePath defaultPath = builderFactory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create();
		final IScopePath globalPath = builderFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final TestConfiguration codeDefaultConfig = new TestConfigurationDefaultFactory().createDefaultConfig(defaultPath);

		final TestConfiguration globalConfig = getConfigService().load(TestConfiguration.class, globalPath);
		globalConfig.setField1("global");
		getConfigService().save(globalConfig);

		final TestConfiguration userConfig = getConfigService().load(TestConfiguration.class);
		userConfig.setField1("user");
		getConfigService().save(userConfig);

		// the default configuration must be saved last - else the other configuration would be stale on load after deletion
		final TestConfiguration defaultConfig = getConfigService().load(TestConfiguration.class, defaultPath);
		defaultConfig.setField1("default");
		getConfigService().save(defaultConfig);

		getConfigService().delete(defaultPath, false);

		final TestConfiguration currentDefaultConfig = getConfigService().load(TestConfiguration.class, defaultPath);
		Assert.assertFalse(defaultConfig.equals(currentDefaultConfig));
		Assert.assertEquals(codeDefaultConfig, currentDefaultConfig);

		final TestConfiguration currentGlobalConfig = getConfigService().load(TestConfiguration.class, globalPath);
		Assert.assertEquals(globalConfig, currentGlobalConfig);

		final TestConfiguration currentUserConfig = getConfigService().load(TestConfiguration.class);
		Assert.assertEquals(userConfig, currentUserConfig);
	}

	@Test
	public void testDeletionWithChildDeletion() {
		final IScopePathBuilderFactory builderFactory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final IScopePath defaultPath = builderFactory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create();
		final IScopePath globalPath = builderFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final TestConfiguration codeDefaultConfig = new TestConfigurationDefaultFactory().createDefaultConfig(defaultPath);

		final TestConfiguration globalConfig = getConfigService().load(TestConfiguration.class, globalPath);
		globalConfig.setField1("global");
		getConfigService().save(globalConfig);

		final TestConfiguration userConfig = getConfigService().load(TestConfiguration.class);
		userConfig.setField1("user");
		getConfigService().save(userConfig);

		// the default configuration must be saved last - else the other configuration would be stale on load after deletion
		final TestConfiguration defaultConfig = getConfigService().load(TestConfiguration.class, defaultPath);
		defaultConfig.setField1("default");
		getConfigService().save(defaultConfig);

		getConfigService().delete(defaultPath, true);

		final TestConfiguration currentDefaultConfig = getConfigService().load(TestConfiguration.class, defaultPath);
		Assert.assertFalse(defaultConfig.equals(currentDefaultConfig));
		Assert.assertEquals(codeDefaultConfig, currentDefaultConfig);

		final TestConfiguration currentGlobalConfig = getConfigService().load(TestConfiguration.class, globalPath);
		Assert.assertFalse(globalConfig.equals(currentGlobalConfig));
		Assert.assertEquals(codeDefaultConfig, currentGlobalConfig);

		final TestConfiguration currentUserConfig = getConfigService().load(TestConfiguration.class);
		Assert.assertFalse(userConfig.equals(currentUserConfig));
		Assert.assertEquals(codeDefaultConfig, currentUserConfig);
	}

}
