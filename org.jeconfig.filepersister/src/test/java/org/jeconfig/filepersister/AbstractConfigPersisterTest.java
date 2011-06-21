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

package org.jeconfig.filepersister;

import java.util.HashMap;
import java.util.Map;

import org.jeconfig.api.ConfigService;
import org.jeconfig.api.ConfigSetupService;
import org.jeconfig.api.persister.ConfigPersistenceService;
import org.jeconfig.api.scope.ScopePropertyProvider;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.internal.ConfigServiceImpl;
import org.jeconfig.server.ConfigPersistenceServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractConfigPersisterTest {
	private static ConfigService configService;
	private static ConfigPersistenceService configPersistenceService;
	private static ScopePropertyProvider userScopeProvider;
	private static ScopePropertyProvider testScopeProvider;
	private static Map<String, String> userScopeProperties;
	private static Map<String, String> testScopeProperties;
	private static ConfigSetupService configSetupService;

	@BeforeClass
	public static void setUpClass() {
		userScopeProperties = new HashMap<String, String>();

		final ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
		configService = configServiceImpl;

		configPersistenceService = new ConfigPersistenceServiceImpl();

		configSetupService = configServiceImpl;

		userScopeProvider = new ScopePropertyProvider() {
			@Override
			public String getScopeName() {
				return UserScopeDescriptor.NAME;
			}

			@Override
			public Map<String, String> getProperties(final Class<?> configClass) {
				userScopeProperties = new HashMap<String, String>();
				userScopeProperties.put(UserScopeDescriptor.PROP_USER_NAME, "hugo"); //$NON-NLS-1$
				return userScopeProperties;
			}
		};

		testScopeProvider = new ScopePropertyProvider() {

			@Override
			public String getScopeName() {
				return "test"; //$NON-NLS-1$
			}

			@Override
			public Map<String, String> getProperties(final Class<?> configClass) {
				testScopeProperties = new HashMap<String, String>();
				testScopeProperties.put("Test", "Data"); //$NON-NLS-1$ //$NON-NLS-2$
				testScopeProperties.put("Port", "5000"); //$NON-NLS-1$ //$NON-NLS-2$
				testScopeProperties.put("user", "lukas"); //$NON-NLS-1$ //$NON-NLS-2$
				return testScopeProperties;
			}
		};

		configSetupService.getScopeRegistry().addScopePropertyProvider(userScopeProvider);
		configSetupService.getScopeRegistry().addScopeDescriptor(new TestScopeDescriptor());
		configSetupService.getScopeRegistry().addScopePropertyProvider(testScopeProvider);

	}

	@AfterClass
	public static void tearDownClass() {
		if (configSetupService != null) {
			configSetupService.getScopeRegistry().removeScopePropertyProvider(userScopeProvider);
			configSetupService.getScopeRegistry().removeScopePropertyProvider(testScopeProvider);
			configSetupService = null;
			configService = null;
		}
		if (configPersistenceService != null) {
			configPersistenceService = null;
		}
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public ConfigPersistenceService getConfigPersistenceService() {
		return configPersistenceService;
	}

	public static Map<String, String> getUserScopeProperties() {
		return userScopeProperties;
	}

	public static Map<String, String> getTestScopeProperties() {
		return testScopeProperties;
	}
}
