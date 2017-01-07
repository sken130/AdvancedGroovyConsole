/*
 *  Copyright 2016 Ken Lam
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kenlam.groovyconsole.interactions.utils

import java.lang.reflect.Method
// import java.awt.Component
import com.kenlam.groovyconsole.interactions.annotations.SnippetItem

public class SnippetUtils {
	public static List<Map> getSnippetMenuItemConfigsFromClassMethods(Class clazz) {
		return clazz.declaredMethods.findAll{ Method method -> method.getAnnotation(SnippetItem) != null }.collect{ Method method ->
			// def snippetItemAnnotation = method.getAnnotation(SnippetItem)
			String methodSuffix = getMethodSnippetName(method)
			return [methodSuffix: methodSuffix]
		}
	}
	
	public static String getMethodSnippetName(Method method) {
		StringBuilder sb = new StringBuilder()
		sb.append(method.getName() + "(");
		sb.append(method.parameterTypes.collect{ Class parameterType ->
			return parameterType.getCanonicalName()
		}.join(", "))
		sb.append(")")
		return sb.toString()
	}
}