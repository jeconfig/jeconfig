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

package org.jeconfig.exporter;

import java.util.Collection;
import java.util.Iterator;

import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.persister.IConfigPersister;
import org.jeconfig.api.scope.ClassScopeDescriptor;
import org.jeconfig.api.scope.IScope;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.util.Assert;
import org.jeconfig.exporter.internal.ExportScopeDescriptor;
import org.jeconfig.exporter.internal.ExportScopePathGenerator;

/**
 * Exports/Imports configurations for certain scopes
 */
public final class ConfigExporter {
	private final IConfigPersister configPersister;

	public ConfigExporter(final IConfigPersister configPersister) {
		Assert.paramNotNull(configPersister, "configPersister"); //$NON-NLS-1$
		this.configPersister = configPersister;
	}

	/**
	 * exports a collection of scopePathes
	 * 
	 * @param scopePathes the collection of IScopePath to export
	 */
	public void exportConfig(final Collection<IScopePath> scopePathes) {
		for (final Iterator<IScopePath> i = scopePathes.iterator(); i.hasNext();) {
			final IScopePath tmpScopePath = i.next();
			final IScope classScope = tmpScopePath.findScopeByName(ClassScopeDescriptor.NAME);
			if (classScope != null) {
				try {
					final Class<?> configClass = Class.forName(classScope.getProperty(ClassScopeDescriptor.PROP_CLASS_NAME));
					exportConfig(configClass, tmpScopePath);
				} catch (final ClassNotFoundException e) {
					throw new RuntimeException(
						"Coulnd't find class for name '" + classScope.getProperty(ClassScopeDescriptor.PROP_CLASS_NAME + "'"), e); //$NON-NLS-1$//$NON-NLS-2$
				}
			}
		}
	}

	/**
	 * exports a configuration
	 * 
	 * @param configClass the type of the configuration to export
	 * @param scopePath the scope describing the export source
	 * @param <T> the type of the configuration to export
	 * 
	 */
	public <T> void exportConfig(final Class<T> configClass, final IScopePath scopePath) {
		exportConfig(configClass, scopePath, true);
	}

	/**
	 * exports a configuration
	 * 
	 * @param <T> the type of the configuration to export
	 * @param configClass the type of the configuration to export
	 * @param scopePath scope the scope describing the export source
	 * @param overwrites delete existing configuration before export
	 */
	public <T> void exportConfig(final Class<T> configClass, final IScopePath scopePath, final boolean overwrites) {
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$
		Assert.paramNotNull(scopePath, "scope"); //$NON-NLS-1$
		if (!(scopePath.findScopeByName(ExportScopeDescriptor.NAME) == null)) {
			throw new IllegalArgumentException(
				"Can't export configClass with scopePath containing 'export'. Remove ExportScope from scopePath."); //$NON-NLS-1$
		}
		IScopePath targetScope = new ExportScopePathGenerator().buildExportScopePath(scopePath, configClass);
		IScopePath sourceScope = scopePath;
		while (targetScope.getLastScope().getName() != ExportScopeDescriptor.NAME) {
			final ComplexConfigDTO sourceConfig = configPersister.loadConfiguration(sourceScope);
			if (overwrites) {
				configPersister.delete(targetScope, false);
			}
			if (sourceConfig != null) {
				final ComplexConfigDTO targetConfig = sourceConfig.deepCopyToScopePath(targetScope);
				configPersister.saveConfiguration(targetConfig);
			}

			sourceScope = sourceScope.getParentPath();
			targetScope = targetScope.getParentPath();
		}
	}

	/**
	 * imports a configuration
	 * 
	 * @param <T> the type of the configuration to import
	 * @param configClass the type of the configuration to import
	 * @param scopePath the scope describing the import destination
	 */
	public <T> void importConfig(final Class<T> configClass, final IScopePath scopePath) {
		importConfig(configClass, scopePath, true);
	}

	/**
	 * imports a configuration
	 * 
	 * @param <T> the type of the configuration to import
	 * @param configClass the type of the configuration to import
	 * @param scopePath the scope describing the import destination
	 * @param overwrites delete existing configuration before import
	 */
	public <T> void importConfig(final Class<T> configClass, final IScopePath scopePath, final boolean overwrites) {
		Assert.paramNotNull(configClass, "configClass"); //$NON-NLS-1$
		Assert.paramNotNull(scopePath, "scope"); //$NON-NLS-1$
		if (!(scopePath.findScopeByName(ExportScopeDescriptor.NAME) == null)) {
			throw new IllegalArgumentException(
				"Can't import configClass with scopePath containing 'export'. Remove ExportScope from scopePath."); //$NON-NLS-1$
		}
		IScopePath sourceScope = new ExportScopePathGenerator().buildExportScopePath(scopePath, configClass);
		IScopePath targetScope = scopePath;
		while (sourceScope.getLastScope().getName() != ExportScopeDescriptor.NAME) {
			final ComplexConfigDTO sourceConfig = configPersister.loadConfiguration(sourceScope);
			if (overwrites) {
				configPersister.delete(targetScope, false);
			}
			if (sourceConfig != null) {
				final ComplexConfigDTO targetConfig = sourceConfig.deepCopyToScopePath(targetScope);
				configPersister.saveConfiguration(targetConfig);
			}
			targetScope = targetScope.getParentPath();
			sourceScope = sourceScope.getParentPath();
		}
	}

}
