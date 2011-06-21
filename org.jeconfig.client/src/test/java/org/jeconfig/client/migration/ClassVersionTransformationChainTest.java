/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel, Lukas Gross
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

package org.jeconfig.client.migration;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.persister.ConfigPersister;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.junit.Assert;
import org.junit.Test;

public class ClassVersionTransformationChainTest extends AbstractConfigServiceTest {
	@Test
	public void testTransformToNewVersionWithOneConverter() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ClassVersionTransformationChainTestConfiguration.class);

		final ClassVersionTransformationChainTestConfiguration config = getConfigService().load(
				ClassVersionTransformationChainTestConfiguration.class);
		config.setId(2);
		getConfigService().save(config);

		final ConfigPersister persister = getPersister();
		final ComplexConfigDTO configDTO = persister.loadConfiguration(factory.annotatedPath().create());
		configDTO.setClassVersion(1);

		getConfigService().delete(ClassVersionTransformationChainTestConfiguration.class);
		persister.saveConfiguration(configDTO);

		final ClassVersionTransformationChainTestConfiguration result = getConfigService().load(
				ClassVersionTransformationChainTestConfiguration.class);
		Assert.assertEquals(MyConfigTransformerV1V3.NEW_ID_VALUE, result.getId());
	}

	@Test(expected = StaleConfigException.class)
	public void testNoTransformerForNewVersion() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ClassVersionTChainNoConverterForNewVersionTestConfiguration.class);

		final ClassVersionTChainNoConverterForNewVersionTestConfiguration config = getConfigService().load(
				ClassVersionTChainNoConverterForNewVersionTestConfiguration.class);
		config.setId(2);
		getConfigService().save(config);

		final ConfigPersister persister = getPersister();
		final ComplexConfigDTO configDTO = persister.loadConfiguration(factory.annotatedPath().create());
		configDTO.setClassVersion(1);

		getConfigService().delete(ClassVersionTChainNoConverterForNewVersionTestConfiguration.class);
		persister.saveConfiguration(configDTO);

		getConfigService().load(ClassVersionTChainNoConverterForNewVersionTestConfiguration.class);
	}

	@Test
	public void testTransformToNewVersionWithMultipleConverters() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ClassVersionTChainMultipleConverterForNewVersionTestConfiguration.class);

		final ClassVersionTChainMultipleConverterForNewVersionTestConfiguration config = getConfigService().load(
				ClassVersionTChainMultipleConverterForNewVersionTestConfiguration.class);
		config.setId(2);
		config.setName("asdf"); //$NON-NLS-1$
		getConfigService().save(config);

		final ConfigPersister persister = getPersister();
		final ComplexConfigDTO configDTO = persister.loadConfiguration(factory.annotatedPath().create());
		configDTO.setClassVersion(1);

		getConfigService().delete(ClassVersionTChainMultipleConverterForNewVersionTestConfiguration.class);
		persister.saveConfiguration(configDTO);

		final ClassVersionTChainMultipleConverterForNewVersionTestConfiguration result = getConfigService().load(
				ClassVersionTChainMultipleConverterForNewVersionTestConfiguration.class);
		Assert.assertEquals(MyConfigTransformerV1V5.NEW_NAME_VALUE, result.getName());
		Assert.assertEquals(MyConfigTransformerV5V7.NEW_ID_VALUE, result.getId());
	}
}
