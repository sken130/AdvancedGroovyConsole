/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *  Modifications copyright 2016 Ken Lam
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
package com.kenlam.groovyconsole

import groovy.console.ui.view.*

import java.awt.MenuItem;

import groovy.swing.factory.SeparatorFactory;

menuBar {
    menu(text: 'File', mnemonic: 'F') {
        menuItem(newFileAction)
        menuItem(newWindowAction)
        menuItem(openAction)
        separator()
        menuItem(saveAction)
        menuItem(saveAsAction)
        separator()
        menuItem(printAction)
        separator()
        menuItem(exitAction)
    }

    menu(text: 'Edit', mnemonic: 'E') {
        menuItem(undoAction)
        menuItem(redoAction)
        separator()
        menuItem(cutAction)
        menuItem(copyAction)
        menuItem(pasteAction)
        separator()
        menuItem(findAction)
        menuItem(findNextAction)
        menuItem(findPreviousAction)
        menuItem(replaceAction)
        separator()
        menuItem(selectAllAction)
	separator()
	menuItem(commentAction)
    }

    menu(text: 'View', mnemonic: 'V') {
        menuItem(clearOutputAction)
        separator()
        menuItem(largerFontAction)
        menuItem(smallerFontAction)
        separator()
        checkBoxMenuItem(captureStdOutAction, selected: controller.captureStdOut)
        checkBoxMenuItem(captureStdErrAction, selected: controller.captureStdErr)
        checkBoxMenuItem(fullStackTracesAction, selected: controller.fullStackTraces)
        checkBoxMenuItem(showScriptInOutputAction, selected: controller.showScriptInOutput)
        checkBoxMenuItem(visualizeScriptResultsAction, selected: controller.visualizeScriptResults)
        checkBoxMenuItem(showToolbarAction, selected: controller.showToolbar)
        checkBoxMenuItem(detachedOutputAction, selected: controller.detachedOutput)
        checkBoxMenuItem(autoClearOutputAction, selected: controller.autoClearOutput)
    }
	
	def addNewTextInteractionAction = action(
		name: 'Add Text Interaction',
		closure: controller.&addNewTextInteractionModule
	)
	def addNewFileSystemInteractionAction = action(
		name: 'Add File System Interaction',
		closure: controller.&addNewFileSystemInteractionModule
	)
	menu(text: 'Project', mnemonic: 'P') {
		menuItem(addNewTextInteractionAction)
		menuItem(addNewFileSystemInteractionAction)
	}

    menu(text: 'History', mnemonic: 'I') {
        menuItem(historyPrevAction)
        menuItem(historyNextAction)
    }

    menu(text: 'Script', mnemonic: 'S') {
        menuItem(runAction)
        checkBoxMenuItem(saveOnRunAction, selected: controller.saveOnRun)
        menuItem(runSelectionAction)
        checkBoxMenuItem(threadInterruptAction, selected: controller.threadInterrupt)
        menuItem(interruptAction)
        menuItem(compileAction)
        separator()
        menuItem(addClasspathJar)
        menuItem(addClasspathDir)
        menuItem(clearClassloader)
        separator()
        menuItem(inspectLastAction)
        menuItem(inspectVariablesAction)
        menuItem(inspectAstAction)
    }

    menu(text: 'Help', mnemonic: 'H') {
        menuItem(aboutAction)
    }
}
