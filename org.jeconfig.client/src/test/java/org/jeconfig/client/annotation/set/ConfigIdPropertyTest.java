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

public class ConfigIdPropertyTest extends AbstractConfigServiceTest {

	@Test
	public void testCustomConverterOnIdProperty() {
		final CustomIdConverterTestConfig config = getConfigService().load(CustomIdConverterTestConfig.class);
		final Set<CustomIdConverterSubConfig> set = getConfigService().createSet();
		set.add(CustomIdConverterSubConfig.create(getConfigService(), "a", "b2")); //$NON-NLS-1$//$NON-NLS-2$
		config.setSet(set);
		getConfigService().save(config);

		final CustomIdConverterTestConfig config2 = getConfigService().load(CustomIdConverterTestConfig.class);
		Assert.assertEquals("a12", config2.getSet().iterator().next().getId()); //$NON-NLS-1$
	}

	@Test
	public void testChildOverwrites() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ConfigIdTestConfiguration.class);
		final ConfigIdTestConfiguration parentConfig = getConfigService().load(
				ConfigIdTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 5, "peter")); //$NON-NLS-1$
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 7, "hans")); //$NON-NLS-1$
		getConfigService().save(parentConfig);

		final ConfigIdTestConfiguration childConfig = getConfigService().load(ConfigIdTestConfiguration.class);
		childConfig.getSet().clear();
		childConfig.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 7, "hans")); //$NON-NLS-1$
		childConfig.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 5, "bob")); //$NON-NLS-1$
		childConfig.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 9, "erik")); //$NON-NLS-1$
		getConfigService().save(childConfig);

		final ConfigIdTestConfiguration result = getConfigService().load(ConfigIdTestConfiguration.class);
		Assert.assertEquals(childConfig.getSet(), result.getSet());
	}

	@Test
	public void testParentOverwrites() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ConfigIdTestConfiguration2.class);
		final ConfigIdTestConfiguration2 parentConfig = getConfigService().load(
				ConfigIdTestConfiguration2.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration2.create(getConfigService(), 5, "bob")); //$NON-NLS-1$
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration2.create(getConfigService(), 9, "erik")); //$NON-NLS-1$
		getConfigService().save(parentConfig);

		final ConfigIdTestConfiguration2 childConfig = getConfigService().load(ConfigIdTestConfiguration2.class);
		childConfig.getSet().clear();
		childConfig.getSet().add(ConfigIdComplexTypeTestConfiguration2.create(getConfigService(), 5, "peter")); //$NON-NLS-1$
		childConfig.getSet().add(ConfigIdComplexTypeTestConfiguration2.create(getConfigService(), 9, "hans")); //$NON-NLS-1$
		getConfigService().save(childConfig);

		final ConfigIdTestConfiguration2 result = getConfigService().load(ConfigIdTestConfiguration2.class);
		Assert.assertEquals(parentConfig.getSet(), result.getSet());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIdUniquenessValidation() {
		final ConfigIdTestConfiguration config = getConfigService().load(ConfigIdTestConfiguration.class);
		config.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 1, "name")); //$NON-NLS-1$
		config.getSet().add(ConfigIdComplexTypeTestConfiguration.create(getConfigService(), 1, "name2")); //$NON-NLS-1$
		getConfigService().save(config);
	}

	@Test
	public void testChildOverwritesItemRemovedStrategyAdd() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ConfigIdTestConfiguration3.class);
		final ConfigIdTestConfiguration3 parentConfig = getConfigService().load(
				ConfigIdTestConfiguration3.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final ConfigIdComplexTypeTestConfiguration3 removedConfig = ConfigIdComplexTypeTestConfiguration3.create(
				getConfigService(),
				5,
				"peter"); //$NON-NLS-1$
		parentConfig.getSet().add(removedConfig);
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration3.create(getConfigService(), 7, "hans")); //$NON-NLS-1$
		getConfigService().save(parentConfig);

		final ConfigIdTestConfiguration3 childConfig = getConfigService().load(ConfigIdTestConfiguration3.class);
		childConfig.getSet().remove(removedConfig);
		childConfig.getSet().add(ConfigIdComplexTypeTestConfiguration3.create(getConfigService(), 9, "erik")); //$NON-NLS-1$
		getConfigService().save(childConfig);

		final ConfigIdTestConfiguration3 result = getConfigService().load(ConfigIdTestConfiguration3.class);
		Assert.assertTrue(result.getSet().contains(removedConfig));
	}

	@Test
	public void testChildOverwritesItemAddedStrategyRemove() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ConfigIdTestConfiguration4.class);
		final ConfigIdTestConfiguration4 parentConfig = getConfigService().load(
				ConfigIdTestConfiguration4.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration4.create(getConfigService(), 5, "peter")); //$NON-NLS-1$
		parentConfig.getSet().add(ConfigIdComplexTypeTestConfiguration4.create(getConfigService(), 7, "hans")); //$NON-NLS-1$
		getConfigService().save(parentConfig);

		final ConfigIdTestConfiguration4 childConfig = getConfigService().load(ConfigIdTestConfiguration4.class);
		final ConfigIdComplexTypeTestConfiguration4 complexType = ConfigIdComplexTypeTestConfiguration4.create(
				getConfigService(),
				9,
				"erik"); //$NON-NLS-1$
		childConfig.getSet().add(complexType);
		getConfigService().save(childConfig);

		final ConfigIdTestConfiguration4 result = getConfigService().load(ConfigIdTestConfiguration4.class);
		childConfig.getSet().remove(complexType);
		Assert.assertEquals(result.getSet(), childConfig.getSet());
	}
}
