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

import org.jeconfig.api.exception.DefaultConfigExceptionHandler;
import org.jeconfig.api.persister.ConfigPersister;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.client.internal.autosave.ConfigAutoSaveServiceImpl;
import org.jeconfig.server.persister.InMemoryPersister;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigAutoSaveServiceTest extends AbstractConfigServiceTest {

	private ConfigAutoSaveServiceImpl autoSaveService;

	@Override
	protected ConfigPersister createPersister() {
		return new InMemoryPersister();
	}

	@Override
	public InMemoryPersister getPersister() {
		return (InMemoryPersister) super.getPersister();
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		autoSaveService = new ConfigAutoSaveServiceImpl();
		autoSaveService.bindConfigService(getConfigService());
	}

	@Test
	public void testAutoSaveConfig() throws InterruptedException {
		final ConfigServiceAccessorTestConfiguration config = getConfigService().load(
				ConfigServiceAccessorTestConfiguration.class);
		config.setField1("sdfksdf"); //$NON-NLS-1$
		autoSaveService.manageConfig(config, new DefaultConfigExceptionHandler());
		autoSaveService.setAutoSaveInterval(50);
		Thread.sleep(150);
		final ConfigServiceAccessorTestConfiguration result = getConfigService().load(
				ConfigServiceAccessorTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testDirtyConfigExists() {
		final ConfigServiceAccessorTestConfiguration config = getConfigService().load(
				ConfigServiceAccessorTestConfiguration.class);
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(
				ConfigServiceAccessorTestConfiguration.class);

		config.setField2(Integer.valueOf(77));
		autoSaveService.manageConfig(config, new DefaultConfigExceptionHandler());

		Assert.assertTrue(autoSaveService.hasDirtyConfig(factory.annotatedPath().create()));
	}

	@Override
	@After
	public void tearDown() {
		if (autoSaveService != null) {
			autoSaveService.close();
		}
		super.tearDown();
	}
}
