/*
 *  Copyright 2021 Ken Lam
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
import com.kenlam.groovyconsole.commonui.JListPanel

import javax.swing.JScrollPane
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListCellRenderer

public class ProjectClassPathsPanel {
    JTable classPathEntryList
    JPanel classPathEntryListPanel
    
    Component builtUI
    
    protected JTable prepareJTable() {
        ProjectClassPathsTableModel tableModel = new ProjectClassPathsTableModel()
        JTable jTable = new JTable(tableModel)

        // jTable.alignmentX = Component.LEFT_ALIGNMENT
        // jTable.maximumSize = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)

        // jTable.setDefaultRenderer(cellRenderer)
        // jTable.setDefaultEditor()

        return jTable
    }
    protected void doBuildUI(AdvancedGroovyConsole console) {
        
        JPanel panel = new JPanel()
        panel.name = "ClassPaths"
        panel.alignmentX = Component.LEFT_ALIGNMENT
        // new BLDComponent()
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS))
        // panel.setBorder(BorderFactory.createLineBorder(Color.red))
        JLabel label_sharedDirectories = new JLabel("Directories (Shared):")
        label_sharedDirectories.alignmentX = Component.LEFT_ALIGNMENT
        
        this.classPathEntryList = prepareJTable()

        JScrollPane classPathEntryListScrollPane = new JScrollPane(this.classPathEntryList);

        Box box = Box.createVerticalBox()
        box.add(label_sharedDirectories)
        box.add(classPathEntryListScrollPane)
        
        panel.add(box)
        
        this.builtUI = panel
    }
}