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

package org.jeconfig.client.migration;

import junit.framework.Assert;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.migration.IPropertyChanger;
import org.jeconfig.client.internal.migration.PropertyChangerImpl;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class PropertyChangerTest {
	private static final String PROP1 = "property1";
	private static final String PROP2 = "property2";
	private static final String PROP3 = "property3";
	private static final String NEW_PROP = "newProperty";

	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";
	private static final String NEW_VALUE = "newValue";

	private static final String TYPE1 = "type1";
	private static final String TYPE2 = "type2";
	private static final String NEW_TYPE = "newType";

	private IPropertyChanger propertyChanger;
	private ComplexConfigDTO rootDto;
	private ComplexConfigDTO complexDto1;
	private ComplexConfigDTO complexDto2;

	@Before
	public void setUp() {
		rootDto = new ComplexConfigDTO();
		rootDto.setPropertyType(TYPE1);
		rootDto.addSimpleValueProperty(createSimpleDto(PROP1, VALUE1));

		complexDto1 = new ComplexConfigDTO();
		complexDto1.setPropertyType(TYPE1);
		complexDto1.setPropertyName(PROP2);
		complexDto1.addSimpleValueProperty(createSimpleDto(PROP1, VALUE2));
		rootDto.addComplexProperty(complexDto1);

		complexDto2 = new ComplexConfigDTO();
		complexDto2.setPropertyType(TYPE2);
		complexDto2.setPropertyName(PROP3);
		complexDto2.addSimpleValueProperty(createSimpleDto(PROP1, VALUE2));
		rootDto.addComplexProperty(complexDto2);

		propertyChanger = new PropertyChangerImpl(rootDto);
	}

	private ConfigSimpleValueDTO createSimpleDto(final String name, final String value) {
		final ConfigSimpleValueDTO result = new ConfigSimpleValueDTO();
		result.setPropertyName(name);
		result.setValue(value);
		return result;
	}

	@Test
	public void testRename() {
		final ConfigSimpleValueDTO simpleValueDto = rootDto.getSimpleValueProperty(PROP1);
		propertyChanger.renameProperty(rootDto, PROP1, NEW_PROP);

		Assert.assertNull(rootDto.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(rootDto.getSimpleValueProperty(NEW_PROP));
		Assert.assertSame(simpleValueDto, rootDto.getSimpleValueProperty(NEW_PROP));
	}

	@Test
	public void testRenameRecursively() {
		Assert.assertNotNull(rootDto.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(complexDto1.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(complexDto2.getSimpleValueProperty(PROP1));

		propertyChanger.renamePropertyRecursively(TYPE1, PROP1, NEW_PROP);

		Assert.assertNull(rootDto.getSimpleValueProperty(PROP1));
		Assert.assertNull(complexDto1.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(complexDto2.getSimpleValueProperty(PROP1));

		Assert.assertNotNull(rootDto.getSimpleValueProperty(NEW_PROP));
		Assert.assertNotNull(complexDto1.getSimpleValueProperty(NEW_PROP));
		Assert.assertNull(complexDto2.getSimpleValueProperty(NEW_PROP));
	}

	@Test
	public void testDelete() {
		Assert.assertNotNull(rootDto.getSimpleValueProperty(PROP1));
		propertyChanger.deleteProperty(rootDto, PROP1);

		Assert.assertNull(rootDto.getSimpleValueProperty(PROP1));
	}

	@Test
	public void testDeleteRecursively() {
		Assert.assertNotNull(rootDto.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(complexDto1.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(complexDto2.getSimpleValueProperty(PROP1));

		propertyChanger.deletePropertyRecursively(TYPE1, PROP1);

		Assert.assertNull(rootDto.getSimpleValueProperty(PROP1));
		Assert.assertNull(complexDto1.getSimpleValueProperty(PROP1));
		Assert.assertNotNull(complexDto2.getSimpleValueProperty(PROP1));
	}

	@Test
	public void testAdd() {
		Assert.assertNull(rootDto.getSimpleValueProperty(NEW_PROP));
		propertyChanger.addSimpleProperty(rootDto, NEW_PROP, NEW_VALUE, NEW_TYPE);

		final ConfigSimpleValueDTO simpleValueProperty = rootDto.getSimpleValueProperty(NEW_PROP);
		Assert.assertNotNull(simpleValueProperty);
		Assert.assertEquals(NEW_VALUE, simpleValueProperty.getValue());
		Assert.assertEquals(NEW_TYPE, simpleValueProperty.getPropertyType());
	}

	@Test
	public void testAddRecursively() {
		Assert.assertNull(rootDto.getSimpleValueProperty(NEW_PROP));
		Assert.assertNull(complexDto1.getSimpleValueProperty(NEW_PROP));
		Assert.assertNull(complexDto2.getSimpleValueProperty(NEW_PROP));

		propertyChanger.addSimplePropertyRecursively(TYPE1, NEW_PROP, NEW_VALUE, NEW_TYPE);

		Assert.assertNotNull(rootDto.getSimpleValueProperty(NEW_PROP));
		Assert.assertNotNull(complexDto1.getSimpleValueProperty(NEW_PROP));
		Assert.assertNull(complexDto2.getSimpleValueProperty(NEW_PROP));

		final ConfigSimpleValueDTO valueDto1 = rootDto.getSimpleValueProperty(NEW_PROP);
		Assert.assertEquals(NEW_TYPE, valueDto1.getPropertyType());
		Assert.assertEquals(NEW_VALUE, valueDto1.getValue());

		final ConfigSimpleValueDTO valueDto2 = complexDto1.getSimpleValueProperty(NEW_PROP);
		Assert.assertEquals(NEW_TYPE, valueDto2.getPropertyType());
		Assert.assertEquals(NEW_VALUE, valueDto2.getValue());
	}

	@Test
	public void testChangeSimpleProperty() {
		final ConfigSimpleValueDTO simpleValueDto = rootDto.getSimpleValueProperty(PROP1);
		Assert.assertNotNull(simpleValueDto);
		Assert.assertEquals(VALUE1, simpleValueDto.getValue());

		propertyChanger.changeSimpleValue(rootDto, PROP1, VALUE1, NEW_VALUE);
		Assert.assertEquals(NEW_VALUE, simpleValueDto.getValue());
	}

	@Test
	public void testChangeSimplePropertyRecursively() {
		final ConfigSimpleValueDTO valueDto1 = rootDto.getSimpleValueProperty(PROP1);
		final ConfigSimpleValueDTO valueDto2 = complexDto1.getSimpleValueProperty(PROP1);
		final ConfigSimpleValueDTO valueDto3 = complexDto2.getSimpleValueProperty(PROP1);

		propertyChanger.changeSimpleValueRecursively(TYPE1, PROP1, VALUE2, NEW_VALUE);

		Assert.assertEquals(valueDto1.getValue(), VALUE1);
		Assert.assertEquals(valueDto2.getValue(), NEW_VALUE);
		Assert.assertEquals(valueDto3.getValue(), VALUE2);
	}
}
