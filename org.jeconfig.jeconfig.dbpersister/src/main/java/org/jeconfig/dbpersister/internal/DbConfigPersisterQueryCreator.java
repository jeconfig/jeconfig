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

/**
 * Utility class, creates SQL-Strings for statements in DbConfigPersister.
 */
// TODO add tests for configPersisterQueryCreator
public final class DbConfigPersisterQueryCreator {
	private static final String WHERE = "WHERE"; //$NON-NLS-1$
	private static final String FROM = "FROM"; //$NON-NLS-1$
	private static final String AND = "AND"; //$NON-NLS-1$
	private static final String SELECT = "SELECT"; //$NON-NLS-1$
	private static final String EQUALS = "="; //$NON-NLS-1$
	private static final String INSERT_INTO = "INSERT INTO"; //$NON-NLS-1$
	private static final String VALUES = "VALUES"; //$NON-NLS-1$
	private static final String UPDATE = "UPDATE"; //$NON-NLS-1$
	private static final String SET = "SET"; //$NON-NLS-1$
	private static final String DELETE = "DELETE"; //$NON-NLS-1$
	private static final String LIKE = "LIKE"; //$NON-NLS-1$
	private static final String WILDCARD = "%"; //$NON-NLS-1$
	private static final String PLACEHOLDER = "?"; //$NON-NLS-1$
	private static final String ESCAPE = "ESCAPE '\'"; //$NON-NLS-1$^
	private static final String LESS_THAN = "<"; //$NON-NLS-1$

	public String createUpdateQuery(final String table, final String targetCol, final String primKey, final String versionCol) {
		final StringBuffer sb = new StringBuffer(UPDATE + " " //$NON-NLS-1$
			+ table
			+ " " //$NON-NLS-1$
			+ SET
			+ " " //$NON-NLS-1$
			+ targetCol
			+ " " //$NON-NLS-1$
			+ EQUALS
			+ " " //$NON-NLS-1$
			+ PLACEHOLDER
			+ " " //$NON-NLS-1$
			+ WHERE
			+ " " //$NON-NLS-1$
			+ primKey
			+ " " //$NON-NLS-1$
			+ EQUALS
			+ " " //$NON-NLS-1$
			+ PLACEHOLDER
			+ " " //$NON-NLS-1$
			+ AND
			+ " " //$NON-NLS-1$
			+ versionCol
			+ " " //$NON-NLS-1$
			+ LESS_THAN
			+ " " //$NON-NLS-1$
			+ PLACEHOLDER);
		return sb.toString();
	}

	public String createSelectEqualsItemQuery(final String selectColumn, final String fromTable, final String whereColumn) {
		final StringBuilder sb = new StringBuilder(SELECT + " " //$NON-NLS-1$
			+ selectColumn
			+ " " //$NON-NLS-1$
			+ FROM
			+ " " //$NON-NLS-1$
			+ fromTable
			+ " " //$NON-NLS-1$
			+ WHERE
			+ " " //$NON-NLS-1$
			+ whereColumn
			+ " " //$NON-NLS-1$
			+ EQUALS
			+ " " //$NON-NLS-1$
			+ PLACEHOLDER);
		return sb.toString();
	}

	public String createSelectLikeItemQuery(
		final String selectColumn,
		final String fromTable,
		final String whereColumn,
		final String item) {
		final StringBuilder sb = new StringBuilder(SELECT + " " //$NON-NLS-1$
			+ selectColumn
			+ " " //$NON-NLS-1$
			+ FROM
			+ " " //$NON-NLS-1$
			+ fromTable
			+ " " //$NON-NLS-1$
			+ WHERE
			+ " " //$NON-NLS-1$
			+ whereColumn
			+ " " //$NON-NLS-1$
			+ LIKE
			+ " " //$NON-NLS-1$
			+ "'" //$NON-NLS-1$
			+ WILDCARD
			+ item
			+ WILDCARD
			+ "' " //$NON-NLS-1$
			+ ESCAPE);
		return sb.toString();
	}

	public String createDeleteEqualsItemQuery(final String fromTable, final String whereColumn) {
		final StringBuilder sb = new StringBuilder(DELETE + " " //$NON-NLS-1$
			+ FROM
			+ " " //$NON-NLS-1$
			+ fromTable
			+ " " //$NON-NLS-1$
			+ WHERE
			+ " " //$NON-NLS-1$
			+ whereColumn
			+ " " //$NON-NLS-1$
			+ EQUALS
			+ " " //$NON-NLS-1$
			+ PLACEHOLDER);
		return sb.toString();
	}

	public String createDeleteLikeItemQuery(final String fromTable, final String whereColumn, final String item) {
		final StringBuilder sb = new StringBuilder(DELETE + " " //$NON-NLS-1$
			+ FROM
			+ " " //$NON-NLS-1$
			+ fromTable
			+ " " //$NON-NLS-1$
			+ WHERE
			+ " " //$NON-NLS-1$
			+ whereColumn
			+ " " //$NON-NLS-1$
			+ LIKE
			+ " " //$NON-NLS-1$
			+ "'" //$NON-NLS-1$
			+ WILDCARD
			+ item
			+ WILDCARD
			+ "' " //$NON-NLS-1$
			+ ESCAPE);
		return sb.toString();
	}

	public String createInsertItemQuery(
		final String targetTable,
		final String firstColumn,
		final String secColumn,
		final String thiColumn) {
		final StringBuilder sb = new StringBuilder(INSERT_INTO + " " //$NON-NLS-1$
			+ targetTable
			+ " " //$NON-NLS-1$
			+ "(" //$NON-NLS-1$
			+ firstColumn
			+ ", " //$NON-NLS-1$
			+ secColumn
			+ ", " //$NON-NLS-1$
			+ thiColumn
			+ ")" //$NON-NLS-1$
			+ " " //$NON-NLS-1$
			+ VALUES
			+ "(" //$NON-NLS-1$
			+ PLACEHOLDER
			+ "," //$NON-NLS-1$
			+ PLACEHOLDER
			+ "," //$NON-NLS-1$
			+ PLACEHOLDER
			+ ")"); //$NON-NLS-1$
		return sb.toString();
	}
}
