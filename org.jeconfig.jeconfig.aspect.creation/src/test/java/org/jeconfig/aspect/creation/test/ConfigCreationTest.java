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

package org.jeconfig.aspect.creation.test;

import org.jeconfig.client.proxy.IConfigProxy;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigCreationTest extends AbstractConfigCreationTest {

	@Test
	public void testSimpleComplexTypeCreation() {
		final CreationTestConfiguration config = new CreationTestConfiguration();
		Assert.assertNull(config.getField());
		Assert.assertTrue(IConfigProxy.class.isAssignableFrom(config.getClass()));
	}

	@Test
	public void testSimpleComplexTypeCreationWithArg() {
		final CreationTestConfiguration config = new CreationTestConfiguration("adsf");
		Assert.assertEquals("adsf", config.getField());
		Assert.assertTrue(IConfigProxy.class.isAssignableFrom(config.getClass()));
	}

	@Test
	public void testNonConfigComplexTypeCreation() {
		final CreationTextNoConfiguration object = new CreationTextNoConfiguration();
		Assert.assertNotNull(object);
		Assert.assertFalse(IConfigProxy.class.isAssignableFrom(object.getClass()));
	}

	@Test
	public void testCreationOfComplexRootConfig() {
		final TestConfig config = new TestConfig();
		Assert.assertNotNull(config);
		Assert.assertTrue(config instanceof IConfigProxy);
	}
}
