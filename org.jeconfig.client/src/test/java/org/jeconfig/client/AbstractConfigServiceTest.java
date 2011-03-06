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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jeconfig.api.ConfigServiceAccessor;
import org.jeconfig.api.IConfigSetupService;
import org.jeconfig.api.persister.IConfigPersistenceService;
import org.jeconfig.api.persister.IConfigPersister;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.IScopePropertyProvider;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.internal.ConfigServiceImpl;
import org.jeconfig.client.internal.conversion.DefaultConverterFactory;
import org.jeconfig.client.internal.conversion.SimpleTypeConverterRegistryImpl;
import org.jeconfig.server.ConfigPersistenceServiceImpl;
import org.jeconfig.server.marshalling.XStreamXmlMarshaller;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Initializes a configuration service for test purposes.
 */
public abstract class AbstractConfigServiceTest {
	private static final SimpleTypeConverterRegistryImpl REGISTRY = new SimpleTypeConverterRegistryImpl();
	private final XStreamXmlMarshaller marshaller = new XStreamXmlMarshaller();

	private ConfigServiceAccessor configService;
	private IConfigPersister persister;
	private IConfigPersistenceService configPersistenceService;
	private IConfigSetupService configSetupService;

	public SimpleTypeConverterRegistryImpl getRegistry() {
		return REGISTRY;
	}

	public XStreamXmlMarshaller getMarshaller() {
		return marshaller;
	}

	@BeforeClass
	public static void setUpClass() {
		new DefaultConverterFactory().createConverters(REGISTRY);
	}

	@Before
	public void setUp() {
		final ConfigServiceImpl configServiceImpl = new ConfigServiceImpl();
		configSetupService = configServiceImpl;
		configService = new ConfigServiceAccessor(configServiceImpl);

		configPersistenceService = new ConfigPersistenceServiceImpl();

		configServiceImpl.getScopeRegistry().addScopePropertyProvider(new IScopePropertyProvider() {
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
		configServiceImpl.getScopeRegistry().addScopePropertyProvider(new IScopePropertyProvider() {

			@Override
			public String getScopeName() {
				return InstanceScopeDescriptor.NAME;
			}

			@Override
			public Map<String, String> getProperties(final Class<?> configClass) {
				final Map<String, String> result = new HashMap<String, String>();
				result.put(InstanceScopeDescriptor.PROP_INSTANCE_NAME, "test"); //$NON-NLS-1$
				return result;
			}
		});
		persister = createPersister();
		configPersistenceService.addConfigPersister(persister);
		configServiceImpl.bindConfigPersister(configPersistenceService);
	}

	@After
	public void tearDown() {
		configPersistenceService.deleteAllOccurences(ClassScopeDescriptor.NAME, Collections.<String, String> emptyMap());
		configPersistenceService.removeConfigPersister(persister);
	}

	protected IConfigPersister createPersister() {
		return new CountingDummyPersister();
	}

	public ConfigServiceAccessor getConfigService() {
		return configService;
	}

	public IConfigPersistenceService getConfigPersistenceService() {
		return configPersistenceService;
	}

	public CountingDummyPersister getCountingDummyPersister() {
		if (persister instanceof CountingDummyPersister) {
			return (CountingDummyPersister) persister;
		}
		return null;
	}

	public IConfigPersister getPersister() {
		return persister;
	}

	public IConfigSetupService getConfigSetupService() {
		return configSetupService;
	}
}
