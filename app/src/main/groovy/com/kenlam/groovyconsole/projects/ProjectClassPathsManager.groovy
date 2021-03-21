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
import com.kenlam.groovyconsole.projects.xmlconfig.ProjectClassPathEntry
import com.kenlam.groovyconsole.projects.xmlconfig.ProjectClassPathSettings

import javax.swing.JButton
import javax.swing.JScrollPane
import javax.swing.JToolBar
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import static com.kenlam.common.SimpleLog.commonLog

public class ProjectClassPathsManager {

    Component builtUI
    protected ProjectClassPathsTableModel classPathsTableModel
    ModelCentricJTable classPathEntryList

    String loadedClassPathEntriesXml

    protected final List<Closure> applyAndSaveListeners = []

    protected void doBuildUI(AdvancedGroovyConsole console) {
        if (this.builtUI != null || this.classPathsTableModel != null || this.classPathEntryList != null) {
            throw new IllegalStateException("The builtUI is not cleared yet.")
        }

        JLabel label_sharedDirectories = new JLabel("Directories or files:")
        label_sharedDirectories.alignmentX = Component.LEFT_ALIGNMENT

        Box box = Box.createVerticalBox()
        // box.alignmentX = Component.LEFT_ALIGNMENT

        box.add(UserInterfaceUtils.boxAndLeftJustify(label_sharedDirectories))

        JToolBar classPathToolbar = new JToolBar()
        classPathToolbar.alignmentX = Component.LEFT_ALIGNMENT
        JButton btnApplyAndSave = new JButton("Apply and Save")
        btnApplyAndSave.addActionListener([
                actionPerformed: { ActionEvent e ->
                    applyAndSaveListeners.each { listener ->
                        listener()
                    }
                }
        ] as ActionListener)
        classPathToolbar.add(btnApplyAndSave)
        classPathToolbar.addSeparator(new Dimension(5, btnApplyAndSave.getPreferredSize().getHeight().toInteger()))
        JButton btnAddClassPathEntry = new JButton("Add")
        classPathToolbar.add(btnAddClassPathEntry)
        classPathToolbar.setFloatable(false)
        box.add(UserInterfaceUtils.boxAndLeftJustify(classPathToolbar))

        classPathsTableModel = new ProjectClassPathsTableModel()

        this.classPathEntryList = new ModelCentricJTable(classPathsTableModel)
        // this.classPathEntryList.setAutoCreateRowSorter(true);  // The sorting is only enabled when debugging.
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

        // panel.add(box)

        this.builtUI = box

        this.commit()
    }

    public void clearBuiltUI() {
        this.builtUI = null
        this.classPathsTableModel = null
        this.classPathEntryList = null
    }

    public void addApplyAndSaveListener(Closure listener) {
        applyAndSaveListeners.push(listener)
    }

    public List<ProjectClassPathEntry> getCurrentClassPathEntries() {
        return classPathsTableModel.projectClasspathEntries
    }

    // Mainly for dirty checking
    public String convertClassPathEntriesToXml(List<ProjectClassPathEntry> classPathEntries) {
        JAXBContext ctx = JAXBContext.newInstance(ProjectClassPathSettings)
        Marshaller marshaller = ctx.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

        ProjectClassPathSettings projectClassPathSettings = new ProjectClassPathSettings()
        projectClassPathSettings.classPathEntries = classPathEntries

        StringWriter stringWriter = new StringWriter()
        stringWriter.withWriter { selfWriter ->
            marshaller.marshal(projectClassPathSettings, selfWriter)
        }
        return stringWriter.toString()
    }

    public void commit() {
        this.loadedClassPathEntriesXml = convertClassPathEntriesToXml(getCurrentClassPathEntries())
        // commonLog("commit - loadedClassPathEntriesXml:\n" + this.loadedClassPathEntriesXml)
    }

    public void loadCurrentClassPathEntries(List<ProjectClassPathEntry> classPathEntries) {
        setCurrentClassPathEntries(classPathEntries)

        commit()
    }

    public void setCurrentClassPathEntries(List<ProjectClassPathEntry> classPathEntries) {
        classPathsTableModel.setRowsData(classPathEntries)
    }

    public void clearCurrentClassPathEntries() {
        classPathsTableModel.removeAllRows()

        commit()
    }

    public boolean isDirty() {
        List<ProjectClassPathEntry> currentClassPathEntries = getCurrentClassPathEntries()
        String currentXml = convertClassPathEntriesToXml(currentClassPathEntries)
        // commonLog("isDirty - currentXml:\n" + currentXml)

        boolean xmlMatched = Objects.equals(this.loadedClassPathEntriesXml, currentXml)
        // commonLog("isDirty - xmlMatched ${xmlMatched}")
        return !xmlMatched
    }
}