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

import junit.framework.Assert;

import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.client.testconfigs.DummyStalenessNotifier;
import org.jeconfig.client.testconfigs.StalenessNotifierTestConfig;
import org.junit.Test;

public class StalenessNotifierTest extends AbstractConfigServiceTest {

	@Override
	public void tearDown() {
		DummyStalenessNotifier.setMergingResult(null);
		super.tearDown();
	}

	@Test
	public void testNonStaleConfig() {
		getConfigService().load(StalenessNotifierTestConfig.class);
		Assert.assertNull(DummyStalenessNotifier.getMergingResult());
	}

	@Test
	public void testStaleConfigParentOverwrites() {
		final StalenessNotifierTestConfig childConfig = getConfigService().load(StalenessNotifierTestConfig.class);
		childConfig.setNumber2(1);
		getConfigService().save(childConfig);
		final ScopePath scopePath = getConfigService().getScopePathBuilderFactory(StalenessNotifierTestConfig.class).annotatedPathUntil(
				GlobalScopeDescriptor.NAME).create();
		final StalenessNotifierTestConfig parentConfig = getConfigService().load(StalenessNotifierTestConfig.class, scopePath);
		parentConfig.setNumber2(2);
		getConfigService().save(parentConfig);

		Assert.assertNull(DummyStalenessNotifier.getMergingResult());

		getConfigService().load(StalenessNotifierTestConfig.class);

		Assert.assertNotNull(DummyStalenessNotifier.getMergingResult());
		Assert.assertEquals(1, DummyStalenessNotifier.getMergingResult().getNumberOfDiscardedProperties());
		Assert.assertEquals(0, DummyStalenessNotifier.getMergingResult().getNumberOfMergedProperties());
	}

	@Test
	public void testStaleConfigMerge() {
		final StalenessNotifierTestConfig childConfig = getConfigService().load(StalenessNotifierTestConfig.class);
		childConfig.setNumber1(1);
		getConfigService().save(childConfig);
		final ScopePath scopePath = getConfigService().getScopePathBuilderFactory(StalenessNotifierTestConfig.class).annotatedPathUntil(
				GlobalScopeDescriptor.NAME).create();
		final StalenessNotifierTestConfig parentConfig = getConfigService().load(StalenessNotifierTestConfig.class, scopePath);
		parentConfig.setNumber1(2);
		getConfigService().save(parentConfig);

		Assert.assertNull(DummyStalenessNotifier.getMergingResult());

		getConfigService().load(StalenessNotifierTestConfig.class);

		Assert.assertNotNull(DummyStalenessNotifier.getMergingResult());
		Assert.assertEquals(0, DummyStalenessNotifier.getMergingResult().getNumberOfDiscardedProperties());
		Assert.assertEquals(1, DummyStalenessNotifier.getMergingResult().getNumberOfMergedProperties());
	}

}
