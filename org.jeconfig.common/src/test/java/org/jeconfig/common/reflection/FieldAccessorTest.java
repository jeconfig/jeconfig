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

package org.jeconfig.common.reflection;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FieldAccessorTest {
	private FieldAccessor accessor;
	private TestBean testBean;

	@Before
	public void setUp() {
		accessor = new FieldAccessor();
		testBean = new TestBean();
	}

	@Test
	public void testRead() {
		final String value = "value"; //$NON-NLS-1$
		testBean.setName(value);
		String actual = (String) accessor.read(testBean, TestBean.PROP_NAME);
		Assert.assertEquals(value, actual);
		// test that cached accessor also works
		actual = (String) accessor.read(testBean, TestBean.PROP_NAME);
		Assert.assertEquals(value, actual);
	}

	@Test
	public void testFieldType() {
		final Class<?> clazz = accessor.getFieldType(testBean.getClass(), TestBean.PROP_NAME);
		Assert.assertEquals(String.class, clazz);
	}

	@Test
	public void testField() throws IllegalAccessException {
		final String value = "value"; //$NON-NLS-1$
		testBean.setName(value);
		final Field field = accessor.getField(testBean.getClass(), TestBean.PROP_NAME);
		final Object actual = field.get(testBean);
		Assert.assertEquals(value, actual);
	}

	@Test
	public void testWrite() {
		final String value = "value"; //$NON-NLS-1$
		accessor.write(testBean, TestBean.PROP_NAME, value);
		Assert.assertEquals(value, testBean.getName());
	}
}
