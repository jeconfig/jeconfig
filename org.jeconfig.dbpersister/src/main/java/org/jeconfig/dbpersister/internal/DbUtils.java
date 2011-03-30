/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel, Lukas Gross
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

package org.jeconfig.dbpersister.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jeconfig.dbpersister.DbConfigPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for DbPersister.
 */
public class DbUtils {
	private static final Logger LOG = LoggerFactory.getLogger(DbConfigPersister.class);

	/**
	 * Closes sql connections ignoring null and exception.
	 * 
	 * @param con
	 */
	public void closeQuietly(final Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (final SQLException e) {
			LOG.trace("Closing db connection failed!", e); //$NON-NLS-1$
		}
	}

	/**
	 * Closes sql statements ignoring null and exception.
	 * 
	 * @param stmt
	 */
	public void closeQuietly(final Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (final SQLException e) {
			LOG.trace("Closing db statement failed!", e); //$NON-NLS-1$
		}
	}

	/**
	 * Closes an inputstream ignoring null and exceptions.
	 * 
	 * @param in
	 */
	public void closeQuietly(final InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (final IOException e) {
			LOG.trace("Closing inputstream failed!", e); //$NON-NLS-1$
		}
	}

	/**
	 * closes an outputstream ignoring null and exceptions.
	 * 
	 * @param out
	 */
	public void closeQuietly(final OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (final IOException e) {
			LOG.trace("Closing outputstream failed!", e); //$NON-NLS-1$
		}
	}
}
