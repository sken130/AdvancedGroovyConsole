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

import groovy.ui.*

import groovy.ui.view.Defaults
import groovy.ui.view.GTKDefaults
import groovy.ui.view.MacOSXDefaults
import groovy.ui.view.WindowsDefaults
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import javax.swing.UIManager
import javax.swing.BoxLayout
import javax.swing.event.DocumentListener
import java.awt.BorderLayout
import java.awt.Component
import java.awt.GridBagConstraints
import javax.swing.SwingConstants
import javax.swing.JTabbedPane
import javax.swing.JOptionPane

switch (UIManager.getSystemLookAndFeelClassName()) {
    case 'com.sun.java.swing.plaf.windows.WindowsLookAndFeel':
    case 'com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel':
        build(ViewWindowsDefaults)
		menuBarClass = AdvancedGroovyConsoleMenuBar
        break

    case 'apple.laf.AquaLookAndFeel':
    case 'com.apple.laf.AquaLookAndFeel':
        build(MacOSXDefaults)
        break

    case 'com.sun.java.swing.plaf.gtk.GTKLookAndFeel':
        build(GTKDefaults)
        break

    default:
        build(ViewDefaults)
        break
}

binding.rootContainerDelegate.delegate = this

contentPaneClass = AdvancedGroovyConsoleContentPane

consoleFrame = binding['rootContainerDelegate']()
container(consoleFrame) {

    binding.menuBarDelegate.delegate = delegate
    binding['menuBarDelegate'](menuBarClass)

    // build(toolBarClass)
	toolbar = toolBar(rollover:true, visible:controller.showToolbar, constraints:BorderLayout.NORTH) {
		button(newFileAction, text:null)
		button(openAction, text:null)
		button(saveAction, text:null)
		separator(orientation:SwingConstants.VERTICAL)
		button(undoAction, text:null)
		button(redoAction, text:null)
		separator(orientation:SwingConstants.VERTICAL)
		button(cutAction, text:null)
		button(copyAction, text:null)
		button(pasteAction, text:null)
		separator(orientation:SwingConstants.VERTICAL)
		button(findAction, text:null)
		button(replaceAction, text:null)
		separator(orientation:SwingConstants.VERTICAL)
		button(historyPrevAction, text:null)
		button(historyNextAction, text:null)
		separator(orientation:SwingConstants.VERTICAL)
		button(runAction, text:null)
		button(interruptAction, text:null)
		separator(orientation:SwingConstants.VERTICAL)
		controller.showSnippetMenuTbBtn = button(showSnippetMenuAction, text: 'Snippets')
	}
	
    def projectClassPathsPanel
    def scriptPanel1
	projectTabPanel = tabbedPane(tabPlacement: JTabbedPane.TOP){
        projectClassPathsPanel = panel(
            name: "Project ClassPaths",
            alignmentX: Component.LEFT_ALIGNMENT) {
            
            boxLayout()
        }
		scriptPanel1 = panel(name: "Script 1") {
			borderLayout()
			build(contentPaneClass)
			// build(statusBarClass)
			panel(constraints: BorderLayout.SOUTH) {
				gridBagLayout()
				separator(gridwidth:GridBagConstraints.REMAINDER, fill:GridBagConstraints.HORIZONTAL)
				status = label("Welcome to Groovy ${GroovySystem.version}.",
					weightx:1.0,
					anchor:GridBagConstraints.WEST,
					fill:GridBagConstraints.HORIZONTAL,
					insets: [1,3,1,3])
				separator(orientation:SwingConstants.VERTICAL, fill:GridBagConstraints.VERTICAL)
				rowNumAndColNum = label('1:1', insets: [1,3,1,3])
			}
		}
		// Depending on project config, will contain other tabs such as FileInteractionModule, DBInteractionModule, TextInteractionModule
	}

    controller.projectClassPathsPanel = projectClassPathsPanel
    controller.scriptPanel1 = scriptPanel1
}


controller.promptStyle = promptStyle
controller.commandStyle = commandStyle
controller.outputStyle = outputStyle
controller.stacktraceStyle = stacktraceStyle
controller.hyperlinkStyle = hyperlinkStyle
controller.resultStyle = resultStyle

// add the window close handler
if (consoleFrame instanceof java.awt.Window) {
    consoleFrame.windowClosing = controller.&exit
}

// link in references to the controller
controller.inputEditor = inputEditor
controller.inputArea = inputEditor.textEditor
controller.outputArea = outputArea
controller.outputWindow = outputWindow
controller.statusLabel = status
controller.frame = consoleFrame
controller.rowNumAndColNum = rowNumAndColNum
controller.toolbar = toolbar
controller.projectTabPanel = projectTabPanel

// link actions
controller.saveAction = saveAction
controller.prevHistoryAction = historyPrevAction
controller.nextHistoryAction = historyNextAction
controller.fullStackTracesAction = fullStackTracesAction
controller.showToolbarAction = showToolbarAction
controller.detachedOutputAction = detachedOutputAction
controller.autoClearOutputAction = autoClearOutputAction
controller.saveOnRunAction = saveOnRunAction
controller.threadInterruptAction = threadInterruptAction
controller.showOutputWindowAction = showOutputWindowAction
controller.hideOutputWindowAction1 = hideOutputWindowAction1
controller.hideOutputWindowAction2 = hideOutputWindowAction2
controller.hideOutputWindowAction3 = hideOutputWindowAction3
controller.hideOutputWindowAction4 = hideOutputWindowAction4
controller.interruptAction = interruptAction
controller.origDividerSize = origDividerSize
controller.splitPane = splitPane
controller.blank = blank
controller.scrollArea = scrollArea

// some more UI linkage
controller.outputArea.addComponentListener(controller)
controller.inputArea.addComponentListener(controller)
controller.outputArea.addHyperlinkListener(controller)
controller.outputArea.addHyperlinkListener(controller)
controller.outputArea.addFocusListener(controller)
controller.inputArea.addCaretListener(controller)
controller.inputArea.document.addDocumentListener({ controller.setDirty(true) } as DocumentListener)
controller.rootElement = inputArea.document.defaultRootElement


def dtListener =  [
    dragEnter:{DropTargetDragEvent evt ->
        if (evt.dropTargetContext.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            evt.acceptDrag(DnDConstants.ACTION_COPY)
        } else {
            evt.rejectDrag()
        }
    },
    dragOver:{DropTargetDragEvent evt ->
        //dragEnter(evt)
    },
    dropActionChanged:{DropTargetDragEvent evt ->
        //dragEnter(evt)
    },
    dragExit:{DropTargetEvent evt  ->
    },
    drop:{DropTargetDropEvent evt  ->
        evt.acceptDrop DnDConstants.ACTION_COPY
        //println "Dropping! ${evt.transferable.getTransferData(DataFlavor.javaFileListFlavor)}"
		// println "controller.frame ${controller.frame} (${controller.frame.getClass()})"
		
		def dialogResult = JOptionPane.showConfirmDialog(controller.frame,
			'Are you sure that this file is a text file and is not huge?',
            'GroovyConsole', JOptionPane.YES_NO_OPTION)
        
		if (dialogResult == JOptionPane.YES_OPTION) {
			if (controller.askToSaveFile()) {
				controller.loadScriptFile(evt.transferable.getTransferData(DataFlavor.javaFileListFlavor)[0])
			}
		}
    },
] as DropTargetListener

[consoleFrame, inputArea, outputArea].each {
    new DropTarget(it, DnDConstants.ACTION_COPY, dtListener)
}

// don't send any return value from the view, all items should be referenced via the bindings
return null
