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

package org.jeconfig.server.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.Assert;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.server.marshalling.XStreamXmlMarshaller;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class XStreamXmlMarshallerTest {

	private XStreamXmlMarshaller marshaller;

	@Before
	public void setUp() {
		marshaller = new XStreamXmlMarshaller();
	}

	@Test
	public void testEvilString() {
		final ComplexConfigDTO complexConfigDTO = new ComplexConfigDTO();
		complexConfigDTO.addSimpleValueProperty(new ConfigSimpleValueDTO("asdf", "asdf", null, "<<<"));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(out, complexConfigDTO);
		final byte[] bytes = out.toByteArray();

		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		final ComplexConfigDTO unmarshalled = marshaller.unmarshal(in);
		Assert.assertEquals(complexConfigDTO, unmarshalled);
	}

	//TODO maybe test some more special xml stuff?
}
