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

package org.jeconfig.dbpersister;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.h2.jdbcx.JdbcDataSource;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.exception.StoreConfigException;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.server.marshalling.XStreamXmlMarshaller;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("nls")
public class DbConfigPersisterTest extends AbstractConfigPersisterTest {

	private static final Logger LOG = LoggerFactory.getLogger(DbConfigPersisterTest.class);
	private DbConfigPersister persister;
	private JdbcDataSource dataSource;
	private Connection con = null;
	private Statement stmt = null;
	private final String configTableName = "ConfigData";
	private final String scopePathColumnName = "ScopePath";
	private final String configColumnName = "ConfigName";
	private final String configVersionColumnName = "configVersion";

	// TODO create test for exeption when using nested connection
	@Before
	public void setUp() throws SQLException {
		final String createTable = "CREATE TABLE "
			+ configTableName
			+ " ("
			+ scopePathColumnName
			+ " VARCHAR(2000) NOT NULL, "
			+ configVersionColumnName
			+ " NUMBER(19) NOT NULL, "
			+ configColumnName
			+ " LONGVARBINARY NOT NULL)"; //$NON-NLS-1$
		final String addPrimaryKey = "ALTER TABLE " + configTableName + " ADD PRIMARY KEY (" + scopePathColumnName + ")";
		//$NON-NLS-1$

		dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:testdb;MODE=Oracle;LOCK_TIMEOUT=2000");
		dataSource.setUser("SA");
		dataSource.setPassword("");

		con = dataSource.getConnection();
		stmt = con.createStatement();
		stmt.execute(createTable);
		stmt.execute(addPrimaryKey);
		final XStreamXmlMarshaller marshaller = new XStreamXmlMarshaller();
		persister = new DbConfigPersister(
			configTableName,
			scopePathColumnName,
			configVersionColumnName,
			configColumnName,
			marshaller,
			dataSource);
	}

	@After
	public void tearDown() throws SQLException {
		final String deleteOldTable = "DROP TABLE " + configTableName;
		try {
			stmt.execute(deleteOldTable);
		} catch (final SQLException e) {
			LOG.info("Couldn't delete test db table.", e);
		}
		if (stmt != null) {
			stmt.close();
		}
		if (con != null) {
			con.close();
		}
		if (dataSource != null) {
			dataSource = null;
		}
	}

	@Test
	public void testListScopesWithProperties() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO config1 = createTestConfigDTO(factory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create());
		final ComplexConfigDTO config2 = createTestConfigDTO(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		final ComplexConfigDTO config3 = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(config1);
		persister.saveConfiguration(config2);
		persister.saveConfiguration(config3);

		final String scopeName = "user";
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put("userName", "hugo");

		final Collection<ScopePath> scopePaths = persister.listScopes(scopeName, properties);
		for (final ScopePath scopePath : scopePaths) {
			Assert.assertTrue((scopePath.findScopeByName(ClassScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(CodeDefaultScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(DefaultScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(GlobalScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(UserScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(UserScopeDescriptor.NAME).containsAllProperties(properties)));
		}
		Assert.assertTrue(scopePaths.size() == 1);
	}

	@Test
	public void testListScopesEmptyProperties() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO config1 = createTestConfigDTO(factory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create());
		persister.saveConfiguration(config1);
		final ComplexConfigDTO config2 = createTestConfigDTO(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		persister.saveConfiguration(config2);
		final ComplexConfigDTO config3 = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(config3);

		final Collection<ScopePath> scopePaths = persister.listScopes("global", new HashMap<String, String>());

		for (final ScopePath scopePath : scopePaths) {
			final Map<String, String> properties = new HashMap<String, String>();
			properties.put("className", TestConfiguration.class.getName()); //$NON-NLS-1$
			Assert.assertTrue((scopePath.findScopeByName(ClassScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(CodeDefaultScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(DefaultScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(GlobalScopeDescriptor.NAME) != null));
		}
		Assert.assertTrue(scopePaths.size() == 2);
	}

	@Test
	public void testDeleteAllOccurencesOneConfigToDelete() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO config1 = createTestConfigDTO(factory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create());
		persister.saveConfiguration(config1);
		final ComplexConfigDTO config2 = createTestConfigDTO(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		persister.saveConfiguration(config2);
		final ComplexConfigDTO config3 = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(config3);

		final String scopeName = "user";
		final Map<String, String> map = new HashMap<String, String>();
		map.put("userName", "hugo");
		persister.deleteAllOccurences(scopeName, map);

		Assert.assertNull(persister.loadConfiguration(factory.annotatedPath().create()));
		Assert.assertEquals(
				config1,
				persister.loadConfiguration(factory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create()));
		Assert.assertEquals(config2, persister.loadConfiguration(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create()));
	}

	@Test
	public void testDeleteAllOccurences() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO config1 = createTestConfigDTO(factory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create());
		persister.saveConfiguration(config1);
		final ComplexConfigDTO config2 = createTestConfigDTO(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		persister.saveConfiguration(config2);
		final ComplexConfigDTO config3 = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(config3);

		persister.deleteAllOccurences("global", new HashMap<String, String>());

		Assert.assertNull(persister.loadConfiguration(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create()));
		Assert.assertNull(persister.loadConfiguration(factory.annotatedPath().create()));
		Assert.assertEquals(
				config1,
				persister.loadConfiguration(factory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create()));
	}

	@Test
	public void testDeleteConfigurationDeleteChildrenFalse() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO parentConfig = createTestConfigDTO(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		persister.saveConfiguration(parentConfig);
		final ComplexConfigDTO childConfig = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(childConfig);

		persister.delete(factory.annotatedPath().create(), false);
		final ComplexConfigDTO result = persister.loadConfiguration(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		Assert.assertEquals(parentConfig, result);

		Assert.assertNull(persister.loadConfiguration(factory.annotatedPath().create()));
	}

	@Test
	public void testDeleteConfigurationDeleteChildrenTrue() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO parentConfig = createTestConfigDTO(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create());
		persister.saveConfiguration(parentConfig);
		final ComplexConfigDTO childConfig = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(childConfig);

		persister.delete(parentConfig.getDefiningScopePath(), true);
		Assert.assertNull(persister.loadConfiguration(factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create()));
		Assert.assertNull(persister.loadConfiguration(factory.annotatedPath().create()));
	}

	@Test
	public void testSaveConfiguration() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(configuration);
	}

	@Test(expected = StaleConfigException.class)
	public void testSaveConfigurationAlreadyExists() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(configuration);
		persister.saveConfiguration(configuration);
	}

	@Test(expected = StoreConfigException.class)
	public void testSaveConfigurationIllegalVersion() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		configuration.setVersion(0);
		persister.saveConfiguration(configuration);
	}

	@Test
	public void testLoadConfigurationNotExists() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO result = persister.loadConfiguration(factory.annotatedPath().create());
		Assert.assertNull(result);
	}

	@Test
	public void testLoadConfiguration() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(configuration);

		final ComplexConfigDTO result = persister.loadConfiguration(configuration.getDefiningScopePath());
		Assert.assertEquals(configuration, result);
	}

	@Test(expected = StaleConfigException.class)
	public void testUpdateConfigurationNotExists() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		persister.updateConfiguration(configuration);
	}

	@Test
	public void testUpdateConfiguration() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(configuration);
		final ConfigSimpleValueDTO simpleValueProperty = configuration.getSimpleValueProperty("field2");
		simpleValueProperty.setValue("23423");
		final long version = configuration.getVersion();
		configuration.setVersion(version + 1);
		configuration.addSimpleValueProperty(simpleValueProperty);
		persister.updateConfiguration(configuration);

		final ComplexConfigDTO result = persister.loadConfiguration(factory.annotatedPath().create());
		Assert.assertEquals(configuration, result);
	}

	@Test(expected = StaleConfigException.class)
	public void testUpdateConfigurationSameVersion() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ComplexConfigDTO configuration = createTestConfigDTO(factory.annotatedPath().create());
		persister.saveConfiguration(configuration);
		persister.updateConfiguration(configuration);
	}

	private ComplexConfigDTO createTestConfigDTO(final ScopePath path) {
		final ComplexConfigDTO configuration = new ComplexConfigDTO();
		configuration.setPolymorph(false);
		configuration.addSimpleValueProperty(new ConfigSimpleValueDTO("Integer", "field2", path, "123"));
		configuration.setDefiningScopePath(path);
		configuration.setVersion(1);
		configuration.setNulled(false);
		return configuration;
	}
}
