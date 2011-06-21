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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;
import org.junit.Test;

public class ScopePathBuilderTest {

	@Test(expected = IllegalArgumentException.class)
	public void testDuplicateScopeInPath() {
		final String scope1 = ClassScopeDescriptor.NAME;
		final String scope2 = CodeDefaultScopeDescriptor.NAME;
		final String scope3 = GlobalScopeDescriptor.NAME;
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(ClassScopeDescriptor.PROP_CLASS_NAME, "value"); //$NON-NLS-1$
		final InternalScopePathBuilderFactory builderFactory = new InternalScopePathBuilderFactory();
		final ScopePath scopePath = builderFactory.createBuilder().append(scope1, properties).append(scope2).append(scope3).append(
				scope3).create();
		scopePath.toString();
	}

	@Test
	public void testAppend() {
		final String scope1 = ClassScopeDescriptor.NAME;
		final String scope2 = CodeDefaultScopeDescriptor.NAME;
		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(ClassScopeDescriptor.PROP_CLASS_NAME, "value"); //$NON-NLS-1$

		final InternalScopePathBuilderFactory builderFactory = new InternalScopePathBuilderFactory();
		final ScopePath scopePath = builderFactory.createBuilder().append(scope1, properties).append(scope2).create();
		Assert.assertEquals(2, scopePath.getScopes().size());
		Assert.assertEquals(scope1, scopePath.getRootScope().getName());
		Assert.assertEquals(scope2, scopePath.getLastScope().getName());
		Assert.assertEquals("value", scopePath.getRootScope().getProperty(ClassScopeDescriptor.PROP_CLASS_NAME)); //$NON-NLS-1$
		Assert.assertEquals(0, scopePath.getLastScope().getProperties().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithoutAppend() {
		// creating a new scope path without scopes is not allowed
		final InternalScopePathBuilderFactory builderFactory = new InternalScopePathBuilderFactory();
		builderFactory.createBuilder().create();
	}
}
