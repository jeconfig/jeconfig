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

package org.jeconfig.client.annotation.map;

import java.util.Map;

import junit.framework.Assert;

import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigMapPropertyTest extends AbstractConfigServiceTest {

	@Test
	public void testStaleMap() {
		final IScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(MapPropertyTestConfiguration.class);
		final IScopePath globalScopePath = factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();
		final MapPropertyTestConfiguration parentConfig = getConfigService().load(
				MapPropertyTestConfiguration.class,
				globalScopePath);
		final Map<String, Integer> map = getConfigService().createMap();
		map.put("a", Integer.valueOf(1));
		parentConfig.setUserID(map);
		getConfigService().save(parentConfig);

		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		config.getUserID().put("a", Integer.valueOf(2));
		getConfigService().save(config);
		parentConfig.getUserID().put("a", Integer.valueOf(3));
		getConfigService().save(parentConfig);
		final MapPropertyTestConfiguration config2 = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(Integer.valueOf(3), config2.getUserID().get("a"));
	}

	@Test
	public void testCustomConverterOnLoadAndSave() {
		final CustomConverterMapTestConfig config = getConfigService().load(CustomConverterMapTestConfig.class);
		final Map<String, String> map = getConfigService().createMap();
		map.put("a", "asdf");
		config.setMap(map);
		getConfigService().save(config);
		final CustomConverterMapTestConfig config2 = getConfigService().load(CustomConverterMapTestConfig.class);
		Assert.assertEquals(config.getMap().get("a") + "12", config2.getMap().get("a12"));
	}

	@Test
	public void testSaveAndLoadEmtpyMap() {
		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		getConfigService().save(config);
		final MapPropertyTestConfiguration result = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(config.getUserID(), result.getUserID());
	}

	@Test
	public void testSaveAndLoadMapWithNullElement() {
		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> map = config.getUserID();
		map.put(null, null);
		map.put("user", null);
		map.put(null, Integer.valueOf(1));
		getConfigService().save(config);
		final MapPropertyTestConfiguration result = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(config.getUserID(), result.getUserID());
	}

	@Test
	public void testSaveAndLoadMapPrimitiveType() {
		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> map = config.getUserID();
		for (int i = 0; i < 10; i++) {
			map.put("user" + i, Integer.valueOf(i));
		}
		getConfigService().save(config);
		final MapPropertyTestConfiguration result = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(config.getUserID(), result.getUserID());
	}

	@Test
	public void testUseChildMergeWithSimpleTypes() {
		final IScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				SimpleTypeMapPropertyTestConfiguration.class);

		final SimpleTypeMapPropertyTestConfiguration parentConfig = getConfigService().load(
				SimpleTypeMapPropertyTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, Integer> parentMap = parentConfig.getUserID();
		parentMap.put("user1", Integer.valueOf(5));
		parentMap.put("name", Integer.valueOf(3));
		getConfigService().save(parentConfig);

		final SimpleTypeMapPropertyTestConfiguration childConfig = getConfigService().load(
				SimpleTypeMapPropertyTestConfiguration.class);
		final Map<String, Integer> childMap = childConfig.getUserID();
		childMap.put("user1", Integer.valueOf(6));
		childMap.put("name", Integer.valueOf(8));
		getConfigService().save(childConfig);

		final SimpleTypeMapPropertyTestConfiguration result = getConfigService().load(
				SimpleTypeMapPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getUserID(), result.getUserID());
	}

	@Test
	public void testMergeDefaultEntryAddedStrategyWithSimpleTypes() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				MapPropertyTestConfiguration.class);
		final MapPropertyTestConfiguration parentConfig = getConfigService().load(
				MapPropertyTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, Integer> parentMap = parentConfig.getUserID();
		parentMap.put("user", Integer.valueOf(5));
		getConfigService().save(parentConfig);

		final MapPropertyTestConfiguration childConfig = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> childMap = childConfig.getUserID();
		childMap.put("user", Integer.valueOf(5));
		childMap.put("name", Integer.valueOf(3));
		getConfigService().save(childConfig);

		final MapPropertyTestConfiguration result = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getUserID(), result.getUserID());
	}

	@Test
	public void testMergeDefaultEntryRemovedStrategyWithSimpleTypes() {
		final IScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				MapPropertyTestConfiguration.class);
		final MapPropertyTestConfiguration parentConfig = getConfigService().load(
				MapPropertyTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, Integer> parentMap = parentConfig.getUserID();
		parentMap.put("user", Integer.valueOf(5));
		parentMap.put("name", Integer.valueOf(3));
		getConfigService().save(parentConfig);

		final MapPropertyTestConfiguration childConfig = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> childMap = childConfig.getUserID();
		childMap.put("user", Integer.valueOf(5));
		getConfigService().save(childConfig);

		final MapPropertyTestConfiguration result = getConfigService().load(MapPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getUserID(), result.getUserID());
	}

	@Test
	public void testUseParentMergingStrategySimpleTypes() {
		final IScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				UseParentMergingStrategyTestConfiguration.class);
		final UseParentMergingStrategyTestConfiguration parentConfig = getConfigService().load(
				UseParentMergingStrategyTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, Integer> parentMap = parentConfig.getUserID();
		parentMap.put("user1", Integer.valueOf(5));
		parentMap.put("name", Integer.valueOf(3));
		getConfigService().save(parentConfig);

		final UseParentMergingStrategyTestConfiguration childConfig = getConfigService().load(
				UseParentMergingStrategyTestConfiguration.class);
		final Map<String, Integer> childMap = childConfig.getUserID();
		childMap.put("user1", Integer.valueOf(7));
		childMap.put("name", Integer.valueOf(4));

		getConfigService().save(childConfig);

		final UseParentMergingStrategyTestConfiguration result = getConfigService().load(
				UseParentMergingStrategyTestConfiguration.class);
		Assert.assertEquals(parentConfig.getUserID(), result.getUserID());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongKeyType() {
		final WrongKeyTypeMapPropertyTestConfiguration config = getConfigService().load(
				WrongKeyTypeMapPropertyTestConfiguration.class);
		config.getUserID().put(Double.valueOf(2d), Integer.valueOf(3));
		getConfigService().save(config);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongValueType() {
		final WrongValueTypeMapPropertyTestConfiguration config = getConfigService().load(
				WrongValueTypeMapPropertyTestConfiguration.class);
		config.getUserID().put("asdf", Double.valueOf(33));
		getConfigService().save(config);
	}

	@Test
	public void testUseChildMergeWithComplexTypes() {
		final IScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ComplexTypeMapPropertyTestConfiguration.class);
		final ComplexTypeMapPropertyTestConfiguration parentConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, MapPropertyTestConfiguration> parentFilterConfigMap = parentConfig.getFilterConfigMap();
		parentFilterConfigMap.put("TestFilter", MapPropertyTestConfiguration.create(getConfigService()));
		final Map<Integer, SimpleTypeMapPropertyTestConfiguration> parentUserIDConfigMap = parentConfig.getUserIDConfigMap();
		parentUserIDConfigMap.put(Integer.valueOf(1), SimpleTypeMapPropertyTestConfiguration.create(getConfigService()));

		final ComplexTypeMapPropertyTestConfiguration childConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class);
		final Map<String, MapPropertyTestConfiguration> childFilterConfigMap = childConfig.getFilterConfigMap();
		childFilterConfigMap.put("TestFilter", MapPropertyTestConfiguration.create(getConfigService()));
		final Map<Integer, SimpleTypeMapPropertyTestConfiguration> childUserIDConfigMap = childConfig.getUserIDConfigMap();
		childUserIDConfigMap.put(Integer.valueOf(1), SimpleTypeMapPropertyTestConfiguration.create(getConfigService()));

		getConfigService().save(parentConfig);
		getConfigService().save(childConfig);

		final ComplexTypeMapPropertyTestConfiguration resultConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getFilterConfigMap(), resultConfig.getFilterConfigMap());
		Assert.assertEquals(childConfig.getUserIDConfigMap(), resultConfig.getUserIDConfigMap());
	}

	@Test
	public void testMergeDefaultEntryAddedStrategyWithComplexTypes() {
		final IScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ComplexTypeMapPropertyTestConfiguration.class);

		final ComplexTypeMapPropertyTestConfiguration parentConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, MapPropertyTestConfiguration> parentFilterConfigMap = parentConfig.getFilterConfigMap();
		parentFilterConfigMap.put("TestFilter", MapPropertyTestConfiguration.create(getConfigService()));
		getConfigService().save(parentConfig);

		final ComplexTypeMapPropertyTestConfiguration childConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class);
		final Map<String, MapPropertyTestConfiguration> childFilterConfigMap = childConfig.getFilterConfigMap();
		childFilterConfigMap.put("TestFilter", MapPropertyTestConfiguration.create(getConfigService()));
		final Map<Integer, SimpleTypeMapPropertyTestConfiguration> childUserIDConfigMap = childConfig.getUserIDConfigMap();
		childUserIDConfigMap.put(Integer.valueOf(1), SimpleTypeMapPropertyTestConfiguration.create(getConfigService()));
		getConfigService().save(childConfig);

		final ComplexTypeMapPropertyTestConfiguration resultConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getFilterConfigMap(), resultConfig.getFilterConfigMap());
		Assert.assertEquals(childConfig.getUserIDConfigMap(), resultConfig.getUserIDConfigMap());
	}

	@Test
	public void testMergeDefaultEntryRemovedStrategyWithComplexTypes() {
		final IScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ComplexTypeMapPropertyTestConfiguration.class);
		final ComplexTypeMapPropertyTestConfiguration parentConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Map<String, MapPropertyTestConfiguration> parentFilterConfigMap = parentConfig.getFilterConfigMap();
		parentFilterConfigMap.put("TestFilter", MapPropertyTestConfiguration.create(getConfigService()));
		final Map<Integer, SimpleTypeMapPropertyTestConfiguration> parentUserIDConfigMap = parentConfig.getUserIDConfigMap();
		parentUserIDConfigMap.put(Integer.valueOf(1), SimpleTypeMapPropertyTestConfiguration.create(getConfigService()));
		getConfigService().save(parentConfig);

		final ComplexTypeMapPropertyTestConfiguration childConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class);
		final Map<String, MapPropertyTestConfiguration> childFilterConfigMap = childConfig.getFilterConfigMap();
		childFilterConfigMap.put("TestFilter", MapPropertyTestConfiguration.create(getConfigService()));
		getConfigService().save(childConfig);

		final ComplexTypeMapPropertyTestConfiguration resultConfig = getConfigService().load(
				ComplexTypeMapPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getFilterConfigMap(), resultConfig.getFilterConfigMap());
		Assert.assertEquals(childConfig.getUserIDConfigMap(), resultConfig.getUserIDConfigMap());
	}

	@Test
	public void testMissingMapPropertyAnnotation() {
		final MissingMapPropertyAnnotationTestConfiguration config = getConfigService().load(
				MissingMapPropertyAnnotationTestConfiguration.class);
		final Map<String, Integer> map = config.getUserID();
		map.put("user", Integer.valueOf(7856));
		getConfigService().save(config);
		final MissingMapPropertyAnnotationTestConfiguration result = getConfigService().load(
				MissingMapPropertyAnnotationTestConfiguration.class);
		Assert.assertTrue(result.getUserID().isEmpty());
	}

}
