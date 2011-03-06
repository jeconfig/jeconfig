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

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.IScopePathBuilder;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;
import org.jeconfig.server.persister.DefaultScopePathGenerator;
import org.junit.Before;
import org.junit.Test;

public class DefaultScopePathGeneratorValidatorTest {

	private DefaultScopePathGenerator gen;

	@Before
	public void setUp() throws Exception {
		gen = new DefaultScopePathGenerator(File.separator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScopeWithIllegalCharactersInName() {
		final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();
		final IScopePathBuilder builder = factory.createBuilder();
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		tmpProperties.put("className", "TestConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(ClassScopeDescriptor.NAME, tmpProperties);
		builder.append(CodeDefaultScopeDescriptor.NAME);
		builder.append("++sdf#++#'*+~~+"); //$NON-NLS-1$

		gen.createName(builder.create());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScopeWithIllegalPropertyValue() {
		final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();
		final IScopePathBuilder builder = factory.createBuilder();
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		tmpProperties.put("className", "TestConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(ClassScopeDescriptor.NAME, tmpProperties);
		builder.append(CodeDefaultScopeDescriptor.NAME);

		final Map<String, String> wrongProperties = new TreeMap<String, String>();
		wrongProperties.put("testKey", "##+#c+s'd*+~~"); //$NON-NLS-1$//$NON-NLS-2$
		builder.append("test", wrongProperties); //$NON-NLS-1$

		gen.createName(builder.create());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScopeWithIllegalPropertyName() {
		final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();
		final IScopePathBuilder builder = factory.createBuilder();
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		tmpProperties.put("className", "TestConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(ClassScopeDescriptor.NAME, tmpProperties);
		builder.append(CodeDefaultScopeDescriptor.NAME);

		final Map<String, String> wrongProperties = new TreeMap<String, String>();
		wrongProperties.put("##+#c+s'd*+~~", "5"); //$NON-NLS-1$//$NON-NLS-2$
		builder.append("test", wrongProperties); //$NON-NLS-1$

		gen.createName(builder.create());
	}
}
