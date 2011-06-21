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

package org.jeconfig.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jeconfig.api.exception.StalenessNotifier;
import org.jeconfig.api.exception.NoStalenessNotifier;

/**
 * Configures a configuration root class to be used with the configuration service.<br>
 * Objects of annotated classes can be stored and loaded with the configuration service.<br>
 * <br>
 * Only the root class of an object tree has to be annotated with this annotation. Other complex
 * types which are referenced by it must be annotated with {@link ConfigComplexType}. <br>
 * <br>
 * All properties of the configuration class which should be stored in the configuration
 * must be annotated with one of the various Config*Property-Annotations.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigClass {

	/**
	 * The class version of the configuration class.<br>
	 * When you consider to use migration functionality you should increment
	 * this version on each structural change of the configuration class or one
	 * of its referenced classes.
	 */
	long classVersion() default 1;

	/**
	 * The scope path which should be used when the configuration of this class
	 * should be loaded without a specified scope.<br>
	 * <br>
	 * Note that the scopes 'class' and 'codeDefault' are added automatically at
	 * the beginning of the path. They must not be specified here!
	 */
	String[] scopePath();

	/**
	 * A factory which provides the 'codeDefault' configuration of this configuration class.
	 */
	Class<? extends DefaultConfigFactory<?>> defaultConfigFactory() default NoDefaultConfigFactory.class;

	/**
	 * A staleness notifier which is informed when an instance of the class has been loaded and one or more properties were stale.
	 */
	Class<? extends StalenessNotifier> stalenessNotfier() default NoStalenessNotifier.class;
}
