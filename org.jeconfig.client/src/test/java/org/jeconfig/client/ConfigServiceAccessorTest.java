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

import org.jeconfig.api.ConfigServiceAccessor;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.LoadFailureSolutionStrategy;
import org.jeconfig.api.exception.RefreshFailureSolutionStrategy;
import org.jeconfig.api.exception.SaveFailureSolutionStrategy;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.persister.IConfigPersister;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.internal.autosave.ConfigAutoSaveServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigServiceAccessorTest extends AbstractConfigServiceTest {

	private ConfigAutoSaveServiceImpl autoSaveService;
	private ConfigServiceAccessor configServiceAccessor;
	private ConfigServiceAccessorTestExceptionHandler exceptionHandler;
	private ConfigServiceAccessorTestPersister persister;

	@Override
	protected IConfigPersister createPersister() {
		return new ConfigServiceAccessorTestPersister();
	}

	@Override
	public ConfigServiceAccessorTestPersister getPersister() {
		return (ConfigServiceAccessorTestPersister) super.getPersister();
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		getConfigPersistenceService().setCacheEnabled(false);
		exceptionHandler = new ConfigServiceAccessorTestExceptionHandler();
		autoSaveService = new ConfigAutoSaveServiceImpl();
		persister = getPersister();
		autoSaveService.bindConfigService(getConfigService());
		configServiceAccessor = new ConfigServiceAccessor(getConfigService(), exceptionHandler);
	}

	@Test(expected = StaleConfigException.class)
	public void testLoadFailed() {
		persister.setShouldFailLoad(true);
		configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		getConfigPersistenceService().setCacheEnabled(true);
	}

	@Test(expected = StaleConfigException.class)
	public void testLoadFailedRetryLoad() {
		persister.setShouldFailLoad(true);
		exceptionHandler.setLoadStrategy(LoadFailureSolutionStrategy.RETRY);
		try {
			configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		} catch (final StaleConfigException e) {
			Assert.assertTrue(persister.getLoadCount() == 7);
			throw e;
		}
	}

	@Test
	public void testLoadFailedOverwriteWithParent() {
		exceptionHandler.setLoadStrategy(LoadFailureSolutionStrategy.OVERWRITE_STALE_CONFIG_WITH_PARENT);
		final IScopePathBuilderFactory factory = configServiceAccessor.getScopePathBuilderFactory(ConfigServiceAccessorTestConfiguration.class);

		final ConfigServiceAccessorTestConfiguration parentConfig = configServiceAccessor.load(
				ConfigServiceAccessorTestConfiguration.class,
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		parentConfig.setField2(Integer.valueOf(67));
		configServiceAccessor.save(parentConfig);

		final ConfigServiceAccessorTestConfiguration childConfig = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		childConfig.setField2(Integer.valueOf(66));
		configServiceAccessor.save(childConfig);

		// workaround to clear configuration service cache (without it persister#load() would not be invoked):
		final ComplexConfigDTO parentDTO = getPersister().loadConfiguration(
				factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final ComplexConfigDTO childDTO = getPersister().loadConfiguration(factory.annotatedPath().create());
		getConfigService().deleteAllOccurences(ConfigServiceAccessorTestConfiguration.class);
		getPersister().saveConfiguration(parentDTO);
		getPersister().saveConfiguration(childDTO);

		persister.setShouldFailLoadOnce(true);
		final ConfigServiceAccessorTestConfiguration resultConfig = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		Assert.assertEquals(parentConfig, resultConfig);
	}

	@Test(expected = StaleConfigException.class)
	public void testSaveFailed() {
		persister.setShouldFailSave(true);
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		config.setField1("xcvsdf"); //$NON-NLS-1$
		configServiceAccessor.save(config);
	}

	@Test(expected = StaleConfigException.class)
	public void testSaveFailedRetrySave() {
		persister.setShouldFailSave(true);
		exceptionHandler.setSaveStrategy(SaveFailureSolutionStrategy.RETRY);
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		config.setField1("sdfsdf"); //$NON-NLS-1$
		try {
			configServiceAccessor.save(config);
		} catch (final StaleConfigException e) {
			Assert.assertTrue(persister.getSaveCount() == 7);
			throw e;
		}
	}

	@Test
	public void testIgnoreSaveFailed() {
		persister.setShouldFailSave(true);
		exceptionHandler.setSaveStrategy(SaveFailureSolutionStrategy.IGNORE);
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		config.setField1("sdfzuf"); //$NON-NLS-1$
		configServiceAccessor.save(config);
	}

	@Test
	public void testSaveFailedRefreshConfig() {
		persister.setShouldFailSave(true);
		exceptionHandler.setSaveStrategy(SaveFailureSolutionStrategy.REFRESH_CONFIG);
		final ConfigServiceAccessorTestConfiguration configOld = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		final ConfigServiceAccessorTestConfiguration configNew = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		configNew.setField1("testv1234"); //$NON-NLS-1$
		configServiceAccessor.save(configNew);

		Assert.assertEquals(configNew, configOld);
	}

	@Test(expected = StaleConfigException.class)
	public void testUpdateFailed() {
		persister.setShouldFailUpdate(true);
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		config.setField2(Integer.valueOf(34));
		configServiceAccessor.save(config);
		config.setField2(Integer.valueOf(43));
		configServiceAccessor.save(config);
	}

	@Test(expected = StaleConfigException.class)
	public void testRefreshFailed() {
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		persister.setShouldFailLoad(true);
		configServiceAccessor.refresh(config);
	}

	@Test
	public void testIgnoreRefreshFailed() {
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		persister.setShouldFailLoad(true);
		exceptionHandler.setRefreshStrategy(RefreshFailureSolutionStrategy.IGNORE);
		configServiceAccessor.refresh(config);
	}

	@Test(expected = StaleConfigException.class)
	public void testRefereshFailedRetryRefresh() {
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		persister.setShouldFailLoad(true);
		exceptionHandler.setRefreshStrategy(RefreshFailureSolutionStrategy.RETRY);
		try {
			configServiceAccessor.refresh(config);
		} catch (final StaleConfigException e) {
			Assert.assertTrue(persister.getLoadCount() == 9);
			throw e;
		}
	}

	@Test
	public void testCreateSet() {
		final ConfigServiceAccessorTestConfiguration2 config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration2.class);
		config.setSet(configServiceAccessor.createSet());
		configServiceAccessor.save(config);
	}

	@Test
	public void testCreateList() {
		final ConfigServiceAccessorTestConfiguration2 config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration2.class);
		config.setList(configServiceAccessor.createList());
		configServiceAccessor.save(config);
	}

	@Test
	public void testCreateMap() {
		final ConfigServiceAccessorTestConfiguration2 config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration2.class);
		config.setMap(configServiceAccessor.createMap());
		configServiceAccessor.save(config);
	}

	@Test
	public void testLoadWithInstanceName() {
		final ConfigServiceAccessorTestConfiguration2 config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration2.class);
		config.setMap(configServiceAccessor.createMap());
		configServiceAccessor.save(config);

		final ConfigServiceAccessorTestConfiguration2 result = configServiceAccessor.load(
				ConfigServiceAccessorTestConfiguration2.class,
				"test"); //$NON-NLS-1$
		Assert.assertEquals(config, result);
	}

	@Test
	public void testConvenienceDelete() {
		final ConfigServiceAccessorTestConfiguration config = configServiceAccessor.load(ConfigServiceAccessorTestConfiguration.class);
		final IScopePathBuilderFactory factory = configServiceAccessor.getScopePathBuilderFactory(ConfigServiceAccessorTestConfiguration.class);
		config.setField1("ttttt"); //$NON-NLS-1$
		getConfigService().save(config);

		configServiceAccessor.delete(ConfigServiceAccessorTestConfiguration.class);
		final ComplexConfigDTO result = getPersister().loadConfiguration(factory.annotatedPath().create());
		Assert.assertNull(result);
	}
}
