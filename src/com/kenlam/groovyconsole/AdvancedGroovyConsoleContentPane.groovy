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

import groovy.ui.view.*

import groovy.ui.ConsoleTextEditor
import groovy.ui.Console
import java.awt.*
import java.awt.image.BufferedImage
import static javax.swing.JSplitPane.VERTICAL_SPLIT
import javax.swing.text.Style
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument
import java.util.prefs.Preferences
import javax.swing.text.StyleConstants
import javax.swing.WindowConstants
import javax.swing.JSplitPane
import javax.swing.SwingConstants
import javax.swing.JPanel
import java.awt.event.ComponentListener
import java.awt.event.ComponentEvent
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import java.awt.event.FocusListener
import java.awt.event.FocusEvent
import javax.swing.event.CaretListener
import javax.swing.event.CaretEvent
import javax.swing.event.DocumentListener
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.awt.dnd.DropTarget
import java.awt.dnd.DnDConstants

public class AdvancedGroovyConsoleContentPane {

	public static final Closure buildPanelForSingleProject = {
		JPanel builder_current_project_panel = panel(name: "New Project") {
			borderLayout()
			build(AdvancedGroovyConsoleContentPane.buildContentPaneForSingleProject)
		}
		// builder_current_AGCProject = null
		// println "builder_current_project_panel ${builder_current_project_panel} (${builder_current_project_panel.getClass()})"
		builder_current_AGCProject.panel = builder_current_project_panel
		return builder_current_project_panel
	}
	
	public static final Closure buildContentPaneForSingleProject = {
		def prefs = Preferences.userNodeForPackage(AdvancedGroovyConsole)
		def detachedOutputFlag = prefs.getBoolean('detachedOutput', false)
		def blank
		def outputWindow = frame(visible:false, defaultCloseOperation: WindowConstants.HIDE_ON_CLOSE) {
			blank = glue()
			blank.preferredSize = [0, 0] as Dimension
		}
		def outputArea
		def scrollArea
		Closure buildOutputArea = { _prefs ->
			scrollArea = scrollPane(border: emptyBorder(0)) {
				outputArea = textPane(
						editable: false,
						name: 'outputArea',
						contentType: 'text/html',
						background: new Color(255, 255, 218),
						font: new Font('Monospaced', Font.PLAIN, _prefs.getInt('fontSize', 12)),
						border: emptyBorder(4)
				)
			}
		}
		def inputEditor
		def splitPane = splitPane(resizeWeight: 0.5, orientation: VERTICAL_SPLIT) {
			inputEditor = widget(new ConsoleTextEditor(), border:emptyBorder(0))
			buildOutputArea(prefs)
		}


		def inputArea = inputEditor.textEditor
		// attach ctrl-enter to input area
		// need to wrap in actions to keep it from being added as a component
		actions {
			container(inputArea, name: 'inputArea', font:new Font('Monospaced', Font.PLAIN, prefs.getInt('fontSize', 12)), border:emptyBorder(4)) {
				action(runAction)
				action(runSelectionAction)
				action(showOutputWindowAction)
			}
			container(outputArea, name: 'outputArea') {
				action(hideOutputWindowAction1)
				action(hideOutputWindowAction2)
				action(hideOutputWindowAction3)
				action(hideOutputWindowAction4)
			}
		}

		// add styles to the output area, should this be moved into SwingBuilder somehow?
		outputArea.font = new Font('Monospaced', outputArea.font.style, outputArea.font.size)
		StyledDocument doc = outputArea.styledDocument

		Style defStyle = StyleContext.defaultStyleContext.getStyle(StyleContext.DEFAULT_STYLE)

		def applyStyle = {Style style, values -> values.each{k, v -> style.addAttribute(k, v)}}

		Style regular = doc.addStyle('regular', defStyle)
		applyStyle(regular, styles.regular)

		def promptStyle = doc.addStyle('prompt', regular)
		applyStyle(promptStyle, styles.prompt)

		def commandStyle = doc.addStyle('command', regular)
		applyStyle(commandStyle, styles.command)

		def outputStyle = doc.addStyle('output', regular)
		applyStyle(outputStyle, styles.output)

		def resultStyle = doc.addStyle('result', regular)
		applyStyle(resultStyle, styles.result)

		def stacktraceStyle = doc.addStyle('stacktrace', regular)
		applyStyle(stacktraceStyle, styles.stacktrace)

		def hyperlinkStyle = doc.addStyle('hyperlink', regular)
		applyStyle(hyperlinkStyle, styles.hyperlink)

		// redo styles for editor
		doc = inputArea.styledDocument
		StyleContext styleContext = StyleContext.defaultStyleContext
		styles.each {styleName, defs ->
			Style style = styleContext.getStyle(styleName)
			if (style) {
				applyStyle(style, defs)
				String family = defs[StyleConstants.FontFamily]
				if (style.name == 'default' && family) {
					inputEditor.defaultFamily = family
					inputArea.font = new Font(family, Font.PLAIN, inputArea.font.size)
				}
			}
		}

		// set the preferred size of the input and output areas
		// this is a good enough solution, there are margins and scrollbars and such to worry about for 80x12x2
		Graphics g = GraphicsEnvironment.localGraphicsEnvironment.createGraphics (new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB))
		FontMetrics fm = g.getFontMetrics(outputArea.font)

		outputArea.preferredSize = [
			prefs.getInt('outputAreaWidth', fm.charWidth(0x77) * 81),
			prefs.getInt('outputAreaHeight', (fm.getHeight() + fm.leading) * 12)
		] as Dimension

		inputEditor.preferredSize = [
			prefs.getInt('inputAreaWidth', fm.charWidth(0x77) * 81),
			prefs.getInt('inputAreaHeight', (fm.getHeight() + fm.leading) * 12)
		] as Dimension

		def origDividerSize = -1
		if (detachedOutputFlag) {
			splitPane.add(blank, JSplitPane.BOTTOM)
			origDividerSize = splitPane.dividerSize
			splitPane.dividerSize = 0
			splitPane.resizeWeight = 1.0
			outputWindow.add(scrollArea, BorderLayout.CENTER)
		}

		def rowNumAndColNum
		AGCProject agcProject = builder_current_AGCProject
		// build(statusBarClass)
		panel(constraints: BorderLayout.SOUTH) {
			gridBagLayout()
			separator(gridwidth:GridBagConstraints.REMAINDER, fill:GridBagConstraints.HORIZONTAL)
			agcProject.statusLabel = label("Welcome to Groovy ${GroovySystem.version}.",
				weightx:1.0,
				anchor:GridBagConstraints.WEST,
				fill:GridBagConstraints.HORIZONTAL,
				insets: [1,3,1,3])
			separator(orientation:SwingConstants.VERTICAL, fill:GridBagConstraints.VERTICAL)
			rowNumAndColNum = label('1:1', insets: [1,3,1,3])
		}
		
		ComponentListener componentListener = [
			componentHidden: {ComponentEvent e -> },
			componentMoved: {ComponentEvent e -> },
			componentResized: {ComponentEvent e ->
				def component = e.getComponent()
				if (component == outputArea || component == inputArea) {
					def rect = component.getVisibleRect()
					prefs.putInt("${component.name}Width", rect.getWidth().intValue())
					prefs.putInt("${component.name}Height", rect.getHeight().intValue())
				} else {
					prefs.putInt("${component.name}Width", component.width)
					prefs.putInt("${component.name}Height", component.height)
				}
			},
			componentShown: {ComponentEvent e -> }
		] as ComponentListener
		
		outputArea.addComponentListener(componentListener)
		inputArea.addComponentListener(componentListener)
		
		outputArea.addHyperlinkListener([hyperlinkUpdate: { HyperlinkEvent e ->
			if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
				// URL of the form: file://myscript.groovy:32
				String url = e.getURL()
				int lineNumber = url[(url.lastIndexOf(':') + 1)..-1].toInteger()

				def editor = inputEditor.textEditor
				def text = editor.text

				int newlineBefore = 0
				int newlineAfter = 0
				int currentLineNumber = 1

				// let's find the previous and next newline surrounding the offending line
				int i = 0
				for (ch in text) {
					if (ch == '\n') {
						currentLineNumber++
					}
					if (currentLineNumber == lineNumber) {
						newlineBefore = i
						def nextNewline = text.indexOf('\n', i + 1)
						newlineAfter = nextNewline > -1 ? nextNewline : text.length()
						break
					}
					i++
				}

				// highlight / select the whole line
				editor.setCaretPosition(newlineBefore)
				editor.moveCaretPosition(newlineAfter)
			}
		}] as HyperlinkListener)
		
		outputArea.addFocusListener([
			focusGained: { FocusEvent e ->
				// remember component with focus for text-copy functionality
				if (e.component == outputArea || e.component == inputArea) {
					controller.copyFromComponent = e.component
				}
			},
			focusLost: { FocusEvent e -> }
		] as FocusListener)
		
		inputArea.addCaretListener([
			caretUpdate: { CaretEvent e ->
				agcProject.textSelectionStart = Math.min(e.dot,e.mark)
				agcProject.textSelectionEnd = Math.max(e.dot,e.mark)
				agcProject.setRowNumAndColNum()
			}
		] as CaretListener)

		
		agcProject.outputWindow = outputWindow
		agcProject.blank = blank
		agcProject.scrollArea = scrollArea
		agcProject.inputEditor = inputEditor
		agcProject.splitPane = splitPane
		agcProject.inputArea = inputArea
		agcProject.outputArea = outputArea
		agcProject.rowNumAndColNum = rowNumAndColNum
		
		agcProject.promptStyle = promptStyle
		agcProject.commandStyle = commandStyle
		agcProject.outputStyle = outputStyle
		agcProject.stacktraceStyle = stacktraceStyle
		agcProject.hyperlinkStyle = hyperlinkStyle
		agcProject.resultStyle = resultStyle
		
		inputArea.document.addDocumentListener({ agcProject.setDirty(true) } as DocumentListener)
		agcProject.rootElement = inputArea.document.defaultRootElement  // Obsolete logic after tab implementation
		
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
				if (agcProject.askToSaveFile()) {
					agcProject.loadScriptFile(evt.transferable.getTransferData(DataFlavor.javaFileListFlavor)[0])
				}
			},
		] as DropTargetListener

		[inputArea, outputArea].each {
			new DropTarget(it, DnDConstants.ACTION_COPY, dtListener)
		}
	}
}

