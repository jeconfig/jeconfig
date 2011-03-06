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

package org.jeconfig.client.annotation.crossreferences;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.client.AbstractConfigServiceTest;
import org.junit.Test;

public class ConfigReferenceReadOnlyTest extends AbstractConfigServiceTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testROCrossReferenceInConfigClass() {
		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		actualConfig.getReferenceConfig().setServerIp("asdf"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testManipulateAttributesBeforeAddingNewCrossReference() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		final CrossReferenceTestConfigReferenced referenced = new CrossReferenceTestConfigReferenced();
		referenced.setServerIp("127.0.0.1"); //$NON-NLS-1$
		referenced.setServerPort("99"); //$NON-NLS-1$
		config.setReferenceConfig(referenced);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testManipulateAttributesCrossReferences() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.getReferenceConfig().setServerPort("90"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testManipulateCrossReferences() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.setReferenceConfig(new CrossReferenceTestConfigReferenced());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testManipulateAttributesAfterAddingNewCrossReference() {
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.setReferenceConfig(new CrossReferenceTestConfigReferenced());
		config.getReferenceConfig().setServerIp("test"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testROCrossReferenceInComplexProperty() {
		final CrossReferenceTestSubConfig subConfig = CrossReferenceTestSubConfig.create(getConfigService(), "id", "theName"); //$NON-NLS-1$//$NON-NLS-2$
		subConfig.setName("theName"); //$NON-NLS-1$

		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.setComplex(subConfig);
		getConfigService().save(config);

		final CrossReferenceTestConfig actualConfig = getConfigService().load(CrossReferenceTestConfig.class);
		actualConfig.getComplex().getReferencedConfig().setServerIp("asdf"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testROCrossReferenceInArrayProperty() {
		final CRArrayTestConfig config = getConfigService().load(CRArrayTestConfig.class);
		config.setSubconfigs(new CRSubConfig[] {CRSubConfig.create(getConfigService(), "id")}); //$NON-NLS-1$
		getConfigService().save(config);

		final CRArrayTestConfig actualConfig = getConfigService().load(CRArrayTestConfig.class);
		actualConfig.getSubconfigs()[0].getRef().setServerIp("asdf"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testROCrossReferenceInSetProperty() {
		final CRSetTestConfig config = getConfigService().load(CRSetTestConfig.class);
		final Set<CRSubConfig> set = getConfigService().createSet();
		set.add(CRSubConfig.create(getConfigService(), "id")); //$NON-NLS-1$
		config.setSubconfigs(set);
		getConfigService().save(config);

		final CRSetTestConfig actualConfig = getConfigService().load(CRSetTestConfig.class);
		actualConfig.getSubconfigs().iterator().next().getRef().setServerIp("asdf"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testROSetInCrossRef() {
		final CrossReferenceTestConfigReferenced refCfg = getConfigService().load(CrossReferenceTestConfigReferenced.class);
		final Set<String> set = getConfigService().createSet();
		refCfg.setSet(set);
		getConfigService().save(refCfg);
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.getReferenceConfig().getSet().add("asdf"); //$NON-NLS-1$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testROMapInCrossRef() {
		final CrossReferenceTestConfigReferenced refCfg = getConfigService().load(CrossReferenceTestConfigReferenced.class);
		final Map<String, String> map = getConfigService().createMap();
		refCfg.setMap(map);
		getConfigService().save(refCfg);
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.getReferenceConfig().getMap().put("asdf", "asfd"); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testROListInCrossRef() {
		final CrossReferenceTestConfigReferenced refCfg = getConfigService().load(CrossReferenceTestConfigReferenced.class);
		final List<String> list = getConfigService().createList();
		refCfg.setList(list);
		getConfigService().save(refCfg);
		final CrossReferenceTestConfig config = getConfigService().load(CrossReferenceTestConfig.class);
		config.getReferenceConfig().getList().add("adsf"); //$NON-NLS-1$
	}
}
