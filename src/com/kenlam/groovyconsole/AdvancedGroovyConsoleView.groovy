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

import com.kenlam.groovyconsole.projects.AGCProject

import groovy.ui.*

import groovy.ui.view.Defaults
import groovy.ui.view.GTKDefaults
import groovy.ui.view.MacOSXDefaults
import groovy.ui.view.WindowsDefaults
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import javax.swing.UIManager
import javax.swing.event.DocumentListener
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.SwingConstants
import javax.swing.JTabbedPane
import javax.swing.JPanel

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
	}
	
	projectTabs = tabbedPane(tabPlacement: JTabbedPane.TOP){
		builder_current_AGCProject = new AGCProject(consoleApp: controller)
		JPanel builder_current_project_panel = panel(name: "Project 1") {
			borderLayout()
			build(AdvancedGroovyConsoleContentPane.buildContentPaneForSingleProject)
		}
		// builder_current_AGCProject = null
		// println "builder_current_project_panel ${builder_current_project_panel} (${builder_current_project_panel.getClass()})"
		builder_current_AGCProject.panel = builder_current_project_panel
		panel(name: "New Project") {
			
		}
	}


}

controller.projectTabs = projectTabs
// controller.promptStyle = promptStyle  // Obsolete logic after tab implementation
// controller.commandStyle = commandStyle  // Obsolete logic after tab implementation
// controller.outputStyle = outputStyle  // Obsolete logic after tab implementation
// controller.stacktraceStyle = stacktraceStyle  // Obsolete logic after tab implementation
// controller.hyperlinkStyle = hyperlinkStyle  // Obsolete logic after tab implementation
// controller.resultStyle = resultStyle  // Obsolete logic after tab implementation

// add the window close handler
if (consoleFrame instanceof java.awt.Window) {
    consoleFrame.windowClosing = controller.&exit
}

// link in references to the controller
// controller.outputWindow = outputWindow  // Obsolete logic after tab implementation
controller.statusLabel = status
controller.frame = consoleFrame
// controller.rowNumAndColNum = rowNumAndColNum  // Obsolete logic after tab implementation
controller.toolbar = toolbar

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
// controller.origDividerSize = origDividerSize  // Obsolete logic after tab implementation
// controller.splitPane = splitPane  // Obsolete logic after tab implementation
// controller.blank = blank  // Obsolete logic after tab implementation
// controller.scrollArea = scrollArea  // Obsolete logic after tab implementation

// some more UI linkage
// controller.rootElement = inputArea.document.defaultRootElement  // Obsolete logic after tab implementation


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
        if (controller.askToSaveFile()) {
            controller.loadScriptFile(evt.transferable.getTransferData(DataFlavor.javaFileListFlavor)[0])
        }
    },
] as DropTargetListener

[consoleFrame].each {
    new DropTarget(it, DnDConstants.ACTION_COPY, dtListener)
}

// don't send any return value from the view, all items should be referenced via the bindings
return null
