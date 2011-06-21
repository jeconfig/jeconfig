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

package org.jeconfig.server.persister;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jeconfig.api.persister.ScopePathGenerator;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.CodeDefaultScopeDescriptor;
import org.jeconfig.api.scope.Scope;
import org.jeconfig.api.scope.ScopePath;
import org.jeconfig.api.scope.ScopePathBuilder;
import org.jeconfig.common.scope.InternalScopePathBuilderFactory;

public final class DefaultScopePathGenerator implements ScopePathGenerator {

	public static final String PATH_PROPERTY_SEPARATOR = "-"; //$NON-NLS-1$
	private final String scopeSeparator;

	public DefaultScopePathGenerator(final String scopeSeparator) {
		this.scopeSeparator = scopeSeparator;
	}

	@Override
	public String createName(final ScopePath scopePath) {
		DefaultScopePathGeneratorValidator.validateScopePath(scopePath);

		final StringBuffer sb = new StringBuffer();
		final Scope classScope = scopePath.findScopeByName(ClassScopeDescriptor.NAME);
		final String className = classScope.getProperty(ClassScopeDescriptor.PROP_CLASS_NAME);
		sb.append(className);
		return sb.toString();
	}

	@Override
	public String getPathFromScopePath(final ScopePath scopePath) {
		DefaultScopePathGeneratorValidator.validateScopePath(scopePath);

		final StringBuilder sb = new StringBuilder();
		boolean firstScope = true;
		for (final Scope currentScope : scopePath.getScopes()) {
			if (!CodeDefaultScopeDescriptor.NAME.equals(currentScope.getName())
				&& !ClassScopeDescriptor.NAME.equals(currentScope.getName())) {
				if (!firstScope) {
					sb.append(scopeSeparator);
				}
				firstScope = false;
				final Map<String, String> properties = new TreeMap<String, String>(currentScope.getProperties());
				sb.append(buildScopeWithProperty(currentScope.getName(), properties));
			}
		}

		return sb.toString();
	}

	@Override
	public String buildScopeWithProperty(final String scopeName, final Map<String, String> properties) {

		final StringBuilder sb = new StringBuilder();
		sb.append(scopeName);
		for (final Entry<String, String> entry : properties.entrySet()) {
			sb.append(scopeSeparator);
			sb.append(entry.getKey());
			sb.append(PATH_PROPERTY_SEPARATOR);
			sb.append(entry.getValue());
		}
		return sb.toString();
	}

	@Override
	public Collection<ScopePath> createScopePaths(
		final Collection<String> paths,
		final String scopeName,
		final Map<String, String> properties) {
		final InternalScopePathBuilderFactory factory = new InternalScopePathBuilderFactory();
		ScopePathBuilder builder = factory.createBuilder();
		final String searchedPathPart = buildScopeWithProperty(scopeName, properties);
		final Collection<String> matches = new LinkedList<String>();
		final Collection<ScopePath> results = new LinkedList<ScopePath>();

		for (final Object path : paths) {
			if (((String) path).contains(searchedPathPart)) {
				matches.add((String) path);
			}
		}
		for (final String path : matches) {
			createClassAndCodeDefaultScope(builder, path);
			String[] pathsWithScopeOrProperty;
			if (scopeSeparator.equals("\\")) { //$NON-NLS-1$
				pathsWithScopeOrProperty = path.split(scopeSeparator + scopeSeparator);
			} else if (scopeSeparator.equals("/")) { //$NON-NLS-1$
				pathsWithScopeOrProperty = path.split("/"); //$NON-NLS-1$
			} else {
				throw new RuntimeException("Unknown file separator for path '" + path + "'!"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// Do nothing with last element, because its the fileName
			for (int i = 0; i < pathsWithScopeOrProperty.length - 1; i++) {
				final String tmp = pathsWithScopeOrProperty[i];
				if (tmp.length() == 0) {
					continue;
				} else {
					if (!tmp.contains(PATH_PROPERTY_SEPARATOR)) {
						final String tmpScopeName = tmp.substring(0, tmp.length());
						builder.append(tmpScopeName, getProperties(pathsWithScopeOrProperty, builder, i));
					}
				}
			}
			results.add(builder.create());
			builder = factory.createBuilder();
		}
		return results;
	}

	private void createClassAndCodeDefaultScope(final ScopePathBuilder builder, final String path) {
		final String className = path.substring(path.lastIndexOf(scopeSeparator) + 1, path.lastIndexOf(".")); //$NON-NLS-1$
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		tmpProperties.put("className", className); //$NON-NLS-1$
		builder.append(ClassScopeDescriptor.NAME, tmpProperties);
		builder.append(CodeDefaultScopeDescriptor.NAME);
	}

	private Map<String, String> getProperties(final String[] paths, final ScopePathBuilder builder, final int scopeIndex) {
		final Map<String, String> tmpProperties = new TreeMap<String, String>();
		// start with the element after scope, do nothing with last element, because its the fileName.
		for (int j = scopeIndex + 1; j < paths.length - 1; j++) {
			final String property = paths[j];
			if (!property.contains(PATH_PROPERTY_SEPARATOR)) {
				break;
			}
			final String key = property.substring(0, property.indexOf(PATH_PROPERTY_SEPARATOR));
			final String value = property.substring(property.indexOf(PATH_PROPERTY_SEPARATOR) + 1, property.length());
			tmpProperties.put(key, value);
		}
		return tmpProperties;
	}
}
