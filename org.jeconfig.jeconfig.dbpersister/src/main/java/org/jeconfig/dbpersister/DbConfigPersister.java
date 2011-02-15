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

package org.jeconfig.dbpersister;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.sql.DataSource;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.exception.StaleConfigException;
import org.jeconfig.api.exception.StoreConfigException;
import org.jeconfig.api.persister.IConfigPersister;
import org.jeconfig.api.persister.IScopePathGenerator;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.dbpersister.internal.DbConfigPersisterQueryCreator;
import org.jeconfig.dbpersister.internal.DbUtils;
import org.jeconfig.dbpersister.internal.IDbCallable;
import org.jeconfig.dbpersister.internal.JdbcTemplate;
import org.jeconfig.server.marshalling.IConfigMarshaller;
import org.jeconfig.server.persister.DefaultScopePathGenerator;

public final class DbConfigPersister implements IConfigPersister {
	public static final String ID = DbConfigPersister.class.getName();
	private static final String SCOPE_PATH_SEPARATOR = "/"; //$NON-NLS-1$

	private final DataSource dataSource;
	private final DbUtils dbUtils;
	private final DbConfigPersisterQueryCreator queryGen;
	private final IScopePathGenerator gen;
	private final IConfigMarshaller marshaller;
	private final String configTableName;
	private final String scopePathColumnName;
	private final String configColumnName;
	private final String configVersionColumnName;

	public DbConfigPersister(
		final String configTableName,
		final String scopePathColumnName,
		final String configVersionColumnName,
		final String configColumnName,
		final IConfigMarshaller marshaller,
		final DataSource dataSource) {
		Assert.paramNotEmpty(configTableName, "configTableName"); //$NON-NLS-1$
		Assert.paramNotEmpty(scopePathColumnName, "scopePathColumnName"); //$NON-NLS-1$
		Assert.paramNotEmpty(configColumnName, "configColumnName"); //$NON-NLS-1$
		Assert.paramNotNull(marshaller, "marshaller"); //$NON-NLS-1$
		Assert.paramNotNull(dataSource, "dataSource"); //$NON-NLS-1$

		this.configTableName = configTableName;
		this.scopePathColumnName = scopePathColumnName;
		this.configColumnName = configColumnName;
		this.configVersionColumnName = configVersionColumnName;
		this.dataSource = dataSource;
		this.marshaller = marshaller;
		gen = new DefaultScopePathGenerator(SCOPE_PATH_SEPARATOR);
		queryGen = new DbConfigPersisterQueryCreator();
		dbUtils = new DbUtils();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public ComplexConfigDTO loadConfiguration(final IScopePath scopePath) {
		Assert.paramNotNull(scopePath, "scope"); //$NON-NLS-1$

		final ComplexConfigDTO result = new JdbcTemplate<ComplexConfigDTO>(dataSource).perform(new IDbCallable() {

			@Override
			public Object execute(final Connection con) throws Exception {
				InputStream in = null;
				PreparedStatement stmt = null;
				try {
					stmt = con.prepareStatement(queryGen.createSelectEqualsItemQuery(
							configColumnName,
							configTableName,
							scopePathColumnName));
					stmt.setString(1, createColumnPath(scopePath));
					final ResultSet resultSet = stmt.executeQuery();
					Blob result = null;
					if (resultSet.next()) {
						result = resultSet.getBlob(configColumnName);
					}
					if (result != null) {
						in = result.getBinaryStream();
						final ComplexConfigDTO configDTO = marshaller.unmarshal(in);
						if (configDTO.getVersion() < 1) {
							throw new StoreConfigException(
								"Illegal config version. Must be 1 or higher. Occures at scope Path '" + configDTO.getDefiningScopePath() + "'!"); //$NON-NLS-1$ //$NON-NLS-2$
						}
						return configDTO;
					}
					return null;
				} finally {
					dbUtils.closeQuietly(stmt);
					dbUtils.closeQuietly(in);
				}
			}
		});
		return result;
	}

	@Override
	public void saveConfiguration(final ComplexConfigDTO configDTO) {
		Assert.paramNotNull(configDTO, "configDTO"); //$NON-NLS-1$

		if (configDTO.getVersion() < 1) {
			throw new StoreConfigException(
				"Illegal config version. Must be 1 or higher. Occures at scope Path '" + configDTO.getDefiningScopePath() + "'!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		new JdbcTemplate<ResultSet>(dataSource).perform(new IDbCallable() {

			@Override
			public Object execute(final Connection con) throws Exception {
				PreparedStatement existsStmt = null;
				PreparedStatement saveStmt = null;
				ByteArrayOutputStream out = null;

				try {
					existsStmt = con.prepareStatement(queryGen.createSelectEqualsItemQuery(
							configColumnName,
							configTableName,
							scopePathColumnName));
					existsStmt.setString(1, createColumnPath(configDTO.getDefiningScopePath()));
					existsStmt.executeQuery();
					final ResultSet resultSet = existsStmt.getResultSet();
					if (resultSet.next()) {
						throw new StaleConfigException(
							configDTO.getDefiningScopePath(),
							"Can't save new configuration. There exists already a file for the scope path '" + configDTO.getDefiningScopePath() + "'!"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					out = new ByteArrayOutputStream();
					marshaller.marshal(out, configDTO);
					final byte[] serializedConfigAsByteArray = out.toByteArray();
					saveStmt = con.prepareStatement(queryGen.createInsertItemQuery(
							configTableName,
							scopePathColumnName,
							configVersionColumnName,
							configColumnName));
					saveStmt.setString(1, createColumnPath(configDTO.getDefiningScopePath()));
					saveStmt.setLong(2, configDTO.getVersion());
					saveStmt.setBinaryStream(
							3,
							new ByteArrayInputStream(serializedConfigAsByteArray),
							serializedConfigAsByteArray.length);
					saveStmt.executeUpdate();
					return null;
				} finally {
					dbUtils.closeQuietly(out);
					dbUtils.closeQuietly(existsStmt);
					dbUtils.closeQuietly(saveStmt);
				}
			}
		});
	}

	@Override
	public void updateConfiguration(final ComplexConfigDTO configDTO) {
		Assert.paramNotNull(configDTO, "configDTO"); //$NON-NLS-1$

		new JdbcTemplate<ResultSet>(dataSource).perform(new IDbCallable() {

			@Override
			public Object execute(final Connection con) throws Exception {
				PreparedStatement updateStmt = null;
				final Statement stmt = null;
				ByteArrayOutputStream out = null;
				try {
					updateStmt = con.prepareStatement(queryGen.createUpdateQuery(
							configTableName,
							configColumnName,
							scopePathColumnName,
							configVersionColumnName));
					out = new ByteArrayOutputStream();
					marshaller.marshal(out, configDTO);
					final byte[] serializedConfigAsByteArray = out.toByteArray();
					updateStmt.setBinaryStream(
							1,
							new ByteArrayInputStream(serializedConfigAsByteArray),
							serializedConfigAsByteArray.length);
					updateStmt.setString(2, gen.getPathFromScopePath(configDTO.getDefiningScopePath())
						+ SCOPE_PATH_SEPARATOR
						+ gen.createName(configDTO.getDefiningScopePath()));
					updateStmt.setLong(3, configDTO.getVersion());
					final int countRowsUpdated = updateStmt.executeUpdate();
					if (countRowsUpdated < 1) {
						throw new StaleConfigException(
							configDTO.getDefiningScopePath(),
							"There exists no record for the scope path '" + configDTO.getDefiningScopePath() + "' or it has been updated meanwhile!"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					return null;
				} finally {
					dbUtils.closeQuietly(stmt);
					dbUtils.closeQuietly(updateStmt);
					dbUtils.closeQuietly(out);
				}
			}
		});
	}

	@Override
	public void delete(final IScopePath scope, final boolean deleteChildren) {
		Assert.paramNotNull(scope, "scope"); //$NON-NLS-1$

		new JdbcTemplate<ResultSet>(dataSource).perform(new IDbCallable() {

			@Override
			public Object execute(final Connection con) throws Exception {
				PreparedStatement stmt = null;
				try {
					if (deleteChildren) {
						stmt = con.prepareStatement(queryGen.createDeleteLikeItemQuery(
								configTableName,
								scopePathColumnName,
								gen.getPathFromScopePath(scope)));
					} else {
						stmt = con.prepareStatement(queryGen.createDeleteEqualsItemQuery(configTableName, scopePathColumnName));
						stmt.setString(1, createColumnPath(scope));
					}
					stmt.executeUpdate();
				} finally {
					dbUtils.closeQuietly(stmt);
				}
				return null;
			}
		});
	}

	@Override
	public void deleteAllOccurences(final String scopeName, final Map<String, String> properties) {
		Assert.paramNotNull(scopeName, "scopeName"); //$NON-NLS-1$
		Assert.paramNotNull(properties, "properties"); //$NON-NLS-1$

		new JdbcTemplate<ResultSet>(dataSource).perform(new IDbCallable() {

			@Override
			public Object execute(final Connection con) throws Exception {
				PreparedStatement stmt = null;
				final String searchedPathPart = gen.buildScopeWithProperty(scopeName, properties);
				try {
					stmt = con.prepareStatement(queryGen.createDeleteLikeItemQuery(
							configTableName,
							scopePathColumnName,
							searchedPathPart));
					stmt.executeUpdate();
				} catch (final SQLException e) {
					throw new StoreConfigException("Error while deleting configurations with path'" + searchedPathPart + "'!", e); //$NON-NLS-1$ //$NON-NLS-2$
				} finally {
					dbUtils.closeQuietly(stmt);
				}
				return null;
			}
		});
	}

	@Override
	public Collection<IScopePath> listScopes(final String scopeName, final Map<String, String> properties) {
		Assert.paramNotNull(scopeName, "scopeName"); //$NON-NLS-1$
		Assert.paramNotNull(properties, "properties"); //$NON-NLS-1$

		final Collection<IScopePath> result = new JdbcTemplate<Collection<IScopePath>>(dataSource).perform(new IDbCallable() {

			@Override
			public Object execute(final Connection con) throws Exception {
				final String searchedPathPart = gen.buildScopeWithProperty(scopeName, properties);
				final Collection<String> paths = new LinkedList<String>();
				PreparedStatement stmt = null;
				try {
					final String createSelectLikeItemQuery = queryGen.createSelectLikeItemQuery(
							scopePathColumnName,
							configTableName,
							scopePathColumnName,
							searchedPathPart);
					stmt = con.prepareStatement(createSelectLikeItemQuery);
					final ResultSet resSet = stmt.executeQuery();
					while (resSet.next()) {
						final String result = resSet.getString(scopePathColumnName);
						paths.add(result);
					}
				} finally {
					dbUtils.closeQuietly(stmt);
				}
				return gen.createScopePaths(paths, scopeName, properties);
			}
		});
		return result;
	}

	private String createColumnPath(final IScopePath scopePath) {
		final StringBuilder sb = new StringBuilder();
		sb.append(gen.getPathFromScopePath(scopePath));
		sb.append(SCOPE_PATH_SEPARATOR);
		sb.append(gen.createName(scopePath));
		return sb.toString();
	}
}
