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

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.jeconfig.api.persister.IPersisterSelector;
import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.common.scope.ScopeImpl;
import org.jeconfig.common.scope.ScopePathImpl;
import org.junit.Test;

public class DevelopmentModePersisterSelectorTest {

	@Test
	public void testReturnSinglePersister() {
		final IPersisterSelector selector = new DevelopmentModePersisterSelector(new DefaultPersisterSelector(), false);
		final String persisterId = selector.getPersisterId(null, Arrays.asList("test")); //$NON-NLS-1$

		Assert.assertEquals("test", persisterId); //$NON-NLS-1$
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionIfNoPersister() {
		final IPersisterSelector selector = new DevelopmentModePersisterSelector(new DefaultPersisterSelector(), false);
		selector.getPersisterId(null, Collections.<String> emptyList());
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionIfTwoPersister() {
		final IPersisterSelector selector = new DevelopmentModePersisterSelector(new DefaultPersisterSelector(), false);
		selector.getPersisterId(null, Arrays.asList("test", "test2")); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Test(expected = IllegalStateException.class)
	public void testExceptionIfNoPersisterAndDevMode() {
		final DevelopmentModePersisterSelector selector = new DevelopmentModePersisterSelector(
			new DefaultPersisterSelector(),
			false);
		selector.setDevelopmentMode(true);
		selector.getPersisterId(null, Collections.<String> emptyList());
	}

	@Test
	public void testReturnInMemoryPersisterForDevMode() {
		final IScope userScope = new ScopeImpl(UserScopeDescriptor.NAME, Collections.<String, String> emptyMap());
		final IScopePath scopePath = new ScopePathImpl(Arrays.asList(userScope));

		final DevelopmentModePersisterSelector selector = new DevelopmentModePersisterSelector(
			new DefaultPersisterSelector(),
			false);
		selector.setDevelopmentMode(true);
		final String persisterId = selector.getPersisterId(scopePath, Arrays.asList("test")); //$NON-NLS-1$

		Assert.assertEquals(InMemoryPersister.ID, persisterId);
	}

	@Test
	public void testReturnNormalPersisterForDevModeIfNoUserScope() {
		final DevelopmentModePersisterSelector selector = new DevelopmentModePersisterSelector(
			new DefaultPersisterSelector(),
			false);
		selector.setDevelopmentMode(true);
		final String persisterId = selector.getPersisterId(null, Arrays.asList("test")); //$NON-NLS-1$

		Assert.assertEquals("test", persisterId); //$NON-NLS-1$
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionIfNullParent() {
		new DevelopmentModePersisterSelector(null, true);
	}

}
