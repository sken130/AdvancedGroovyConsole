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

import com.kenlam.common.ui.UserInterfaceUtils
import com.kenlam.common.ui.table.JTableUtils
import com.kenlam.common.ui.table.ModelCentricJTable
import com.kenlam.groovyconsole.AdvancedGroovyConsole

import javax.swing.JButton
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.JToolBar
import javax.swing.SwingConstants
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.JPanel
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

public class ProjectClassPathsPanel {
    ModelCentricJTable classPathEntryList
    JPanel classPathEntryListPanel

    Component builtUI

    protected void doBuildUI(AdvancedGroovyConsole console) {

        JPanel panel = new JPanel()
        panel.name = "ClassPaths"
        panel.alignmentX = Component.LEFT_ALIGNMENT
        // new BLDComponent()
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS))
        // panel.setBorder(BorderFactory.createLineBorder(Color.red))
        JLabel label_sharedDirectories = new JLabel("Directories or files:")
        label_sharedDirectories.alignmentX = Component.LEFT_ALIGNMENT

        Box box = Box.createVerticalBox()
        // box.alignmentX = Component.LEFT_ALIGNMENT

        box.add(UserInterfaceUtils.boxAndLeftJustify(label_sharedDirectories))

        JToolBar classPathToolbar = new JToolBar()
        classPathToolbar.alignmentX = Component.LEFT_ALIGNMENT
        JButton btnApplyAndSave = new JButton("Apply and Save")
        classPathToolbar.add(btnApplyAndSave)
        classPathToolbar.addSeparator(new Dimension(5, btnApplyAndSave.getPreferredSize().getHeight().toInteger()))
        JButton btnAddClassPathEntry = new JButton("Add")
        classPathToolbar.add(btnAddClassPathEntry)
        classPathToolbar.setFloatable(false)
        box.add(UserInterfaceUtils.boxAndLeftJustify(classPathToolbar))

        ProjectClassPathsTableModel classPathsTableModel = new ProjectClassPathsTableModel()

        this.classPathEntryList = new ModelCentricJTable(classPathsTableModel)

        JTableUtils.addAutoHeightListenersToModelCentricJTable(this.classPathEntryList)

        // this.classPathEntryList.setDefaultEditor()
        // this.classPathEntryList.getColumn(null)

        JScrollPane classPathEntryListScrollPane = new JScrollPane(this.classPathEntryList);

        btnAddClassPathEntry.addActionListener([
                actionPerformed: { ActionEvent e ->
                    classPathsTableModel.addRowAtEnd(new ProjectClassPathEntry())
                }
        ] as ActionListener)

        box.add(classPathEntryListScrollPane)

        panel.add(box)

        this.builtUI = panel
    }


}