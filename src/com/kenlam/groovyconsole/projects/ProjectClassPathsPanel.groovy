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

import java.awt.Component
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListCellRenderer

public class ProjectClassPathsPanel {
    JList classPathEntryList
    JPanel classPathEntryListPanel
    
    Component builtUI
    
    protected JList prepareJList() {
        DefaultListModel listModel = new DefaultListModel()
        JList jList = new JList(listModel)
        
        jList.alignmentX = Component.LEFT_ALIGNMENT
        jList.maximumSize = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)
        
        ListCellRenderer cellRenderer = [
            getListCellRendererComponent: { JList jList_, def element, int index ->
                JPanel classPathEntryPanel = new JPanel()
                classPathEntryPanel.setLayout(new BoxLayout(classPathEntryPanel, BoxLayout.LINE_AXIS))
                classPathEntryPanel.add(new JTextField())
                classPathEntryPanel.add(new JTextField())
                return classPathEntryPanel
            }
        ] as ListCellRenderer
        
        jList.setCellRenderer(cellRenderer)
        
        return jList
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
        
        this.classPathEntryList = prepareJList()
        this.classPathEntryListPanel = new JListPanel(this.classPathEntryList)
        this.classPathEntryListPanel.alignmentX = Component.LEFT_ALIGNMENT
        
        Box box = Box.createVerticalBox()
        box.add(label_sharedDirectories)
        box.add(this.classPathEntryListPanel)
        
        panel.add(box)
        
        this.builtUI = panel
    }
}