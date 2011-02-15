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

package org.jeconfig.client.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.ComplexMapTestConfiguration;
import org.jeconfig.client.annotation.set.ComplexSetPropertyTestConfiguration;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.junit.Test;

public class ProxyUsageTest extends AbstractConfigServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnComplex() {
		final ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		config.setProperty(new ComplexSubtype());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnList() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		config.setComplex(new ArrayList<ComplexSubtype>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnListAdd() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final List<ComplexSubtype> list = getConfigService().createList();
		config.setComplex(list);
		config.getComplex().add(new ComplexSubtype());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnSet() {
		final ComplexSetPropertyTestConfiguration config = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		config.setSubTypeSet(new HashSet<ComplexSubtype>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnSetAdd() {
		final ComplexSetPropertyTestConfiguration config = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		final Set<ComplexSubtype> set = getConfigService().createSet();
		config.setSubTypeSet(set);
		config.getSubTypeSet().add(new ComplexSubtype());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnMap() {
		final ComplexMapTestConfiguration config = getConfigService().load(ComplexMapTestConfiguration.class);
		config.setProperty(new HashMap<String, ComplexSubtype>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionOnMapPut() {
		final ComplexMapTestConfiguration config = getConfigService().load(ComplexMapTestConfiguration.class);
		final Map<String, ComplexSubtype> map = getConfigService().createMap();
		config.setProperty(map);
		config.getProperty().put("asdf", new ComplexSubtype()); //$NON-NLS-1$
	}

}
