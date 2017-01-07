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

import java.awt.Font
import com.kenlam.groovyconsole.AdvancedGroovyConsole
import javax.swing.JTextArea
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JMenu
import groovy.swing.SwingBuilder
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import com.kenlam.groovyconsole.interactions.annotations.SnippetItem
import com.kenlam.groovyconsole.interactions.utils.SnippetUtils

public class TextInteractionModule extends InteractionModule {
	public static final String DEFAULT_NAME_PREFIX = "Text"
	
	protected JTextArea mainTextArea
	
	public TextInteractionModule(AdvancedGroovyConsole console, Map params) {
		super(console, params)
	}
	
	@SnippetItem
	public def getText() {
		return mainTextArea.getText()
	}
	
	protected Component doBuildUI(AdvancedGroovyConsole console) {
		SwingBuilder swing = new SwingBuilder()
		String title = this.name
		def buildResult = swing.build{
			return panel(name: this.name) {
				borderLayout()
				scrollPane(border: emptyBorder(0)) {
					this.mainTextArea = textArea(
						name: 'mainTextArea',
						font: new Font('Monospaced', Font.PLAIN, 12),
						border: emptyBorder(4)
					)
				}
			}
		}
		// println "buildResult ${buildResult} (${buildResult.getClass()})"
		// console.projectTabPanel.addTab(title, buildResult)
		return buildResult
	}
	
	public Component buildSnipperMenuItem(Map params) {
		JMenu menu = new JMenu("${this.name}")
		
		buildSnippetMenuItemsFromClassMethods(params).each{ JMenuItem methodMenuItem ->
			menu.add(methodMenuItem)
		}
		return menu
	}
}