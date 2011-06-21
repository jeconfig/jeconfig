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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;
import org.jeconfig.server.persister.DefaultScopePathGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultScopePathGeneratorTest {

	private DefaultScopePathGenerator gen;

	@Before
	public void setUp() throws Exception {
		gen = new DefaultScopePathGenerator(File.separator);
	}

	@Test
	public void testCreateName() {
		final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();
		final ScopePathBuilder builder = factory.createBuilder();
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		tmpProperties.put("className", "TestConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(ClassScopeDescriptor.NAME, tmpProperties);
		builder.append(CodeDefaultScopeDescriptor.NAME);

		final String result = gen.createName(builder.create());
		Assert.assertTrue(result.equals("TestConfiguration")); //$NON-NLS-1$
	}

	@Test
	public void testGetPathFromScopePath() {
		final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();
		final ScopePathBuilder builder = factory.createBuilder();
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		tmpProperties.put("className", "TestConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(ClassScopeDescriptor.NAME, tmpProperties);
		builder.append(CodeDefaultScopeDescriptor.NAME);
		builder.append("Test", tmpProperties); //$NON-NLS-1$

		final String result = gen.getPathFromScopePath(builder.create());
		Assert.assertTrue(result.equals("Test" + File.separator + "className" + DefaultScopePathGenerator.PATH_PROPERTY_SEPARATOR + "TestConfiguration")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testBuildScopeWithProperty() {
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("key", "value"); //$NON-NLS-1$ //$NON-NLS-2$

		final String result = gen.buildScopeWithProperty("test", map); //$NON-NLS-1$
		Assert.assertTrue(result.equals("test" + File.separator + "key" + DefaultScopePathGenerator.PATH_PROPERTY_SEPARATOR + "value")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Test
	public void testCreateScopePaths() {
		final Collection<String> paths = new LinkedList<String>();
		paths.add(File.separator + "test" //$NON-NLS-1$
			+ File.separator
			+ "userName-hugo" //$NON-NLS-1$
			+ File.separator
			+ "admin" + File.separator + "hf.pdf"); //$NON-NLS-1$ //$NON-NLS-2$
		paths.add(File.separator + "test" + File.separator //$NON-NLS-1$
			+ "userName-peter" + File.separator //$NON-NLS-1$
			+ "blxcvcvx" + File.separator //$NON-NLS-1$
			+ "test" + File.separator //$NON-NLS-1$
			+ "ggg.txt"); //$NON-NLS-1$
		paths.add(File.separator + "component" //$NON-NLS-1$
			+ File.separator
			+ "userName-hugo" //$NON-NLS-1$
			+ File.separator
			+ "test.xml"); //$NON-NLS-1$
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("userName", "hugo"); //$NON-NLS-1$ //$NON-NLS-2$

		final Collection<ScopePath> scopePaths = gen.createScopePaths(paths, "test", map); //$NON-NLS-1$
		for (final ScopePath scopePath : scopePaths) {
			Assert.assertTrue((scopePath.findScopeByName(ClassScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName(CodeDefaultScopeDescriptor.NAME) != null)
				&& (scopePath.findScopeByName("test") != null)); //$NON-NLS-1$
		}
	}
}
