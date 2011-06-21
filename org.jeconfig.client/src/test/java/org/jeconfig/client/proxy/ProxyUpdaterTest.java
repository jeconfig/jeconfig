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

import java.util.List;
import java.util.Map;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.ComplexMapTestConfiguration;
import org.jeconfig.client.annotation.map.MapPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.ComplexSetPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.SetPropertyTestConfiguration;
import org.jeconfig.client.annotation.simple.SimplePropertyTestConfiguration;
import org.jeconfig.client.proxy.ConfigListDecorator;
import org.jeconfig.client.proxy.ConfigMapDecorator;
import org.jeconfig.client.proxy.ConfigSetDecorator;
import org.jeconfig.client.proxy.ConfigProxy;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"nls", "unchecked"})
public class ProxyUpdaterTest extends AbstractConfigServiceTest {

	@Test
	public void testUpdateComplexMapProperty() {
		final ComplexMapTestConfiguration config = getConfigService().load(ComplexMapTestConfiguration.class);
		final Map<String, ComplexSubtype> map = getConfigService().createMap();
		map.put("a", ComplexSubtype.create(getConfigService(), "b", "c"));
		config.setProperty(map);
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDto = proxy.getLeafConfigDTO();
		final ConfigMapDTO mapDto = rootDto.getMapProperty("property");
		final ComplexConfigDTO complexDto = (ComplexConfigDTO) mapDto.getMap().get("a");
		Assert.assertNotNull(complexDto);
		final ConfigSimpleValueDTO idSimpleValueProperty = complexDto.getSimpleValueProperty("id");
		Assert.assertEquals("b", idSimpleValueProperty.getValue());
		final ConfigSimpleValueDTO nameSimpleValueDTO = complexDto.getSimpleValueProperty("name");
		Assert.assertEquals("c", nameSimpleValueDTO.getValue());
		final ConfigMapDecorator<String, ComplexSubtype> decorator = (ConfigMapDecorator<String, ComplexSubtype>) config.getProperty();
		Assert.assertEquals(mapDto, decorator.getLeafConfigDTO());
	}

	@Test
	public void testUpdateSimpleMapProperty() {
		final MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		config.getUserID().put("a", Integer.valueOf(1));
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDto = proxy.getLeafConfigDTO();
		final ConfigMapDTO mapDto = rootDto.getMapProperty("userID");
		final ConfigSimpleValueDTO simpleDto = (ConfigSimpleValueDTO) mapDto.getMap().get("a");
		Assert.assertEquals(Integer.toString(1), simpleDto.getValue());
		Assert.assertTrue(config.getUserID() instanceof ConfigMapDecorator);
		final ConfigMapDecorator<String, Integer> decorator = (ConfigMapDecorator<String, Integer>) config.getUserID();
		Assert.assertEquals(mapDto, decorator.getLeafConfigDTO());
	}

	@Test
	public void testUpdateComplexListProperty() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final List<ComplexSubtype> list = getConfigService().createList();
		list.add(ComplexSubtype.create(getConfigService(), "a", "b"));
		config.setComplex(list);
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDto = proxy.getLeafConfigDTO();
		final ConfigListDTO listDto = rootDto.getListProperty("complex");
		final ComplexConfigDTO complexDto = (ComplexConfigDTO) listDto.getItems().get(0);
		Assert.assertNotNull(complexDto);
		final ConfigSimpleValueDTO idSimpleValueProperty = complexDto.getSimpleValueProperty("id");
		Assert.assertEquals("a", idSimpleValueProperty.getValue());
		final ConfigSimpleValueDTO nameSimpleValueDTO = complexDto.getSimpleValueProperty("name");
		Assert.assertEquals("b", nameSimpleValueDTO.getValue());
		final ConfigListDecorator<ComplexSubtype> decorator = (ConfigListDecorator<ComplexSubtype>) config.getComplex();
		Assert.assertEquals(listDto, decorator.getLeafConfigDTO());
	}

	@Test
	public void testUpdateSimpleProperty() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setIntValue(5);
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		Assert.assertTrue(proxy.getLeafConfigDTO().getDeclaredProperties().contains("intValue"));
	}

	@Test
	public void testUpdateComplexProperty() {
		final ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		config.setProperty(ComplexSubtype.create(getConfigService(), "a", "b"));
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDTO = proxy.getLeafConfigDTO();
		final ComplexConfigDTO complexDto = rootDTO.getComplexProperty("property");
		Assert.assertNotNull(complexDto);
		final ConfigSimpleValueDTO idSimpleValueProperty = complexDto.getSimpleValueProperty("id");
		Assert.assertEquals("a", idSimpleValueProperty.getValue());
		final ConfigSimpleValueDTO nameSimpleValueDTO = complexDto.getSimpleValueProperty("name");
		Assert.assertEquals("b", nameSimpleValueDTO.getValue());
		Assert.assertTrue(config.getProperty() instanceof ConfigProxy);
		final ComplexSubtype subtype = config.getProperty();
		final ConfigProxy<ComplexConfigDTO> subProxy = (ConfigProxy<ComplexConfigDTO>) subtype;
		final ComplexConfigDTO subDTO = subProxy.getLeafConfigDTO();
		Assert.assertEquals(complexDto, subDTO);
	}

	@Test
	public void testUpdateSimpleSetProperty() {
		final SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		config.getData().add("a");
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDTO = proxy.getLeafConfigDTO();
		final ConfigSetDTO setDto = rootDTO.getSetProperty("data");
		Assert.assertNotNull(setDto);
		final ConfigSimpleValueDTO simpleValueDto = (ConfigSimpleValueDTO) setDto.getItems().iterator().next();
		Assert.assertEquals("a", simpleValueDto.getValue());
		final ConfigSetDecorator<String> decorator = (ConfigSetDecorator<String>) config.getData();
		Assert.assertEquals(setDto, decorator.getLeafConfigDTO());
	}

	@Test
	public void testUpdateComplexSetProperty() {
		final ComplexSetPropertyTestConfiguration config = getConfigService().load(ComplexSetPropertyTestConfiguration.class);
		config.getSubTypeSet().add(ComplexSubtype.create(getConfigService(), "a", "b"));
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDTO = proxy.getLeafConfigDTO();
		final ConfigSetDTO setDto = rootDTO.getSetProperty("subTypeSet");
		Assert.assertNotNull(setDto);
		final ComplexConfigDTO complexDto = (ComplexConfigDTO) setDto.getItems().iterator().next();
		Assert.assertNotNull(complexDto);
		final ConfigSimpleValueDTO idSimpleValueProperty = complexDto.getSimpleValueProperty("id");
		Assert.assertEquals("a", idSimpleValueProperty.getValue());
		final ConfigSimpleValueDTO nameSimpleValueDTO = complexDto.getSimpleValueProperty("name");
		Assert.assertEquals("b", nameSimpleValueDTO.getValue());
		Assert.assertTrue(config.getSubTypeSet() instanceof ConfigSetDecorator);
		final ConfigSetDecorator<ComplexSubtype> setDecorator = (ConfigSetDecorator<ComplexSubtype>) config.getSubTypeSet();
		Assert.assertEquals(setDto, setDecorator.getLeafConfigDTO());
		final ComplexSubtype subconfig = config.getSubTypeSet().iterator().next();
		final ConfigProxy<ComplexConfigDTO> subProxy = (ConfigProxy<ComplexConfigDTO>) subconfig;
		Assert.assertEquals(complexDto, subProxy.getLeafConfigDTO());
	}

	@Test
	public void testUpdateSimpleListProperty() {
		final ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		config.getStringField().add("a");
		getConfigService().save(config);
		final ConfigProxy<ComplexConfigDTO> proxy = (ConfigProxy<ComplexConfigDTO>) config;
		final ComplexConfigDTO rootDTO = proxy.getLeafConfigDTO();
		final ConfigListDTO listDto = rootDTO.getListProperty("stringField");
		Assert.assertNotNull(listDto);
		final ConfigSimpleValueDTO simpleValueDTO = (ConfigSimpleValueDTO) listDto.getItems().get(0);
		Assert.assertEquals("a", simpleValueDTO.getValue());
		Assert.assertTrue(config.getStringField() instanceof ConfigListDecorator);
		final ConfigListDecorator<String> decorator = (ConfigListDecorator<String>) config.getStringField();
		Assert.assertEquals(listDto, decorator.getLeafConfigDTO());
	}
}
