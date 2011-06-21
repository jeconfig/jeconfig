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

package org.jeconfig.api.annotation.merging;

import org.jeconfig.api.annotation.merging.internal.ChildOverwritesMergingStrategy;
import org.jeconfig.api.annotation.merging.internal.ParentOverwritesMergingStrategy;
import org.jeconfig.api.dto.ConfigSimpleValueDTO;
import org.jeconfig.api.scope.UserScopeDescriptor;

/**
 * Common simple property merging strategies.<br>
 * May be used at the @ConfigSimpleMergingStrategy annotation.
 */
public final class MergingStrategies {

	/**
	 * Merging strategy which always returns the child as the merge result.
	 */
	public static class ChildOverwrites extends ChildOverwritesMergingStrategy<Object> {}

	/**
	 * Merging strategy which always returns the parent as the merge result.
	 */
	public static class ParentOverwrites extends ParentOverwritesMergingStrategy<Object> {}

	/**
	 * Merging strategy for <code>boolean</code>-values which returns the child value except the
	 * user overwrote a property which is set to <code>false</code>.<br>
	 * <br>
	 * Possible use-case: a table column property 'visible' may be overwritten by the user except the administrator
	 * decided to set it to <code>false</code>.
	 */
	public static class UserOverwritesWhenParentTrue implements SimpleValueMergingStrategy<Boolean> {
		@Override
		public ConfigSimpleValueDTO merge(final PropertyMergingParameter<Boolean> mergingParameter) {
			if (UserScopeDescriptor.NAME.equals(mergingParameter.getChildValueDTO().getDefiningScopePath().getLastScope().getName())) {
				if (Boolean.FALSE.equals(mergingParameter.getParentValue())) {
					return mergingParameter.getParentValueDTO();
				}
			}
			return mergingParameter.getChildValueDTO();
		}
	}
}
