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

package com.kenlam.groovyconsole.interactions

import com.kenlam.groovyconsole.AdvancedGroovyConsole
import com.kenlam.groovyconsole.interactions.annotations.SnippetItem
import com.kenlam.groovyconsole.interactions.utils.SnippetUtils
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JMenuItem

public abstract class InteractionModule {
	String name
	
	final LinkedHashSet<Closure> nameChangeListeners = new LinkedHashSet()
	
	Component builtUI
	
	public InteractionModule (AdvancedGroovyConsole console, Map params) {
		this.name = params.name ?: console.getNextInteractionModuleName(this)
	}
	
	public Component buildUI(AdvancedGroovyConsole console) {
		if (this.builtUI) {
			throw new IllegalStateException("UI already built for ${this}")
		}
		this.builtUI = doBuildUI(console)
		return this.builtUI
	}
	
	protected abstract Component doBuildUI(AdvancedGroovyConsole console)
	
	public void addNameChangeListener(Closure listener) {
		nameChangeListeners.add(listener)
	}
	public void removeNameChangeListener(Closure listener) {
		nameChangeListeners.remove(listener)
	}
	public void removeAllNameChangeListeners() {
		nameChangeListeners.removeAll()
	}
	
	public void setName(String newName) {
		this.name = newName
		nameChangeListeners.each{ Closure nameChangeListener -> nameChangeListener(this.name) }
	}
	
	public Component buildSnipperMenuItem(Map params) {
		JMenuItem iModuleMenuItem = new JMenuItem("${this.name}")
		iModuleMenuItem.addActionListener([
			actionPerformed: { ActionEvent actionEvent ->
				// int cursorPos = params.inputArea.getCaretPosition()
				// DefaultStyledDocument document = params.inputArea.document
				// document.insertString(cursorPos, "${AdvancedGroovyConsole.INTERACTION_MODULES_VARIABLE}[\"${this.name}\"]", null)
				params.inputArea.replaceSelection("${AdvancedGroovyConsole.INTERACTION_MODULES_VARIABLE}[\"${this.name}\"]")
			}
		] as ActionListener)
		return iModuleMenuItem
	}
	
	public List<JMenuItem> buildSnippetMenuItemsFromClassMethods(Map params) {
		List menuItemConfigsFromMethods = SnippetUtils.getSnippetMenuItemConfigsFromClassMethods(this.getClass())
		List menuItems = menuItemConfigsFromMethods.collect{ Map methodConfig ->
			JMenuItem methodMenuItem = new JMenuItem(methodConfig.methodSuffix)
			methodMenuItem.addActionListener([
				actionPerformed: { ActionEvent actionEvent ->
					params.inputArea.replaceSelection("${AdvancedGroovyConsole.INTERACTION_MODULES_VARIABLE}[\"${this.name}\"].${methodConfig.methodSuffix}")
				}
			] as ActionListener)
			
			return methodMenuItem
		}
		return menuItems
	}
}