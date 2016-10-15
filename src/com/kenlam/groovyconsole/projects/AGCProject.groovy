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

package com.kenlam.groovyconsole.projects

import com.kenlam.groovyconsole.AdvancedGroovyConsole
import groovy.ui.ConsoleTextEditor

import java.awt.Component
import javax.swing.*
import javax.swing.text.Element
import javax.swing.text.Style
import java.util.concurrent.atomic.AtomicInteger

public class AGCProject {
	public final AdvancedGroovyConsole consoleApp
	public JPanel panel
	
	public File loadedConfigFile
	public final List<File> groovyScriptFiles = []
	protected final Integer newProjectIndex
	
	private static final AtomicInteger NEW_PROJECT_INDEX = new AtomicInteger()
	
	Component outputWindow
	Component blank
	Component scrollArea
	
	ConsoleTextEditor inputEditor
	JSplitPane splitPane
	JTextPane inputArea
	JTextPane outputArea
	JLabel statusLabel
	JLabel rowNumAndColNum
	
	Element rootElement
	int cursorPos
    int rowNum
    int colNum
	
	// Styles for output area
    Style promptStyle
    Style commandStyle
    Style outputStyle
    Style stacktraceStyle
    Style hyperlinkStyle
    Style resultStyle
	
	// Current editor state
	boolean dirty
	int textSelectionStart  // keep track of selections in inputArea
    int textSelectionEnd
	
	public AGCProject(Map params) {
		this.consoleApp = params.consoleApp
		this.loadedConfigFile = params.loadedConfigFile  // null for new projectTabs
		if (!this.loadedConfigFile) {
			newProjectIndex = NEW_PROJECT_INDEX.getAndIncrement() + 1
		}
	}
	
	public Action getSaveAction() {
		return consoleApp.saveAction
	}
	
	public File firstGroovyScriptFile() {
		return groovyScriptFiles[0]
	}
	
	public boolean isNew() {
		return !loadedConfigFile
	}
	
	public int getApplicationTabPaneIndex() {
		assert this.panel != null
		int idx = consoleApp.projectTabs.indexOfComponent(this.panel)
		assert idx > -1
		return idx
	}
	
	void setDirty(boolean newDirty) {
        //TODO when @BoundProperty is live, this should be handled via listeners
        dirty = newDirty
        saveAction.enabled = newDirty
        updateTitle()
    }
	
	void updateTitle() {
		File scriptFile = firstGroovyScriptFile()
		String newTitle = (scriptFile?.name ?: "New Project ${newProjectIndex}") + (dirty?' * ':'') + ''
		consoleApp.projectTabs.setTitleAt(getApplicationTabPaneIndex(), newTitle)
    }
	
	void setRowNumAndColNum() {
        cursorPos = inputArea.getCaretPosition()
        rowNum = rootElement.getElementIndex(cursorPos) + 1

        def rowElement = rootElement.getElement(rowNum - 1)
        colNum = cursorPos - rowElement.getStartOffset() + 1

        rowNumAndColNum.setText("$rowNum:$colNum")
    }


}