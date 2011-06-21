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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.junit.Test;

public class ScopePathBuilderTest {
	private final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnAppendNull() {
		final ScopePathBuilder scopePathBuilder = factory.createBuilder();
		scopePathBuilder.append(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnAppendNullProperties() {
		final ScopePathBuilder scopePathBuilder = factory.createBuilder();
		scopePathBuilder.append("test", null); //$NON-NLS-1$
	}

	@Test
	public void testAppendSingleScope() {
		final Map<String, String> props = new HashMap<String, String>();
		props.put("test", "value"); //$NON-NLS-1$//$NON-NLS-2$
		final ScopePathBuilder scopePathBuilder = factory.createBuilder();
		appendDefaultScopes(scopePathBuilder);

		scopePathBuilder.append("test", props); //$NON-NLS-1$
		final ScopePath scopePath = scopePathBuilder.create();

		Assert.assertEquals("value", scopePath.getLastScope().getProperty("test")); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Test
	public void testAppendMultipleScopes() {
		final ScopePathBuilder scopePathBuilder = factory.createBuilder();
		appendDefaultScopes(scopePathBuilder);
		scopePathBuilder.appendAll(new String[] {"test1", "test2"}); //$NON-NLS-1$ //$NON-NLS-2$
		scopePathBuilder.addPropertyToScope("test2", "test", "value"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		final ScopePath scopePath = scopePathBuilder.create();

		Assert.assertEquals(4, scopePath.getScopes().size());
		Assert.assertEquals("value", scopePath.getLastScope().getProperty("test")); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnAddNullProperty() {
		final ScopePathBuilder scopePathBuilder = factory.createBuilder();
		appendDefaultScopes(scopePathBuilder);
		scopePathBuilder.addPropertyToScope(CodeDefaultScopeDescriptor.NAME, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnAddPropertyToNonExistentScope() {
		final ScopePathBuilder scopePathBuilder = factory.createBuilder();
		appendDefaultScopes(scopePathBuilder);
		scopePathBuilder.addPropertyToScope("asdf", "blub", null); //$NON-NLS-1$//$NON-NLS-2$
	}

	private void appendDefaultScopes(final ScopePathBuilder builder) {
		final Map<String, String> classProps = new HashMap<String, String>();
		classProps.put(ClassScopeDescriptor.PROP_CLASS_NAME, "class.name"); //$NON-NLS-1$
		builder.append(ClassScopeDescriptor.NAME, classProps);
		builder.append(CodeDefaultScopeDescriptor.NAME);
	}
}
