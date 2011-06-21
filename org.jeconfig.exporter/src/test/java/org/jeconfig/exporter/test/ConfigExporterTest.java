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

package org.jeconfig.exporter.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.exporter.ConfigExporter;
import org.jeconfig.exporter.test.configs.ComplexType;
import org.jeconfig.exporter.test.configs.ComplexTypeTestConfiguration;
import org.jeconfig.exporter.test.configs.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class ConfigExporterTest extends AbstractConfigExporterTest {

	@Test
	public void testImportNonExistingConfig() {
		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		exporter.importConfig(TestConfiguration.class, factory.annotatedPath().create());
		final ScopePath exportScope = getExportScope(factory.annotatedPath().create(), TestConfiguration.class);
		final ComplexConfigDTO resultDTO = getConfigPersistenceService().loadConfiguration(exportScope);
		Assert.assertNull(resultDTO);
	}

	@Test
	public void testExportConfig() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ScopePath scope = factory.annotatedPath().create();
		final TestConfiguration config = getConfigService().load(TestConfiguration.class, scope);
		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());
		final Map<String, Double> map = config.getMap();
		map.put("test", Double.valueOf(6)); //$NON-NLS-1$
		final List<Integer> list = config.getList();
		list.add(Integer.valueOf(7124));
		getConfigService().save(config);

		exporter.exportConfig(config.getClass(), scope);

		final ScopePath exportScope = getExportScope(scope, TestConfiguration.class);
		final TestConfiguration result = getConfigService().load(TestConfiguration.class, exportScope);
		Assert.assertEquals(config.getS(), result.getS());
		Assert.assertEquals(config.getMap(), result.getMap());
		Assert.assertEquals(config.getF(), result.getF());
		Assert.assertEquals(config.getList(), result.getList());
		Assert.assertEquals(config.getI(), result.getI());
	}

	@Test
	public void testExportNonExistingConfig() {
		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ScopePath scope = factory.annotatedPath().create();

		getConfigService().delete(scope, true);
		exporter.exportConfig(TestConfiguration.class, scope);

		final ScopePath exportScope = getExportScope(scope, TestConfiguration.class);
		final ComplexConfigDTO resultDTO = getConfigPersistenceService().loadConfiguration(exportScope);
		Assert.assertNull(resultDTO);
	}

	@Test
	public void testImportConfig() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(TestConfiguration.class);
		final ScopePath scope = factory.annotatedPath().create();
		final TestConfiguration config = getConfigService().load(TestConfiguration.class, scope);
		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());
		getConfigService().save(config);
		exporter.exportConfig(TestConfiguration.class, scope);
		exporter.importConfig(TestConfiguration.class, scope);
		final ScopePath exportScope = getExportScope(scope, TestConfiguration.class);
		final TestConfiguration result = getConfigService().load(TestConfiguration.class, exportScope);
		Assert.assertEquals(config.getS(), result.getS());
		Assert.assertEquals(config.getMap(), result.getMap());
	}

	@Test
	public void testExportComplexTypeConfig() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ComplexTypeTestConfiguration.class);
		final ComplexTypeTestConfiguration config = getConfigService().load(
				ComplexTypeTestConfiguration.class,
				factory.annotatedPath().create());
		config.setName("dfg"); //$NON-NLS-1$
		config.setComplexType(ComplexType.create(getConfigService(), 0, "asd")); //$NON-NLS-1$
		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());
		getConfigService().save(config);
		final ScopePath exportScopePath = getExportScope(factory.annotatedPath().create(), ComplexTypeTestConfiguration.class);
		exporter.exportConfig(ComplexTypeTestConfiguration.class, factory.annotatedPath().create());

		final ComplexTypeTestConfiguration result = getConfigService().load(ComplexTypeTestConfiguration.class, exportScopePath);
		Assert.assertEquals(config.getName(), result.getName());
		Assert.assertEquals(config.getComplexType().getName(), result.getComplexType().getName());
		Assert.assertEquals(config.getComplexType().getId(), result.getComplexType().getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExportWithExportScope() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ComplexTypeTestConfiguration.class);
		final ComplexTypeTestConfiguration config = getConfigService().load(
				ComplexTypeTestConfiguration.class,
				factory.annotatedPath().create());
		config.setName("dfg"); //$NON-NLS-1$
		config.setComplexType(ComplexType.create(getConfigService(), 1, "1")); //$NON-NLS-1$
		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());
		getConfigService().save(config);
		final ScopePath exportScopePath = getExportScope(factory.annotatedPath().create(), ComplexTypeTestConfiguration.class);
		exporter.exportConfig(ComplexTypeTestConfiguration.class, exportScopePath);
	}

	@Test
	public void testExportCollection() {
		final ScopePathBuilderFactory factory = getConfigService().getScopePathBuilderFactory(ComplexTypeTestConfiguration.class);
		final ScopePath userScopePath = factory.annotatedPath().create();
		final ScopePath globalScopePath = factory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();

		final ComplexTypeTestConfiguration config2 = getConfigService().load(ComplexTypeTestConfiguration.class, globalScopePath);
		config2.setName("vscv"); //$NON-NLS-1$
		config2.setComplexType(ComplexType.create(getConfigService(), 0, "vscv")); //$NON-NLS-1$
		getConfigService().save(config2);

		final ComplexTypeTestConfiguration config = getConfigService().load(ComplexTypeTestConfiguration.class, userScopePath);
		config.setName("tsdf"); //$NON-NLS-1$
		config.setComplexType(ComplexType.create(getConfigService(), 0, "name")); //$NON-NLS-1$
		getConfigService().save(config);

		final ConfigExporter exporter = new ConfigExporter(getConfigPersistenceService());

		final Collection<ScopePath> scopeList = new LinkedList<ScopePath>();
		scopeList.add(userScopePath);
		scopeList.add(globalScopePath);

		exporter.exportConfig(scopeList);
		final ScopePath exportScopeUserPath = getExportScope(userScopePath, ComplexTypeTestConfiguration.class);
		final ScopePath exportScopeGlobalPath = getExportScope(globalScopePath, ComplexTypeTestConfiguration.class);

		final ComplexTypeTestConfiguration resultUser = getConfigService().load(
				ComplexTypeTestConfiguration.class,
				exportScopeUserPath);
		final ComplexTypeTestConfiguration resultGlobal = getConfigService().load(
				ComplexTypeTestConfiguration.class,
				exportScopeGlobalPath);

		Assert.assertEquals(config.getName(), resultUser.getName());
		Assert.assertEquals(config.getComplexType().getId(), resultUser.getComplexType().getId());
		Assert.assertEquals(config2.getComplexType().getId(), resultGlobal.getComplexType().getId());
		Assert.assertEquals(config.getComplexType().getName(), resultUser.getComplexType().getName());
		Assert.assertEquals(config2.getComplexType().getName(), resultGlobal.getComplexType().getName());
	}
}
