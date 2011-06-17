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
package org.jeconfig.server.persister;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilder;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("nls")
public class InMemoryPersisterTest {

	private static final Map<String, String> EMPTY_MAP = Collections.<String, String> emptyMap();
	//CHECKSTYLE:OFF
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	//CHECKSTYLE:ON

	private InMemoryPersister persister;
	private ComplexConfigDTO testConfig;

	@Before
	public void setUp() {
		persister = new InMemoryPersister();
		testConfig = createTestConfigDTO(createDummyScope("a"));
	}

	@Test
	public void testSave() {
		persister.saveConfiguration(testConfig);

		final ComplexConfigDTO result = persister.loadConfiguration(testConfig.getDefiningScopePath());
		assertEquals(testConfig, result);
	}

	@Test
	public void testExceptionOnSaveExisting() {
		expectedException.expect(StaleConfigException.class);
		expectedException.expectMessage("Can't save a configuration which already exists.");
		persister.saveConfiguration(testConfig);
		persister.saveConfiguration(testConfig);
	}

	@Test
	public void testUpdate() {
		persister.saveConfiguration(testConfig);
		final ComplexConfigDTO dto = createTestConfigDTO(testConfig.getDefiningScopePath());
		dto.setVersion(2);
		persister.updateConfiguration(dto);
		final ComplexConfigDTO result = persister.loadConfiguration(testConfig.getDefiningScopePath());
		assertEquals(dto, result);
	}

	@Test
	public void testExceptionOnUpdateNonExisting() {
		expectedException.expect(StaleConfigException.class);
		expectedException.expectMessage("The configuration to update doesnt exist");
		persister.updateConfiguration(testConfig);
	}

	@Test
	public void testExceptionOnUpdateOlderVersion() {
		expectedException.expect(StaleConfigException.class);
		expectedException.expectMessage("The configuration to update is not newer than the existing one");
		testConfig.setVersion(2);
		persister.saveConfiguration(testConfig);
		final ComplexConfigDTO dto = createTestConfigDTO(testConfig.getDefiningScopePath());
		dto.setVersion(1);
		persister.updateConfiguration(dto);
	}

	@Test
	public void testGetId() {
		assertEquals(InMemoryPersister.ID, persister.getId());
	}

	@Test
	public void testListScopes() {
		Collection<IScopePath> scopes = persister.listScopes("a", EMPTY_MAP);
		assertEquals(0, scopes.size());

		persister.saveConfiguration(testConfig);
		scopes = persister.listScopes("a", EMPTY_MAP);

		assertEquals(testConfig.getDefiningScopePath(), scopes.iterator().next());
	}

	@Test
	public void testDeleteAllOccurences() {
		persister.saveConfiguration(testConfig);
		final ComplexConfigDTO dto = createTestConfigDTO(createDummyScope("b"));
		persister.saveConfiguration(dto);
		persister.deleteAllOccurences("a", EMPTY_MAP);
		final Collection<IScopePath> scopes = persister.listScopes("b", EMPTY_MAP);
		assertEquals(dto.getDefiningScopePath(), scopes.iterator().next());
		assertEquals(0, persister.listScopes("a", EMPTY_MAP).size());
	}

	@Test
	public void testDeleteScopePath() {
		persister.saveConfiguration(testConfig);
		final ComplexConfigDTO dto = createTestConfigDTO(createDoubleDummyScope("a", "b"));
		persister.saveConfiguration(dto);
		persister.delete(testConfig.getDefiningScopePath(), false);
		assertEquals(dto.getDefiningScopePath(), persister.listScopes("a", EMPTY_MAP).iterator().next());
	}

	@Test
	public void testDeleteScopePathWithChildren() {
		persister.saveConfiguration(testConfig);
		final ComplexConfigDTO dto = createTestConfigDTO(createDoubleDummyScope("a", "b"));
		persister.saveConfiguration(dto);
		persister.delete(testConfig.getDefiningScopePath(), true);
		assertTrue(persister.listScopes("a", EMPTY_MAP).isEmpty());
	}

	private ComplexConfigDTO createTestConfigDTO(final IScopePath path) {
		final ComplexConfigDTO configuration = new ComplexConfigDTO();
		configuration.setPolymorph(false);
		configuration.setDefiningScopePath(path);
		configuration.setVersion(1);
		configuration.setClassVersion(1);
		configuration.setNulled(false);
		return configuration;
	}

	private IScopePath createDoubleDummyScope(final String userScope, final String userScope2) {
		final IScopePathBuilder scopePathBuilder = new InternalScopePathBuilderFactory().createBuilder();
		appendDefaultScopes(scopePathBuilder);
		scopePathBuilder.append(userScope);
		scopePathBuilder.append(userScope2);
		return scopePathBuilder.create();
	}

	private IScopePath createDummyScope(final String userScope) {
		final IScopePathBuilder scopePathBuilder = new InternalScopePathBuilderFactory().createBuilder();
		appendDefaultScopes(scopePathBuilder);
		scopePathBuilder.append(userScope);
		return scopePathBuilder.create();
	}

	private void appendDefaultScopes(final IScopePathBuilder builder) {
		final Map<String, String> classProps = new HashMap<String, String>();
		classProps.put(ClassScopeDescriptor.PROP_CLASS_NAME, "class.name");
		builder.append(ClassScopeDescriptor.NAME, classProps);
		builder.append(CodeDefaultScopeDescriptor.NAME);
	}
}
