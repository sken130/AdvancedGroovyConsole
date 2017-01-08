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
import groovy.swing.SwingBuilder
import java.awt.Component
import javax.swing.JList
import javax.swing.SwingConstants
import javax.swing.DropMode
import javax.swing.BorderFactory
import java.awt.dnd.DropTarget
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.awt.datatransfer.DataFlavor
import javax.swing.DefaultListModel
import java.awt.Dimension
import java.awt.Color
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.BoxLayout
import javax.swing.JScrollPane
import javax.swing.Box
import javax.swing.JMenuItem
import javax.swing.JMenu
import com.kenlam.groovyconsole.commonui.JListPanel
import com.kenlam.groovyconsole.interactions.annotations.SnippetItem

public class FileSystemInputModule extends InteractionModule {
	public static final String DEFAULT_NAME_PREFIX = "FileSystem"
	
	protected JList inputFileList
	protected JListPanel inputFileListPanel
	
	public FileSystemInputModule(AdvancedGroovyConsole console, Map params) {
		super(console, params)
	}
	
	@SnippetItem
	public List<File> getAllInputFiles() {
		DefaultListModel listModel = inputFileList.getModel()
		return listModel.toArray().toList()
	}
	
	@SnippetItem
	public List<File> getSelectedInputFiles() {
		return inputFileListPanel.getSelectedValuesList()
	}
	
	protected Component doBuildUI(AdvancedGroovyConsole console) {
		Closure prepareFileList = {
			DefaultListModel listModel = new DefaultListModel()
			JList jList = new JList(listModel)
			jList.dropMode = DropMode.INSERT
			DropTargetListener dtListener = [
				dragEnter: { DropTargetDragEvent evt ->
					if (evt.dropTargetContext.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						evt.acceptDrag(DnDConstants.ACTION_COPY)
					} else {
						evt.rejectDrag()
					}
				},
				dragOver:{DropTargetDragEvent evt -> },
				dropActionChanged:{DropTargetDragEvent evt -> },
				dragExit:{DropTargetEvent evt -> },
				drop: { DropTargetDropEvent dtde ->
					def t = dtde.transferable
					if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						dtde.acceptDrop(DnDConstants.ACTION_REFERENCE)
						t.getTransferData(DataFlavor.javaFileListFlavor).each{ element ->
							// println "drop ${element} (${element.getClass()})"
							listModel.addElement(element)
						}
					}
				}
			] as DropTargetListener
			new DropTarget(jList, DnDConstants.ACTION_COPY, dtListener)
			jList.alignmentX = Component.LEFT_ALIGNMENT
			jList.maximumSize = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)
			// println "jList.maximumSize ${jList.maximumSize} (${jList.maximumSize.getClass()})"
			
			return jList
		}
		JPanel panel = new JPanel()
		panel.name = this.name
		panel.alignmentX = Component.LEFT_ALIGNMENT
		// new BLDComponent()
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS))
		// panel.setBorder(BorderFactory.createLineBorder(Color.red))
		JLabel label_input = new JLabel("Input Files/Directories:")
		label_input.alignmentX = Component.LEFT_ALIGNMENT
		
		this.inputFileList = prepareFileList()
		this.inputFileListPanel = new JListPanel(this.inputFileList)
		this.inputFileListPanel.alignmentX = Component.LEFT_ALIGNMENT
		
		Box box = Box.createVerticalBox()
		box.add(label_input)
		box.add(this.inputFileListPanel)
		
		panel.add(box)
		
		return panel
	}
	
	public Component buildSnippetMenuItem(Map params) {
		JMenu menu = new JMenu("${this.name}")
		
		buildSnippetMenuItemsFromClassMethods(params).each{ JMenuItem methodMenuItem ->
			menu.add(methodMenuItem)
		}
		return menu
	}
}