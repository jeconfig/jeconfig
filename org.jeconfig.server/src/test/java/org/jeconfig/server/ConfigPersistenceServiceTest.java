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
package org.jeconfig.server;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.persister.ConfigPersister;
import org.jeconfig.api.persister.PersisterSelector;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("nls")
public class ConfigPersistenceServiceTest {

	//CHECKSTYLE:OFF
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	//CHECKSTYLE:ON

	private ConfigPersistenceServiceImpl persistenceService;
	private ConfigPersister persister;
	private ConfigPersister persister2;
	private PersisterSelector persisterSelector;
	private ComplexConfigDTO dto1;
	private ComplexConfigDTO dto2;
	private ComplexConfigDTO dto3;

	@Before
	public void setUp() {
		persistenceService = new ConfigPersistenceServiceImpl();
		persister = createMock(ConfigPersister.class);
		persister2 = createMock(ConfigPersister.class);
		persisterSelector = createMock(PersisterSelector.class);

		dto1 = new ComplexConfigDTO();
		dto1.setDefiningScopePath(createDummyScope("a"));
		dto2 = new ComplexConfigDTO();
		dto2.setDefiningScopePath(createDummyScope("a"));
		dto2.setVersion(2L);
		dto3 = new ComplexConfigDTO();
		dto3.setDefiningScopePath(createDummyScope("b"));
	}

	@Test
	public void testRemoveConfigPersister() {
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("DefaultPersisterSelector needs exactly one configuration persister! got: 0");
		expect(persister.getId()).andReturn("1").times(4);

		replay(persister);

		persistenceService.addConfigPersister(persister);
		persistenceService.removeConfigPersister(persister);
		persistenceService.loadConfiguration(createDummyScope("a"));
	}

	@Test
	public void testUpdateWithCacheRemoveFromCacheOnStaleConfig() {
		expectedException.expect(StaleConfigException.class);
		expectedException.expectMessage("a");
		expect(persister.getId()).andReturn("1").times(2);
		persister.updateConfiguration(eq(dto1));
		expectLastCall().andThrow(new StaleConfigException(dto1.getDefiningScopePath(), "a"));

		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		replay(persister);

		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);

		persistenceService.updateConfiguration(dto1);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
	}

	@Test
	public void testUpdateWithCache() {
		expect(persister.getId()).andReturn("1").times(2);
		persister.updateConfiguration(eq(dto1));
		expectLastCall();
		replay(persister);

		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);

		persistenceService.updateConfiguration(dto1);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
	}

	@Test
	public void testUpdateWithoutCache() {
		expect(persister.getId()).andReturn("1").times(2);
		persister.updateConfiguration(eq(dto1));
		expectLastCall();
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		replay(persister);

		persistenceService.setCacheEnabled(false);
		persistenceService.setConfigPersister(persister);

		persistenceService.updateConfiguration(dto1);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
	}

	@Test
	public void testSaveWithCacheRemoveFromCacheOnStaleConfig() {
		expectedException.expect(StaleConfigException.class);
		expectedException.expectMessage("a");
		expect(persister.getId()).andReturn("1").times(2);
		persister.saveConfiguration(eq(dto1));
		expectLastCall().andThrow(new StaleConfigException(dto1.getDefiningScopePath(), "a"));

		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		replay(persister);

		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);

		persistenceService.saveConfiguration(dto1);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
	}

	@Test
	public void testSaveWithCache() {
		expect(persister.getId()).andReturn("1").times(2);
		persister.saveConfiguration(eq(dto1));
		expectLastCall();
		replay(persister);

		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);

		persistenceService.saveConfiguration(dto1);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
	}

	@Test
	public void testSaveWithoutCache() {
		expect(persister.getId()).andReturn("1").times(2);
		persister.saveConfiguration(eq(dto1));
		expectLastCall();
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		replay(persister);

		persistenceService.setCacheEnabled(false);
		persistenceService.setConfigPersister(persister);

		persistenceService.saveConfiguration(dto1);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
	}

	@Test
	public void testNullId() {
		Assert.assertNull(persistenceService.getId());
	}

	@Test
	public void testListScopes() {
		expect(persister.getId()).andReturn("1").times(2);
		expect(persister.listScopes("a", Collections.<String, String> emptyMap())).andReturn(Arrays.asList(createDummyScope("a")));

		replay(persister);
		persistenceService.setConfigPersister(persister);
		final Collection<ScopePath> scopes = persistenceService.listScopes("a", Collections.<String, String> emptyMap());
		Assert.assertEquals(createDummyScope("a"), scopes.iterator().next());
	}

	@Test
	public void testDeleteAllOccurences() {
		expect(persister.getId()).andReturn("1").times(2);
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		persister.deleteAllOccurences(eq("a"), eq(Collections.<String, String> emptyMap()));
		expectLastCall();
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);

		replay(persister);
		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);
		persistenceService.loadConfiguration(createDummyScope("a"));
		persistenceService.deleteAllOccurences("a", Collections.<String, String> emptyMap());
		persistenceService.loadConfiguration(createDummyScope("a")); //cache should be empty
	}

	@Test
	public void testDeleteWithChildren() {
		expect(persister.getId()).andReturn("1").times(2);
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		persister.delete(eq(createDummyScope("a")), eq(true));
		expectLastCall();
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);

		replay(persister);
		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);
		persistenceService.loadConfiguration(createDummyScope("a"));
		persistenceService.delete(createDummyScope("a"), true);
		persistenceService.loadConfiguration(createDummyScope("a")); //cache should be empty
	}

	@Test
	public void testDelete() {
		expect(persister.getId()).andReturn("1").times(2);
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		persister.delete(eq(createDummyScope("a")), eq(false));
		expectLastCall();
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);

		replay(persister);
		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);
		persistenceService.loadConfiguration(createDummyScope("a"));
		persistenceService.delete(createDummyScope("a"), false);
		persistenceService.loadConfiguration(createDummyScope("a")); //cache should be empty
	}

	@Test
	public void testLoadWithCaching() {
		expect(persister.getId()).andReturn("1").times(2);
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		replay(persister);

		persistenceService.setCacheEnabled(true);
		persistenceService.setConfigPersister(persister);
		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
		final ComplexConfigDTO loaded2 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded2);
	}

	@Test
	public void testLoadWithoutCaching() {
		expect(persister.getId()).andReturn("1").times(2);
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto1);
		expect(persister.loadConfiguration(createDummyScope("a"))).andReturn(dto2);
		replay(persister);

		persistenceService.setCacheEnabled(false);
		persistenceService.setConfigPersister(persister);

		final ComplexConfigDTO loaded1 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto1, loaded1);
		final ComplexConfigDTO loaded2 = persistenceService.loadConfiguration(createDummyScope("a"));
		Assert.assertEquals(dto2, loaded2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetPersisterSelector() {
		final ScopePath dummyScope = createDummyScope("blub");

		expect(persister.getId()).andReturn("1").times(2);
		expect(persister2.getId()).andReturn("2").times(2);
		expect(persisterSelector.getPersisterId((ScopePath) anyObject(), (Collection<String>) anyObject())).andReturn("1");
		expect(persister.loadConfiguration(dummyScope)).andReturn(null);
		replay(persister, persister2, persisterSelector);

		persistenceService.setPersisterSelector(persisterSelector);
		persistenceService.setConfigPersisters(new ConfigPersister[] {persister, persister2});

		persistenceService.loadConfiguration(dummyScope);
		verify(persister, persister2, persisterSelector);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnsetPersisterSelector() {
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("DefaultPersisterSelector needs exactly one configuration persister! got: 2");
		final ScopePath dummyScope = createDummyScope("blub");

		expect(persister.getId()).andReturn("1").times(2);
		expect(persister2.getId()).andReturn("2").times(2);
		expect(persisterSelector.getPersisterId((ScopePath) anyObject(), (Collection<String>) anyObject())).andReturn("1");
		expect(persister.loadConfiguration(dummyScope)).andReturn(null);
		replay(persister, persister2, persisterSelector);

		persistenceService.setPersisterSelector(persisterSelector);
		persistenceService.setConfigPersisters(new ConfigPersister[] {persister, persister2});
		persistenceService.unsetPersisterSelector(persisterSelector);

		persistenceService.loadConfiguration(dummyScope);
		verify(persister, persister2, persisterSelector);
	}

	private ScopePath createDummyScope(final String userScope) {
		final ScopePathBuilder scopePathBuilder = new InternalScopePathBuilderFactory().createBuilder();
		appendDefaultScopes(scopePathBuilder);
		scopePathBuilder.append(userScope);
		return scopePathBuilder.create();
	}

	private void appendDefaultScopes(final ScopePathBuilder builder) {
		final Map<String, String> classProps = new HashMap<String, String>();
		classProps.put(ClassScopeDescriptor.PROP_CLASS_NAME, "class.name");
		builder.append(ClassScopeDescriptor.NAME, classProps);
		builder.append(CodeDefaultScopeDescriptor.NAME);
	}
}
