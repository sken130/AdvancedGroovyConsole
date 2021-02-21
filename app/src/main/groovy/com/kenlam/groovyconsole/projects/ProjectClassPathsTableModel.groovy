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


import com.kenlam.common.ui.table.AbstractModelCentricTableModel
import com.kenlam.common.ui.table.StringListAsTextAreaCellRenderer
import com.kenlam.common.ui.table.StringListAsTextAreaEditor
import com.kenlam.common.ui.table.TableModelColumnIndex
import com.kenlam.common.ui.table.TableModelColumnMeta

public class ProjectClassPathsTableModel extends AbstractModelCentricTableModel {

    private final List<ProjectClassPathEntry> projectClasspathEntries = []

    private static List<TableModelColumnMeta> getInitialColumnMetas() {
        return [
                new TableModelColumnMeta(
                        "paths",
                        "Paths",
                        () -> {
                            return new StringListAsTextAreaCellRenderer()
                        },
                        () -> {
                            return new StringListAsTextAreaEditor()
                        }
                ),
                new TableModelColumnMeta(
                        "wildCards",
                        "Wildcards",
                        () -> {
                            return new StringListAsTextAreaCellRenderer()
                        },
                        () -> {
                            return new StringListAsTextAreaEditor()
                        }
                ),
        ]
    }

    public ProjectClassPathsTableModel() {
        super(getInitialColumnMetas())
    }

    @Override
    public int getRowCount() {
        return projectClasspathEntries.size();
    }


    @Override
    String getColumnName(int column) {
        return columnMetas[column].columnHeader
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String fieldName = getFieldNameByColumnIndex(new TableModelColumnIndex(columnIndex))
        ProjectClassPathEntry projectClassPathEntry = projectClasspathEntries[rowIndex]
        return projectClassPathEntry[fieldName];
    }

    @Override
    boolean isCellEditable(int rowIndex, int columnIndex) {
        return true
    }

    @Override
    public void doSetValueAt(Object newValue, int rowIndex, int columnIndex) {
        String fieldName = getFieldNameByColumnIndex(new TableModelColumnIndex(columnIndex))
        ProjectClassPathEntry projectClassPathEntry = projectClasspathEntries[rowIndex]
        projectClassPathEntry[fieldName] = newValue
    }

    public void addRowAtEnd(ProjectClassPathEntry entry) {
        projectClasspathEntries.add(entry)
        int indexInserted = projectClasspathEntries.indexOf(entry)
        super.fireTableRowsInserted(indexInserted, indexInserted)
    }
}
