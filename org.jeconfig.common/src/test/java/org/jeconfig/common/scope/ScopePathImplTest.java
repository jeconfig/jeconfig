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
package org.jeconfig.common.scope;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jeconfig.api.scope.Scope;
import org.jeconfig.api.scope.ScopePath;
import org.junit.Test;

public class ScopePathImplTest {

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnNullScopeList() {
		new ScopePathImpl(null);
	}

	@Test
	public void testGetScopes() {
		final ScopePath testScopePath = createTestScopePath();
		Assert.assertEquals(2, testScopePath.getScopes().size());
	}

	@Test
	public void testGetRootScope() {
		final ScopePath testScopePath = createTestScopePath();
		Assert.assertEquals("test", testScopePath.getRootScope().getName()); //$NON-NLS-1$
	}

	@Test
	public void testGetParentScopePath() {
		final ScopePath testScopePath = createTestScopePath();
		Assert.assertEquals("test", testScopePath.getParentPath().getRootScope().getName()); //$NON-NLS-1$
	}

	@Test
	public void testFindScopeByName() {
		final ScopePath testScopePath = createTestScopePath();
		Assert.assertEquals("value", testScopePath.findScopeByName("test").getProperty("test")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testGetLastScope() {
		final ScopePath testScopePath = createTestScopePath();
		Assert.assertEquals("test2", testScopePath.getLastScope().getName()); //$NON-NLS-1$
	}

	@Test
	public void testStartsPathWith() {
		final ScopePath testScopePath = createTestScopePath();
		final ScopePath parentPath = testScopePath.getParentPath();
		Assert.assertTrue(testScopePath.startsPathWith(parentPath));
	}

	private ScopePath createTestScopePath() {
		final Map<String, String> props = new HashMap<String, String>();
		props.put("test", "value"); //$NON-NLS-1$//$NON-NLS-2$
		final Scope scope = new ScopeImpl("test", props); //$NON-NLS-1$
		final Scope scope2 = new ScopeImpl("test2", Collections.<String, String> emptyMap()); //$NON-NLS-1$
		return new ScopePathImpl(Arrays.asList(scope, scope2));
	}
}
