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

package org.jeconfig.client.annotation.complex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.testconfigs.ComplexSubtype;
import org.jeconfig.client.testconfigs.MyInterface;
import org.jeconfig.client.testconfigs.LargeTestConfiguration;
import org.jeconfig.client.testconfigs.SubClass1;
import org.jeconfig.client.testconfigs.SubClass2;
import org.jeconfig.client.testconfigs.SubClass3;
import org.junit.Test;

public class ConfigComplexPropertyTest extends AbstractConfigServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testComplexPropertyOnPrimitiveType() {
		final ComplexPropertyOnPrimitiveTestConfiguration config = getConfigService().load(
				ComplexPropertyOnPrimitiveTestConfiguration.class);
		getConfigService().save(config);
	}

	@Test
	public void testComplexPropertyInterface() {
		final ComplexPropertyInterfacePolyTestConfiguration config = getConfigService().load(
				ComplexPropertyInterfacePolyTestConfiguration.class);
		config.setMyInterface(SubClass1.create(getConfigService(), "test", 5, "1")); //$NON-NLS-1$ //$NON-NLS-2$
		getConfigService().save(config);

		final ComplexPropertyInterfacePolyTestConfiguration result = getConfigService().load(
				ComplexPropertyInterfacePolyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testComplexTypeInArray() {
		final ComplexTypeInArrayConfiguration config = getConfigService().load(ComplexTypeInArrayConfiguration.class);
		final MyInterface[] array = {SubClass1.create(getConfigService(), "fsdf", 6, "1"), SubClass2.create( //$NON-NLS-1$ //$NON-NLS-2$
				getConfigService(),
				"sdf", //$NON-NLS-1$
				0,
				"2")}; //$NON-NLS-1$
		config.setArray(array);
		getConfigService().save(config);

		final ComplexTypeInArrayConfiguration result = getConfigService().load(ComplexTypeInArrayConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testComplexTypeInMapAsKey() {
		getConfigService().load(ComplexTypeInMapAsKeyConfig.class);
	}

	@Test
	public void testComplexTypeInMapAsValue() {
		final ComplexTypeInMapAsValueConfig config = getConfigService().load(ComplexTypeInMapAsValueConfig.class);
		final Map<String, MyInterface> map = getConfigService().createMap();
		map.put("sdfsdf", SubClass2.create(getConfigService(), "xsc", 1, "1")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		config.setMap(map);
		getConfigService().save(config);

		final ComplexTypeInMapAsValueConfig result = getConfigService().load(ComplexTypeInMapAsValueConfig.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testComplexTypeInSet() {
		final ComplexTypeInSetConfiguration config = getConfigService().load(ComplexTypeInSetConfiguration.class);
		final Set<MyInterface> set = getConfigService().createSet();
		set.add(SubClass1.create(getConfigService(), "sdfsdf", 9, "1")); //$NON-NLS-1$ //$NON-NLS-2$#
		config.setSet(set);
		getConfigService().save(config);

		final ComplexTypeInSetConfiguration result = getConfigService().load(ComplexTypeInSetConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testComplexTypeInList() {
		final ComplexTypeInListConfig config = getConfigService().load(ComplexTypeInListConfig.class);
		final List<MyInterface> list = getConfigService().createList();
		list.add(SubClass1.create(getConfigService(), "sdfsdf", 99, "1")); //$NON-NLS-1$ //$NON-NLS-2$
		config.setList(list);
		getConfigService().save(config);

		final ComplexTypeInListConfig result = getConfigService().load(ComplexTypeInListConfig.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testComplexPropertyAbstractClass() {
		final ComplexPropertyAbstractPolyTestConfiguration config = getConfigService().load(
				ComplexPropertyAbstractPolyTestConfiguration.class);
		config.setMyClass(SubClass3.create(getConfigService(), "asd", 7)); //$NON-NLS-1$
		getConfigService().save(config);

		final ComplexPropertyAbstractPolyTestConfiguration result = getConfigService().load(
				ComplexPropertyAbstractPolyTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test
	public void testManyComplexAndSimpleProperties() {
		final LargeTestConfiguration config = getConfigService().load(LargeTestConfiguration.class);
		config.setA(5);
		config.setB("sdfsdf"); //$NON-NLS-1$
		config.setC(ComplexSubtype.create(getConfigService(), "7", "sdfsdf")); //$NON-NLS-1$//$NON-NLS-2$
		config.setD(SubClass1.create(getConfigService(), "dsfsdf", 6, "1")); //$NON-NLS-1$//$NON-NLS-2$
		final Set<Integer> set = getConfigService().createSet();
		set.add(Integer.valueOf(5));
		config.setE(set);
		final Set<ComplexSubtype> set2 = getConfigService().createSet();
		set2.add(ComplexSubtype.create(getConfigService(), "12", "sdfcvbbb")); //$NON-NLS-1$//$NON-NLS-2$
		config.setF(set2);
		final Set<MyInterface> set3 = getConfigService().createSet();
		set3.add(SubClass1.create(getConfigService(), "sdfsdf", 12, "2")); //$NON-NLS-1$//$NON-NLS-2$
		config.setG(set3);
		final List<Integer> list = getConfigService().createList();
		list.add(Integer.valueOf(5));
		config.setH(list);
		final List<ComplexSubtype> list2 = getConfigService().createList();
		list2.add(ComplexSubtype.create(getConfigService(), "12", "sdfsdf")); //$NON-NLS-1$//$NON-NLS-2$
		config.setI(list2);
		final List<MyInterface> list3 = getConfigService().createList();
		list3.add(SubClass2.create(getConfigService(), "sdf", 12)); //$NON-NLS-1$
		config.setJ(list3);

		getConfigService().save(config);

		final LargeTestConfiguration result = getConfigService().load(LargeTestConfiguration.class);
		Assert.assertEquals(config, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testComplexTypeMapMissingValueType() {
		final ComplexTypeMapMissingValueTypeConfig config = getConfigService().load(ComplexTypeMapMissingValueTypeConfig.class);
		final Map<String, MyInterface> map = getConfigService().createMap();
		map.put("sdf", SubClass2.create(getConfigService(), "aaaa", 0)); //$NON-NLS-1$//$NON-NLS-2$
		config.setMap(map);
		getConfigService().save(config);
	}
}
