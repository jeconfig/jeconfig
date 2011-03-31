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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jeconfig.api.scope.IScope;
import org.junit.Test;

public class ScopeImplTest {

	@Test(expected = IllegalArgumentException.class)
	public void testThrowExceptionForNullScopeName() {
		new ScopeImpl(null, Collections.<String, String> emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowExceptionForEmptyScopeName() {
		new ScopeImpl("", Collections.<String, String> emptyMap()); //$NON-NLS-1$
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowExceptionForNullProperties() {
		new ScopeImpl("test", null); //$NON-NLS-1$
	}

	@Test
	public void testGetScopeName() {
		final IScope scope = new ScopeImpl("test", Collections.<String, String> emptyMap()); //$NON-NLS-1$
		Assert.assertEquals("test", scope.getName()); //$NON-NLS-1$
	}

	@Test
	public void testGetProperty() {
		final Map<String, String> props = new HashMap<String, String>();
		props.put("test", "value"); //$NON-NLS-1$//$NON-NLS-2$
		final IScope scope = new ScopeImpl("test", props); //$NON-NLS-1$
		Assert.assertEquals("value", scope.getProperty("test")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testEqualsIncludesPropertiesAndName() {
		final Map<String, String> props = new HashMap<String, String>();
		props.put("test", "value"); //$NON-NLS-1$//$NON-NLS-2$
		final IScope scope = new ScopeImpl("test", props); //$NON-NLS-1$
		final IScope notEqualScope1 = new ScopeImpl("test", Collections.<String, String> emptyMap()); //$NON-NLS-1$
		final IScope notEqualScope2 = new ScopeImpl("test2", props); //$NON-NLS-1$
		final IScope equalScope = new ScopeImpl("test", props); //$NON-NLS-1$

		Assert.assertEquals(scope, equalScope);
		Assert.assertFalse(scope.equals(notEqualScope1));
		Assert.assertFalse(scope.equals(notEqualScope2));
	}

	@Test
	public void testContainsAllProperties() {
		final Map<String, String> props = new HashMap<String, String>();
		props.put("test", "value"); //$NON-NLS-1$//$NON-NLS-2$
		final Map<String, String> props2 = new HashMap<String, String>();
		props2.put("asdf", "value"); //$NON-NLS-1$//$NON-NLS-2$
		final IScope scope = new ScopeImpl("test", props); //$NON-NLS-1$

		Assert.assertTrue(scope.containsAllProperties(props));
		Assert.assertFalse(scope.containsAllProperties(props2));
	}
}
