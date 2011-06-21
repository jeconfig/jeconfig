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

package org.jeconfig.client.annotation.crossreferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigCrossReferencesTest extends AbstractConfigServiceTest {

	private CrossReferenceTestConfigReferenced referencedConfig;

	@Override
	public void setUp() {
		super.setUp();

		referencedConfig = getConfigService().load(CrossReferenceTestConfigReferenced.class);
		referencedConfig.setServerIp("localhost"); //$NON-NLS-1$
		referencedConfig.setServerPort("1337"); //$NON-NLS-1$
		getConfigService().save(referencedConfig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferenceCycleReferences() {
		getConfigService().load(CrossReferenceTestConfigCycleReference.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferencesListCycleReferences() {
		getConfigService().load(CrossReferenceTestConfigListCycleReference.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferencesMapCycleReferences() {
		getConfigService().load(CrossReferenceTestConfigMapCycleReference.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferencesSetCycleReferences() {
		getConfigService().load(CrossReferenceTestConfigSetCycleReference.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferencesArrayCycleReferences() {
		getConfigService().load(CrossReferenceTestConfigArrayCycleReference.class);
	}

	@Test
	public void testDeepCrossReferences() {
		final CrossReferenceTestConfigDeepReferences1 deepPref1 = new CrossReferenceTestConfigDeepReferences1();
		final CrossReferenceTestConfigDeepReferences2 deepPref2 = new CrossReferenceTestConfigDeepReferences2();

		final CrossReferenceTestConfigDeepReferences config = getConfigService().load(
				CrossReferenceTestConfigDeepReferences.class);
		config.setName("test"); //$NON-NLS-1$
		getConfigService().save(config);

		final CrossReferenceTestConfigDeepReferences result = getConfigService().load(
				CrossReferenceTestConfigDeepReferences.class);
		Assert.assertEquals(config.getName(), result.getName());
		Assert.assertEquals(deepPref1.getId(), result.getConfig().getId());
		Assert.assertEquals(deepPref2.getTest(), result.getConfig().getConfig().getTest());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCreateNewConfigWithCrossReferences() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final CrossReferenceTestConfigReferenced referenced = new CrossReferenceTestConfigReferenced();
		referenced.setServerIp("192.168.100.6");
		referenced.setServerPort("21012"); //$NON-NLS-1$
		config.setReferenceConfig(referenced);
	}

	@Test
	public void testCrossReferenceInConfigClass() {
		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(referencedConfig, actualConfig.getReferenceConfig());
	}

	@Test
	public void testCrossReferenceInComplexProperty() {
		final CrossReferenceTestSubConfig subConfig = CrossReferenceTestSubConfig.create(getConfigService(), "a", "b"); //$NON-NLS-1$//$NON-NLS-2$

		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.setComplex(subConfig);
		getConfigService().save(config);
		getConfigService().save(config);
		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertNotNull(actualConfig.getComplex());
		Assert.assertEquals(subConfig.getName(), actualConfig.getComplex().getName());
		Assert.assertEquals(referencedConfig, actualConfig.getComplex().getReferencedConfig());
	}

	@Test
	public void testCrossReferenceInArrayProperty() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final CrossReferenceTestSubConfig[] array = new CrossReferenceTestSubConfig[] {CrossReferenceTestSubConfig.create(
				getConfigService(),
				"id",
				"name")};
		config.setArray(array);
		getConfigService().save(config);

		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(referencedConfig, actualConfig.getArray()[0].getReferencedConfig());
	}

	@Test
	public void testCrossReferenceInListPropertyPolymorph() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final List<CrossReferenceTestSubConfig> list = getConfigService().createList();
		list.add(CrossReferenceTestSubConfig.create(getConfigService(), "id", "name"));
		config.setList(list);
		getConfigService().save(config);

		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(referencedConfig, actualConfig.getList().get(0).getReferencedConfig());
	}

	@Test
	public void testCrossReferenceInSetProperty() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final Set<CrossReferenceTestSubConfig> set = getConfigService().createSet();
		set.add(CrossReferenceTestSubConfig.create(getConfigService(), "id", "name"));
		config.setSet(set);
		getConfigService().save(config);

		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(referencedConfig, actualConfig.getSet().iterator().next().getReferencedConfig());
	}

	@Test
	public void testCrossReferenceInMapProperty() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final Map<String, CrossReferenceTestSubConfig> map = getConfigService().createMap();
		map.put("key", CrossReferenceTestSubConfig.create(getConfigService(), "id", "name"));
		config.setMap(map);
		getConfigService().save(config);

		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(referencedConfig, actualConfig.getMap().values().iterator().next().getReferencedConfig());
	}

	@Test
	public void testCrossReferenceWithConfigClassScope() {
		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(referencedConfig, actualConfig.getReferenceConfig());
	}

	@Test
	public void testCrossReferenceWithAnnotationScope() {
		final ScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				CrossReferenceTestConfigReferenced.class);
		final ScopePath scope = scopeFactory.stub().append(DefaultScopeDescriptor.NAME).create();
		final CrossReferenceTestConfigReferenced defaultReferencedConfig = getConfigService().load(
				CrossReferenceTestConfigReferenced.class,
				scope);
		defaultReferencedConfig.setServerIp("10.2.0.1"); //$NON-NLS-1$
		getConfigService().save(defaultReferencedConfig);

		final CrossReferenceTestConfigAnnotationScope actualConfig = getConfigService().load(
				CrossReferenceTestConfigAnnotationScope.class);
		Assert.assertEquals(defaultReferencedConfig, actualConfig.getReferencedConfig());
	}

	@Test
	public void testCrossReferenceResolutionOnSave() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final List<CrossReferenceTestSubConfig> list = getConfigService().createList();
		list.add(CrossReferenceTestSubConfig.create(getConfigService(), "id", "name"));
		config.setList(list);
		getConfigService().save(config);

		Assert.assertEquals(referencedConfig, config.getList().get(0).getReferencedConfig());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferenceAnnoOnConfigProperty() {
		getConfigService().load(CrossReferenceTestConfigAnnoOnConfigProperty.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferenceNonConfigClassComplexType() {
		getConfigService().load(CrossReferenceTestConfigNonConfigClassComplex.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrossReferenceNonConfigClassSimpleType() {
		getConfigService().load(CrossReferenceTestConfigNonConfigClassSimple.class);
	}

	@Test
	public void testAnnotatedInstanceName() {
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(InstanceScopeDescriptor.PROP_INSTANCE_NAME, "FirstInstance"); //$NON-NLS-1$
		final ScopePathBuilderFactory scopePathBuilderFactory = getConfigService().getScopePathBuilderFactory(
				CrossReferenceTestConfigReferenced.class);
		final ScopePath instanceScopePath = scopePathBuilderFactory.annotatedPath().append(
				InstanceScopeDescriptor.NAME,
				properties).create();

		final CrossReferenceTestConfigReferenced instanceConfig = getConfigService().load(
				CrossReferenceTestConfigReferenced.class,
				instanceScopePath);
		instanceConfig.setServerPort("1337"); //$NON-NLS-1$
		getConfigService().save(instanceConfig);

		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		Assert.assertEquals(instanceConfig, config.getInstanceReferenceConfig());
	}
}
