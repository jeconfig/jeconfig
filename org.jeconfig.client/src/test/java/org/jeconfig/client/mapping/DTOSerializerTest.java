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

package org.jeconfig.client.mapping;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeconfig.api.annotation.ConfigArrayProperty;
import org.jeconfig.api.annotation.ConfigComplexProperty;
import org.jeconfig.api.annotation.ConfigListProperty;
import org.jeconfig.api.annotation.ConfigMapProperty;
import org.jeconfig.api.annotation.ConfigSetProperty;
import org.jeconfig.api.annotation.ConfigSimpleProperty;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.UserScopeDescriptor;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.annotation.array.ArrayTestConfiguration;
import org.jeconfig.client.annotation.complex.ComplexPropertyTestConfiguration;
import org.jeconfig.client.annotation.list.ListTestConfiguration;
import org.jeconfig.client.annotation.map.MapPropertyTestConfiguration;
import org.jeconfig.client.annotation.set.SetPropertyTestConfiguration;
import org.jeconfig.client.annotation.simple.SimplePropertyTestConfiguration;
import org.jeconfig.client.proxy.IRootConfigProxy;
import org.jeconfig.client.proxy.ProxyUtil;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("nls")
public class DTOSerializerTest extends AbstractConfigServiceTest {

	@Test
	public void testSimplePropertyNoDiff() {
		SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		assertNoDiff(config);

		getConfigService().save(config);
		assertNoDiff(config);

		config = getConfigService().load(SimplePropertyTestConfiguration.class);
		assertNoDiff(config);
	}

	@Test
	public void testSimpleProperty() {
		SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setStringValue("101");

		getConfigService().save(config);
		assertPropertyDiff(config, "stringValue");

		config = getConfigService().load(SimplePropertyTestConfiguration.class);
		assertPropertyDiff(config, "stringValue");
	}

	@Test
	public void testComplexPropertyNoDiff() {
		ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		assertNoDiff(config);

		getConfigService().save(config);
		assertNoDiff(config);

		config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		assertNoDiff(config);
	}

	@Test
	public void testComplexPropertyDiff() {
		ComplexPropertyTestConfiguration config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		config.setProperty(ComplexSubtype.create(getConfigService(), "a", "b"));

		getConfigService().save(config);
		assertPropertyDiff(config, "property");

		config = getConfigService().load(ComplexPropertyTestConfiguration.class);
		assertPropertyDiff(config, "property");
	}

	@Test
	public void testArrayPropertyNoDiff() {
		ArrayTestConfiguration config = getConfigService().load(ArrayTestConfiguration.class);
		assertNoDiff(config);

		getConfigService().save(config);
		assertNoDiff(config);

		config = getConfigService().load(ArrayTestConfiguration.class);
		assertNoDiff(config);
	}

	@Test
	public void testArrayPropertyDiff() {
		ArrayTestConfiguration config = getConfigService().load(ArrayTestConfiguration.class);
		config.setIntField(new int[] {1, 2, 3});

		getConfigService().save(config);
		assertPropertyDiff(config, "intField");

		config = getConfigService().load(ArrayTestConfiguration.class);
		assertPropertyDiff(config, "intField");
	}

	@Test
	public void testListPropertyNoDiff() {
		ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		assertNoDiff(config);

		getConfigService().save(config);
		assertNoDiff(config);

		config = getConfigService().load(ListTestConfiguration.class);
		assertNoDiff(config);
	}

	@Test
	public void testListPropertyDiff() {
		ListTestConfiguration config = getConfigService().load(ListTestConfiguration.class);
		final List<String> list = getConfigService().createList();
		list.addAll(Arrays.asList("a", "b"));
		config.setStringField(list);

		getConfigService().save(config);
		assertPropertyDiff(config, "stringField");

		config = getConfigService().load(ListTestConfiguration.class);
		assertPropertyDiff(config, "stringField");
	}

	@Test
	public void testSetPropertyNoDiff() {
		SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		assertNoDiff(config);

		getConfigService().save(config);
		assertNoDiff(config);

		config = getConfigService().load(SetPropertyTestConfiguration.class);
		assertNoDiff(config);
	}

	@Test
	public void testSetPropertyDiff() {
		SetPropertyTestConfiguration config = getConfigService().load(SetPropertyTestConfiguration.class);
		final Set<String> set = getConfigService().createSet();
		set.addAll(Arrays.asList("1", "2"));
		config.setData(set);

		getConfigService().save(config);
		assertPropertyDiff(config, "data");

		config = getConfigService().load(SetPropertyTestConfiguration.class);
		assertPropertyDiff(config, "data");
	}

	@Test
	public void testMapPropertyNoDiff() {
		MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		assertNoDiff(config);

		getConfigService().save(config);
		assertNoDiff(config);

		config = getConfigService().load(MapPropertyTestConfiguration.class);
		assertNoDiff(config);
	}

	@Test
	public void testMapPropertyDiff() {
		MapPropertyTestConfiguration config = getConfigService().load(MapPropertyTestConfiguration.class);
		final Map<String, Integer> map = getConfigService().createMap();
		config.setUserID(map);

		getConfigService().save(config);
		assertPropertyDiff(config, "userID");

		config = getConfigService().load(MapPropertyTestConfiguration.class);
		assertPropertyDiff(config, "userID");
	}

	@Test
	public void testSimplePropertyToNullDiff() {
		final SimplePropertyTestConfiguration config = getConfigService().load(SimplePropertyTestConfiguration.class);
		config.setStringValue(null);
		getConfigService().save(config);

		assertPropertyDiff(config, "stringValue");
		final ComplexConfigDTO dto = getLastOriginalDto(config);
		final ConfigSimpleValueDTO valueDTO = dto.getSimpleValueProperty("stringValue");
		Assert.assertNull(valueDTO.getValue());
		Assert.assertEquals(1, valueDTO.getVersion());
		Assert.assertEquals(1, valueDTO.getParentVersion());
		Assert.assertEquals(ClassScopeDescriptor.NAME, valueDTO.getParentScopeName());
	}

	private void assertPropertyDiff(final Object config, final String propertyName) {
		final IRootConfigProxy proxy = (IRootConfigProxy) config;
		Assert.assertEquals(1, proxy.getPropertiesWithDiff().size());
		Assert.assertTrue(proxy.getPropertiesWithDiff().contains(propertyName));
		final ComplexConfigDTO rootDTO = getLastOriginalDto(proxy);
		try {
			final PropertyDescriptor desc = new PropertyDescriptor(propertyName, ProxyUtil.getConfigClass(config.getClass()));
			for (final Annotation annotation : desc.getReadMethod().getAnnotations()) {
				if (ConfigSimpleProperty.class.equals(annotation.annotationType())) {
					assertDiff(rootDTO, UserScopeDescriptor.NAME, 1, 0, 0, 0, 0);
				} else if (ConfigComplexProperty.class.equals(annotation.annotationType())) {
					assertDiff(rootDTO, UserScopeDescriptor.NAME, 0, 1, 0, 0, 0);
				} else if (ConfigListProperty.class.equals(annotation.annotationType())) {
					assertDiff(rootDTO, UserScopeDescriptor.NAME, 0, 0, 1, 0, 0);
				} else if (ConfigArrayProperty.class.equals(annotation.annotationType())) {
					assertDiff(rootDTO, UserScopeDescriptor.NAME, 0, 0, 1, 0, 0);
				} else if (ConfigSetProperty.class.equals(annotation.annotationType())) {
					assertDiff(rootDTO, UserScopeDescriptor.NAME, 0, 0, 0, 1, 0);
				} else if (ConfigMapProperty.class.equals(annotation.annotationType())) {
					assertDiff(rootDTO, UserScopeDescriptor.NAME, 0, 0, 0, 0, 1);
				}
			}
			Assert.assertNotNull(rootDTO.getProperty(propertyName));
		} catch (final IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertNoDiff(final Object config) {
		final IRootConfigProxy proxy = (IRootConfigProxy) config;
		Assert.assertEquals(0, proxy.getPropertiesWithDiff().size());
		Assert.assertFalse(proxy.hasDiff());
		assertLastScope(config, ClassScopeDescriptor.NAME);
	}

	private void assertLastScope(final Object config, final String expectedScopeName) {
		Assert.assertEquals(expectedScopeName, getLastOriginalDto(config).getDefiningScopePath().getLastScope().getName());
	}

	private ComplexConfigDTO getLastOriginalDto(final Object config) {
		final IRootConfigProxy proxy = (IRootConfigProxy) config;
		return proxy.getConfigDTOs().get(proxy.getConfigDTOs().size() - 1);
	}

	private void assertDiff(
		final ComplexConfigDTO dto,
		final String expectedScopeName,
		final int simple,
		final int complex,
		final int list,
		final int set,
		final int map) {
		Assert.assertEquals(expectedScopeName, dto.getDefiningScopePath().getLastScope().getName());
		Assert.assertEquals(simple, dto.getSimpleProperties().size());
		Assert.assertEquals(complex, dto.getComplexProperties().size());
		Assert.assertEquals(list, dto.getListProperties().size());
		Assert.assertEquals(set, dto.getSetProperties().size());
		Assert.assertEquals(map, dto.getMapProperties().size());
	}
}
