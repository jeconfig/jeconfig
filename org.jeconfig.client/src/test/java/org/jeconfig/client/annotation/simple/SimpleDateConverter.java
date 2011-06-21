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

package org.jeconfig.client.annotation.simple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jeconfig.api.conversion.SimpleTypeConverter;

public class SimpleDateConverter implements SimpleTypeConverter<Date> {

	private boolean hasConvertedToObject = false;
	private boolean hasConvertedToSerializedForm = false;

	@Override
	public Date convertToObject(final Class<Date> simpleType, final String serializedForm) {
		final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); //$NON-NLS-1$
		try {
			final Date date = format.parse(serializedForm);
			hasConvertedToObject = true;
			return date;
		} catch (final ParseException e) {
			throw new RuntimeException("Couldn't convert to Date", e); //$NON-NLS-1$
		}
	}

	@Override
	public String convertToSerializedForm(final Date object) {
		final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); //$NON-NLS-1$
		hasConvertedToSerializedForm = true;
		return format.format(object);
	}

	public boolean isHasConvertedToObject() {
		return hasConvertedToObject;
	}

	public boolean isHasConvertedToSerializedForm() {
		return hasConvertedToSerializedForm;
	}
}
