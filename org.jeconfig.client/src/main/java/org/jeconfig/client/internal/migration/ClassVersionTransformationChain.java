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

package org.jeconfig.client.internal.migration;

import java.util.ArrayList;
import java.util.List;

import org.jeconfig.api.annotation.ConfigMigration;
import org.jeconfig.api.annotation.ConfigTransformer;
import org.jeconfig.api.dto.ComplexConfigDTO;
import org.jeconfig.api.migration.ConfigTransformerDelegate;
import org.jeconfig.client.internal.AnnotationUtil;

public final class ClassVersionTransformationChain {

	private final List<TransformerContainer> transformerList;

	public ClassVersionTransformationChain(final Class<?> configClass) {
		transformerList = new ArrayList<TransformerContainer>();
		final ConfigMigration annotation = AnnotationUtil.getAnnotation(configClass, ConfigMigration.class);
		if (annotation != null) {
			final ConfigTransformer[] configTransformerAnnos = annotation.value();
			for (final ConfigTransformer ctAnno : configTransformerAnnos) {
				final TransformerContainer transformer = new TransformerContainer(
					ctAnno.sourceVersion(),
					ctAnno.destinationVersion(),
					ctAnno.transformer());
				transformerList.add(transformer);
			}
		}
	}

	public boolean canConvert(final long sourceVersion, final long destinationVersion) {
		final List<TransformerContainer> transformPath = findTransformationPath(sourceVersion, destinationVersion);
		return transformPath != null;
	}

	private List<TransformerContainer> findTransformationPath(final long sourceVersion, final long destinationVersion) {

		if (sourceVersion == destinationVersion) {
			throw new IllegalArgumentException("Equal SourceVersion and DestinationVersion are not allowed."); //$NON-NLS-1$
		}
		if (sourceVersion > destinationVersion) {
			throw new IllegalArgumentException("SourceVersion can't be higher than DestinationVersion."); //$NON-NLS-1$
		}
		if (sourceVersion < 1) {
			throw new IllegalArgumentException("Illegal SourceVersion. Has to be 1 or higher."); //$NON-NLS-1$
		}

		final List<TransformerContainer> relevantConverterList = new ArrayList<TransformerContainer>();
		List<TransformerContainer> resultList = new ArrayList<TransformerContainer>();
		final List<List<TransformerContainer>> resultsList = new ArrayList<List<TransformerContainer>>();

		for (final TransformerContainer transformer : transformerList) {
			// remove converters with lower version than the sourceVersion
			if ((transformer.getSourceVersion() >= sourceVersion) && (transformer.getDestinationVersion() <= destinationVersion)) {
				relevantConverterList.add(transformer);
			}
			// Check if there is a converter for given sourceVersion and
			// destinationVersion
			if ((transformer.getSourceVersion() == sourceVersion) && (transformer.getDestinationVersion() == destinationVersion)) {
				resultList.add(transformer);
				return resultList;
			}
		}
		while (!relevantConverterList.isEmpty()) {
			resultList = buildResultList(
					sourceVersion,
					destinationVersion,
					relevantConverterList,
					new ArrayList<TransformerContainer>());
			if ((resultList != null) && (!resultList.isEmpty())) {
				// add to resultsList if the sourceVersion and
				// destinationVersion are correct.
				if ((resultList.get(0).sourceVersion == sourceVersion)
					&& (resultList.get(resultList.size() - 1).destinationVersion == destinationVersion)) {
					resultsList.add(resultList);
				}
				// remove last used converter
				relevantConverterList.remove(resultList.get(resultList.size() - 1));
			}
		}
		resultList = new ArrayList<TransformerContainer>();
		// Check size of each resultsList elements and return the one with the
		// smallest size (= best path to destinationVersion).
		int size = Integer.MAX_VALUE;
		for (final List<TransformerContainer> currentList : resultsList) {

			if (currentList.size() < size) {
				resultList = currentList;
				size = currentList.size();
			}
		}
		if (!resultList.isEmpty()) {
			return resultList;
		}
		// No converter found
		return null;
	}

	private List<TransformerContainer> buildResultList(
		final long sourceVersion,
		final long destinationVersion,
		final List<TransformerContainer> converterList,
		final List<TransformerContainer> resultList) {
		if (sourceVersion == destinationVersion) {
			return resultList;
		}
		if (converterList.isEmpty()) {
			return resultList;
		}
		boolean convFound = false;
		final List<TransformerContainer> tmpList = new ArrayList<TransformerContainer>();
		for (final TransformerContainer converter : converterList) {
			if (converter.getSourceVersion() == sourceVersion && converter.getDestinationVersion() <= destinationVersion) {
				convFound = true;
				tmpList.add(converter);
			}
		}
		if (convFound) {
			TransformerContainer resultConv = null;
			long length = 0;
			for (final TransformerContainer currentConv : tmpList) {
				if ((currentConv.getDestinationVersion() - currentConv.getSourceVersion()) > length) {
					resultConv = currentConv;
					length = (currentConv.getDestinationVersion() - currentConv.getSourceVersion());
				}
			}
			resultList.add(resultConv);
		} else {
			return resultList;
		}
		return buildResultList(
				resultList.get(resultList.size() - 1).getDestinationVersion(),
				destinationVersion,
				converterList,
				resultList);
	}

	public ComplexConfigDTO convert(final long sourceVersion, final long destinationVersion, final ComplexConfigDTO dtoToTransform) {
		final List<TransformerContainer> transformPath = findTransformationPath(sourceVersion, destinationVersion);
		if (transformPath != null) {
			return doConvert(dtoToTransform, transformPath);
		} else {
			throw new IllegalArgumentException("cannot convert from version " //$NON-NLS-1$
				+ sourceVersion
				+ " to version " //$NON-NLS-1$
				+ destinationVersion);
		}
	}

	private ComplexConfigDTO doConvert(final ComplexConfigDTO dtoToTransform, final List<TransformerContainer> transformPath) {
		for (final TransformerContainer transformerContaioner : transformPath) {
			final ConfigTransformerDelegate transformer = transformerContaioner.getTransformer();
			transformer.transform(new TransformParamImpl(
				transformerContaioner.getSourceVersion(),
				transformerContaioner.getDestinationVersion(),
				dtoToTransform));
			dtoToTransform.setClassVersion(transformerContaioner.getDestinationVersion());
		}
		return dtoToTransform;
	}

	private static class TransformerContainer {
		private final long sourceVersion;
		private final long destinationVersion;
		private final Class<? extends ConfigTransformerDelegate> transformerClass;
		private ConfigTransformerDelegate transformer;

		public TransformerContainer(
			final long sourceVersion,
			final long destinationVersion,
			final Class<? extends ConfigTransformerDelegate> transformerClass) {

			this.sourceVersion = sourceVersion;
			this.destinationVersion = destinationVersion;
			this.transformerClass = transformerClass;
		}

		public long getDestinationVersion() {
			return destinationVersion;
		}

		public long getSourceVersion() {
			return sourceVersion;
		}

		public ConfigTransformerDelegate getTransformer() {
			if (transformer == null) {
				createTransformer();
			}
			return transformer;
		}

		private void createTransformer() {
			try {
				transformer = transformerClass.newInstance();
			} catch (final InstantiationException e) {
				throw new RuntimeException(e);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (destinationVersion ^ (destinationVersion >>> 32));
			result = prime * result + (int) (sourceVersion ^ (sourceVersion >>> 32));
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TransformerContainer other = (TransformerContainer) obj;
			if (destinationVersion != other.destinationVersion) {
				return false;
			}
			if (sourceVersion != other.sourceVersion) {
				return false;
			}
			return true;
		}

	}
}
