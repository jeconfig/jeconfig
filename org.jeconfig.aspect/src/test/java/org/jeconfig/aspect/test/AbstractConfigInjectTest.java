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

package org.jeconfig.aspect.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jeconfig.api.ConfigServiceAccessor;
import org.jeconfig.api.ConfigService;
import org.jeconfig.api.ConfigSetupService;
import org.jeconfig.api.persister.ConfigPersistenceService;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.ScopePropertyProvider;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.aspect.internal.Activator;
import org.jeconfig.client.internal.ConfigServiceImpl;
import org.jeconfig.client.internal.autosave.ConfigAutoSaveServiceImpl;
import org.jeconfig.server.ConfigPersistenceServiceImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractConfigInjectTest {
	private static ConfigService configService;
	private static ConfigPersistenceService configPersistenceService;
	private static ConfigSetupService configSetupService;
	private static InjectTestConfigPersister persister;

	@BeforeClass
	public static void setUpClass() {

		final ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
		Activator.getInstance().setConfigService(configServiceImpl);

		final ConfigAutoSaveServiceImpl cass = new ConfigAutoSaveServiceImpl();
		cass.bindConfigService(configServiceImpl);
		Activator.getInstance().setConfigAutoSaveService(cass);
		configSetupService = configServiceImpl;
		configService = new ConfigServiceAccessor(configServiceImpl);
		configPersistenceService = new ConfigPersistenceServiceImpl();

		configSetupService.getScopeRegistry().addScopePropertyProvider(new ScopePropertyProvider() {
			@Override
			public String getScopeName() {
				return UserScopeDescriptor.NAME;
			}

			@Override
			public Map<String, String> getProperties(final Class<?> configClass) {
				final Map<String, String> result = new HashMap<String, String>();
				result.put(UserScopeDescriptor.PROP_USER_NAME, "hugo"); //$NON-NLS-1$
				return result;
			}
		});

		persister = new InjectTestConfigPersister();
		configPersistenceService.addConfigPersister(persister);
		configServiceImpl.bindConfigPersister(configPersistenceService);
	}

	@AfterClass
	public static void tearDownClass() {
		if (configPersistenceService != null) {
			configPersistenceService.removeConfigPersister(persister);
		}
		configService = null;
		configPersistenceService = null;
	}

	@After
	public void tearDown() {
		TestConfigExceptionHandler.setLoadFailed(false);
		TestConfigExceptionHandler.setSaveFailed(false);
		TestConfigExceptionHandler.setRefreshFailed(false);
		persister.setShouldFailLoad(false);
		persister.setShouldFailSave(false);
		configService.deleteAllOccurences(ClassScopeDescriptor.NAME, Collections.<String, String> emptyMap());
	}

	public ConfigServiceAccessor getConfigService() {
		return new ConfigServiceAccessor(configService);
	}

	public ConfigPersistenceService getConfigSetupService() {
		return configPersistenceService;
	}

	public static InjectTestConfigPersister getPersister() {
		return persister;
	}
}
