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

import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.ComplexMapTestConfiguration;
import org.jeconfig.client.annotation.set.ComplexSetPropertyTestConfiguration;
import org.jeconfig.client.proxy.IConfigProxy;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.junit.Test;

@SuppressWarnings("nls")
public class ConfigDTOSetterTest extends AbstractConfigServiceTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testDTOsAndParentOnMapProperty() {
		final ComplexMapTestConfiguration config = getConfigService().load(ComplexMapTestConfiguration.class);
		final Map<String, ComplexSubtype> map = getConfigService().createMap();
		final ComplexSubtype sub = getConfigService().createComplexObject(ComplexSubtype.class);
		sub.setId("a");
		sub.setName("b");
		map.put("key", sub);
		Assert.assertEquals(map, ((IConfigProxy<ComplexConfigDTO>) sub).getParentProxy());
		config.setProperty(map);
		IConfigProxy<ConfigMapDTO> mapProxy = (IConfigProxy<ConfigMapDTO>) config.getProperty();
		Assert.assertEquals(config, mapProxy.getParentProxy());
		ConfigMapDTO mapDto = mapProxy.getLeafConfigDTO();
		Assert.assertEquals(Map.class.getName(), mapDto.getPropertyType());

		getConfigService().save(config);
		mapProxy = (IConfigProxy<ConfigMapDTO>) config.getProperty();
		mapDto = mapProxy.getLeafConfigDTO();
		Assert.assertEquals(1, mapDto.getMap().size());
		final ComplexSubtype subtype = config.getProperty().get("key");
		final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) subtype;
		final ComplexConfigDTO itemDto = proxy.getLeafConfigDTO();
		final ConfigSimpleValueDTO idDto = itemDto.getSimpleValueProperty("id");
		Assert.assertEquals("a", idDto.getValue());
		final ConfigSimpleValueDTO nameDto = itemDto.getSimpleValueProperty("name");
		Assert.assertEquals("b", nameDto.getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDTOsAndParentOnListProperty() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final List<ComplexSubtype> list = getConfigService().createList();
		final ComplexSubtype sub = getConfigService().createComplexObject(ComplexSubtype.class);
		sub.setId("a");
		sub.setName("b");
		list.add(sub);
		Assert.assertEquals(list, ((IConfigProxy<ComplexConfigDTO>) sub).getParentProxy());
		config.setComplex(list);
		IConfigProxy<ConfigListDTO> listProxy = (IConfigProxy<ConfigListDTO>) config.getComplex();
		Assert.assertEquals(config, listProxy.getParentProxy());
		ConfigListDTO listDto = listProxy.getLeafConfigDTO();
		Assert.assertEquals(List.class.getName(), listDto.getPropertyType());

		getConfigService().save(config);
		listProxy = (IConfigProxy<ConfigListDTO>) config.getComplex();
		listDto = listProxy.getLeafConfigDTO();
		Assert.assertEquals(1, listDto.getItems().size());
		final ComplexSubtype subtype = config.getComplex().get(0);
		final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) subtype;
		final ComplexConfigDTO itemDto = proxy.getLeafConfigDTO();
		Assert.assertNull(itemDto);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDTOsAndParentOnSetProperty() {
		final ComplexSetPropertyTestConfiguration config = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		final Set<ComplexSubtype> set = getConfigService().createSet();
		final ComplexSubtype sub = getConfigService().createComplexObject(ComplexSubtype.class);
		sub.setId("a");
		sub.setName("b");
		set.add(sub);
		Assert.assertEquals(set, ((IConfigProxy<ComplexConfigDTO>) sub).getParentProxy());
		config.setSubTypeSet(set);
		final IConfigProxy<ConfigSetDTO> setProxy = (IConfigProxy<ConfigSetDTO>) config.getSubTypeSet();
		Assert.assertEquals(config, setProxy.getParentProxy());
		ConfigSetDTO setDto = setProxy.getLeafConfigDTO();
		Assert.assertEquals(Set.class.getName(), setDto.getPropertyType());

		getConfigService().save(config);
		setDto = setProxy.getLeafConfigDTO();
		Assert.assertEquals(1, setDto.getItems().size());
		final ComplexSubtype subtype = config.getSubTypeSet().iterator().next();
		final IConfigProxy<ComplexConfigDTO> proxy = (IConfigProxy<ComplexConfigDTO>) subtype;
		final ComplexConfigDTO itemDto = proxy.getLeafConfigDTO();
		final ConfigSimpleValueDTO idDto = itemDto.getSimpleValueProperty("id");
		Assert.assertEquals("a", idDto.getValue());
		final ConfigSimpleValueDTO nameDto = itemDto.getSimpleValueProperty("name");
		Assert.assertEquals("b", nameDto.getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDTOsAndParentOnComplexProperty() {
		final ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		final ComplexSubtype sub = getConfigService().createComplexObject(ComplexSubtype.class);
		sub.setId("a");
		sub.setName("b");
		config.setProperty(sub);

		final IConfigProxy<ComplexConfigDTO> subProxy = (IConfigProxy<ComplexConfigDTO>) config.getProperty();
		Assert.assertEquals(config, subProxy.getParentProxy());
		ComplexConfigDTO dto = subProxy.getLeafConfigDTO();
		Assert.assertEquals(ComplexSubtype.class.getName(), dto.getPropertyType());

		getConfigService().save(config);

		dto = subProxy.getLeafConfigDTO();
		final ConfigSimpleValueDTO idDto = dto.getSimpleValueProperty("id");
		Assert.assertEquals("a", idDto.getValue());
		final ConfigSimpleValueDTO nameDto = dto.getSimpleValueProperty("name");
		Assert.assertEquals("b", nameDto.getValue());
	}
}
