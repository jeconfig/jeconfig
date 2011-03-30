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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to encapsulate exception handling and sql specific boilerplate code from DbPersister.
 * TODO REVIEW use org.springsource JdbcTemplate instead
 * 
 * @param <T>
 */
public class JdbcTemplate<T> {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplate.class);
	private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

	private final DataSource dataSource;
	private final DbUtils dbUtils;

	public JdbcTemplate(final DataSource dataSource) {
		this.dataSource = dataSource;
		this.dbUtils = new DbUtils();
	}

	/**
	 * Configures and opens a connection to Db. Executes the given runnable.
	 * Connection will be closed quietly after executing runnable.
	 * 
	 * @param runnable
	 * @return T a ResultSet or a Collection of IScopePath
	 */
	@SuppressWarnings("unchecked")
	public T perform(final IDbCallable runnable) {
		Connection con = null;
		T result = null;
		try {
			con = connectionHolder.get();
			if (con != null) {
				throw new IllegalStateException("Nested connections are not supported!"); //$NON-NLS-1$
			}
			con = dataSource.getConnection();
			connectionHolder.set(con);
			con.setAutoCommit(false);
			result = (T) runnable.execute(con);
			con.commit();
		} catch (final RuntimeException e) {
			try {
				con.rollback();
			} catch (final SQLException e1) {
				LOG.warn("Error rolling back DB Connection", e1); //$NON-NLS-1$
			}
			throw e;
		} catch (final Exception e) {
			try {
				con.rollback();
			} catch (final SQLException e1) {
				LOG.warn("Error rolling back DB Connection", e1); //$NON-NLS-1$
			}
			throw new RuntimeException(e);
		} finally {
			connectionHolder.remove();
			dbUtils.closeQuietly(con);
		}
		return result;
	}
}
