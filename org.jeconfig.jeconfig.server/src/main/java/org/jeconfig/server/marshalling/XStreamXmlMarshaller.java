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

import java.io.InputStream;
import java.io.OutputStream;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.dto.ConfigListDTO;
import org.jeconfig.api.dto.ConfigMapDTO;
import org.jeconfig.api.dto.ConfigSetDTO;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;

import com.thoughtworks.xstream.XStream;

public final class XStreamXmlMarshaller implements IConfigMarshaller {
	private final XStream xstream = new XStream();

	@SuppressWarnings("nls")
	public XStreamXmlMarshaller() {
		xstream.setClassLoader(getClass().getClassLoader());
		xstream.alias("ConfigClass", ComplexConfigDTO.class);
		xstream.alias("ConfigSimpleValue", ConfigSimpleValueDTO.class);
		xstream.alias("ConfigSet", ConfigSetDTO.class);
		xstream.alias("ConfigList", ConfigListDTO.class);
		xstream.alias("ConfigMap", ConfigMapDTO.class);
	}

	@Override
	public void marshal(final OutputStream out, final ComplexConfigDTO configurationObject) {
		xstream.toXML(configurationObject, out);
	}

	@Override
	public ComplexConfigDTO unmarshal(final InputStream serializedObject) {
		return (ComplexConfigDTO) xstream.fromXML(serializedObject);
	}
}
