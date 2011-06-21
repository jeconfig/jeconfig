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

import junit.framework.Assert;

import org.jeconfig.api.conversion.SimpleTypeConverterRegistry;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.client.annotation.configclass.TestConfiguration;
import org.jeconfig.client.internal.mapping.ConfigDTOMapper;
import org.jeconfig.client.proxy.ConfigProxyFactory;
import org.jeconfig.client.proxy.ProxyUpdater;
import org.junit.Test;

public class ConfigServiceCachingTest extends AbstractConfigServiceTest {

	@Test
	public void testCachingOnSave() {
		final TestConfiguration userConfiguration = getConfigService().load(TestConfiguration.class);
		getConfigService().save(userConfiguration);
		Assert.assertEquals(3, getCountingDummyPersister().getLoadCount());

		getConfigService().load(TestConfiguration.class);
		Assert.assertEquals(3, getCountingDummyPersister().getLoadCount());
	}

	@Test
	public void testCachingOnLoad() {
		final ScopePath scope = getConfigService().getScopePathBuilderFactory(TestConfiguration.class).annotatedPath().create();
		final ConfigProxyFactory proxyFactory = new ConfigProxyFactory(getConfigSetupService().getSimpleTypeConverterRegistry());
		final SimpleTypeConverterRegistry simpleTypeConverterRegistry = getConfigSetupService().getSimpleTypeConverterRegistry();
		final ProxyUpdater proxyUpdater = new ProxyUpdater(proxyFactory, simpleTypeConverterRegistry);
		final ConfigDTOMapper mapper = new ConfigDTOMapper(simpleTypeConverterRegistry, proxyFactory, proxyUpdater);
		getCountingDummyPersister().saveConfiguration(mapper.serialize(new TestConfiguration(), scope));

		getConfigService().load(TestConfiguration.class);
		Assert.assertEquals(3, getCountingDummyPersister().getLoadCount());

		getConfigService().load(TestConfiguration.class);
		Assert.assertEquals(3, getCountingDummyPersister().getLoadCount());
	}
}
