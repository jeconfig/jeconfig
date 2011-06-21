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

import java.util.HashMap;
import java.util.Map;

import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilderFactory;
import org.jeconfig.api.scope.InstanceScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class ConfigInjectAspectTest extends AbstractConfigInjectTest {

	@Test
	public void testSaveDirtyConfigBeforeInjection() {
		final InjectConfigUser user1 = new InjectConfigUser();
		Assert.assertEquals(0, user1.getAutoSavedConfig().getI());

		user1.getAutoSavedConfig().setI(1337);

		final InjectConfigUser user2 = new InjectConfigUser();
		Assert.assertEquals(1337, user2.getConfig().getI());
		Assert.assertEquals(1337, user2.getAutoSavedConfig().getI());
	}

	@Test
	public void testSimpleInjection() {
		final InjectConfigUser injectConfigUser = new InjectConfigUser();
		Assert.assertNotNull(injectConfigUser.getConfig());
	}

	@Test
	public void testSimpleNonInjection() {
		final InjectConfigUser injectConfigUser = new InjectConfigUser();
		Assert.assertNull(injectConfigUser.getNonAnnotatedConfig());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSetInjectedField() {
		final InjectConfigUser injectConfigUser = new InjectConfigUser();
		injectConfigUser.setConfig(null);
	}

	@Test
	public void testAnnotatedScopePath() {
		final ScopePathBuilderFactory scopePathBuilderFactory = getConfigService().getScopePathBuilderFactory(
				InjectTestConfig.class);
		final ScopePath userScopePath = scopePathBuilderFactory.annotatedPath().append(UserScopeDescriptor.NAME).create();

		final InjectTestConfig userConfig = getConfigService().load(InjectTestConfig.class, userScopePath);
		userConfig.setI(1337);
		getConfigService().save(userConfig);

		final InjectConfigUser injectConfigUser = new InjectConfigUser();
		Assert.assertEquals(userConfig, injectConfigUser.getUserConfig());
	}

	@Test
	public void testAnnotatedInstanceName() {
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(InstanceScopeDescriptor.PROP_INSTANCE_NAME, "FirstInstance"); //$NON-NLS-1$
		final ScopePathBuilderFactory scopePathBuilderFactory = getConfigService().getScopePathBuilderFactory(
				InjectTestConfig.class);
		final ScopePath instanceScopePath = scopePathBuilderFactory.annotatedPath().append(
				InstanceScopeDescriptor.NAME,
				properties).create();

		final InjectTestConfig instanceConfig = getConfigService().load(InjectTestConfig.class, instanceScopePath);
		instanceConfig.setI(1337);
		getConfigService().save(instanceConfig);

		final InjectConfigUser injectConfigUser = new InjectConfigUser();
		Assert.assertEquals(instanceConfig, injectConfigUser.getInstanceConfig());
	}

	@Test
	public void testAnnotatedExceptionHandlerOnLoad() {
		Assert.assertFalse(TestConfigExceptionHandler.isLoadFailed());
		getPersister().setShouldFailLoad(true);
		try {
			new InjectConfigUser().getConfigWithExceptionHandler();
			Assert.fail("Expected RuntimeException"); //$NON-NLS-1$
		} catch (final RuntimeException e) {
			// expected
			e.equals(e); // only to make check-style happy ;)
		}

		Assert.assertTrue(TestConfigExceptionHandler.isLoadFailed());
	}
}
