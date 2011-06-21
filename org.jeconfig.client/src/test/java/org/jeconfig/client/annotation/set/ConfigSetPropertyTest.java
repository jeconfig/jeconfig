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

package org.jeconfig.client.annotation.set;

import java.util.Set;

import junit.framework.Assert;

import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigSetPropertyTest extends AbstractConfigServiceTest {

	@Test
	public void testCustomConverterOnLoadAndSave() {
		final CustomConverterSetTestConfig config = getConfigService().load(CustomConverterSetTestConfig.class);
		final Set<String> set = getConfigService().createSet();
		set.add("asdf");
		config.setSet(set);
		getConfigService().save(config);
		final CustomConverterSetTestConfig config2 = getConfigService().load(CustomConverterSetTestConfig.class);
		Assert.assertEquals(config.getSet().iterator().next() + "12", config2.getSet().iterator().next());
	}

	@Test
	public void testSaveAndLoadPrimitiveTypeSet() {
		final SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> set = config.getData();
		set.add("data1");
		set.add("lukas");
		set.add("user234");
		getConfigService().save(config);
		final SetPropertyTestConfiguration result = getConfigService().load(SetPropertyTestConfiguration.class);
		Assert.assertEquals(config.getData(), result.getData());
	}

	@Test
	public void testSaveAndLoadEmptySet() {
		final SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		getConfigService().save(config);
		final SetPropertyTestConfiguration result = getConfigService().load(SetPropertyTestConfiguration.class);
		Assert.assertEquals(config.getData(), result.getData());
	}

	@Test
	public void testSaveAndLoadSetWithNullElement() {
		final SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> set = config.getData();
		set.add(null);
		getConfigService().save(config);
		final SetPropertyTestConfiguration result = getConfigService().load(SetPropertyTestConfiguration.class);
		Assert.assertEquals(config.getData(), result.getData());
	}

	@Test
	public void testUseChildMergeWithSimpleTypes() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				SimpleTypeSetPropertyTestConfiguration.class);
		final SimpleTypeSetPropertyTestConfiguration parentConfig = getConfigService().load(
				SimpleTypeSetPropertyTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Set<Integer> parentSet = parentConfig.getUserID();
		parentSet.add(Integer.valueOf(5));
		parentSet.add(Integer.valueOf(3));
		getConfigService().save(parentConfig);

		final SimpleTypeSetPropertyTestConfiguration childConfig = getConfigService().load(
				SimpleTypeSetPropertyTestConfiguration.class);
		final Set<Integer> childSet = childConfig.getUserID();
		childSet.add(Integer.valueOf(6));
		childSet.add(Integer.valueOf(8));
		getConfigService().save(childConfig);

		final SimpleTypeSetPropertyTestConfiguration result = getConfigService().load(
				SimpleTypeSetPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getUserID(), result.getUserID());
	}

	@Test
	public void testMergeDefaultEntryAddedStrategyWithSimpleTypes() {
		final ScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SetPropertyTestConfiguration.class);
		final SetPropertyTestConfiguration parentConfig = getConfigService().load(
				SetPropertyTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Set<String> parentSet = parentConfig.getData();
		parentSet.add("user");
		getConfigService().save(parentConfig);

		final SetPropertyTestConfiguration childConfig = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> childSet = childConfig.getData();
		childSet.add("user");
		childSet.add("name");
		getConfigService().save(childConfig);

		final SetPropertyTestConfiguration result = getConfigService().load(SetPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getData(), result.getData());
	}

	@Test
	public void testMergeDefaultEntryRemovedStrategyWithSimpleTypes() {
		final ScopePathBuilderFactory scopeFactory = getConfigService().getScopePathBuilderFactory(
				SetPropertyTestConfiguration.class);
		final SetPropertyTestConfiguration parentConfig = getConfigService().load(
				SetPropertyTestConfiguration.class,
				scopeFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final Set<String> parentMap = parentConfig.getData();
		parentMap.add("user");
		parentMap.add("name");
		getConfigService().save(parentConfig);

		final SetPropertyTestConfiguration childConfig = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> childMap = childConfig.getData();
		childMap.add("user");
		getConfigService().save(childConfig);

		final SetPropertyTestConfiguration result = getConfigService().load(SetPropertyTestConfiguration.class);
		Assert.assertEquals(childConfig.getData(), result.getData());
	}

	@Test
	public void testMissingSetPropertyAnnotation() {
		final MissingSetPropertyAnnotationTestConfiguration config = getConfigService().load(
				MissingSetPropertyAnnotationTestConfiguration.class);
		final Set<String> map = config.getUserID();
		map.add("user");
		getConfigService().save(config);
		final MissingSetPropertyAnnotationTestConfiguration result = getConfigService().load(
				MissingSetPropertyAnnotationTestConfiguration.class);
		Assert.assertTrue(result.getUserID().isEmpty());
	}
}
