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

import com.kenlam.common.ui.table.AbstractModelCentricJTableCellEditor
import com.kenlam.common.ui.table.AbstractModelCentricJTableCellRenderer
import com.kenlam.common.ui.table.AbstractModelCentricTableModel
import com.kenlam.common.ui.table.JTableRendererReturnValues
import com.kenlam.common.ui.table.StringListAsTextAreaCellRenderer
import com.kenlam.common.ui.table.TableModelColumnIndex
import com.kenlam.common.ui.table.TableModelColumnMeta
import com.kenlam.common.ui.table.TableModelRowIndex
import com.kenlam.common.ui.table.TableViewColumnIndex
import com.kenlam.common.ui.table.TableViewRowIndex
import com.kenlam.common.ui.table.popupeditor.StringListAsTextAreaPopupEditor
import com.kenlam.groovyconsole.projects.xmlconfig.ProjectClassPathEntry

import javax.swing.Box
import javax.swing.JButton
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import static com.kenlam.common.SimpleLog.commonLog

public class ProjectClassPathsTableModel extends AbstractModelCentricTableModel {

    protected final List<ProjectClassPathEntry> projectClasspathEntries = []

    private static List<TableModelColumnMeta> getInitialColumnMetas() {
        return [
                new TableModelColumnMeta(
                        "paths",
                        "Paths",
                        () -> {
                            return new StringListAsTextAreaCellRenderer()
                        },
                        () -> {
                            return new StringListAsTextAreaPopupEditor()
                        }
                ),
                new TableModelColumnMeta(
                        "wildCards",
                        "Wildcards",
                        () -> {
                            return new StringListAsTextAreaCellRenderer()
                        },
                        () -> {
                            return new StringListAsTextAreaPopupEditor()
                        }
                ),
                new TableModelColumnMeta(
                        "actions",
                        "Actions",
                        () -> {
                            return new AbstractModelCentricJTableCellRenderer() {
                                @Override
                                JTableRendererReturnValues renderTableCellComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                                                    TableModelColumnMeta columnMeta,
                                                                                    TableModelRowIndex modelRowIndex,
                                                                                    TableModelColumnIndex modelColumnIndex,
                                                                                    TableViewRowIndex viewRowIndex,
                                                                                    TableViewColumnIndex viewColumnIndex) {
                                    Box box = ProjectClassPathsTableModel.getRowActionButtonsBox(null, table, modelRowIndex)
                                    JTableRendererReturnValues returnValues = new JTableRendererReturnValues(box, null)
                                    return returnValues
                                }

                            }
                        },
                        () -> {
                            return new AbstractModelCentricJTableCellEditor() {
                                @Override
                                Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                                      TableModelRowIndex modelRowIndex,
                                                                      TableModelColumnIndex modelColumnIndex,
                                                                      TableViewRowIndex viewRowIndex,
                                                                      TableViewColumnIndex viewColumnIndex) {
                                    Box box = ProjectClassPathsTableModel.getRowActionButtonsBox(this, table, modelRowIndex)
                                    return box
                                }

                                @Override
                                Object getCellEditorValue() {
                                    return null
                                }
                            }
                        },
                        false
                )
        ]
    }

    private static Box getRowActionButtonsBox(TableCellEditor editor, JTable table, TableModelRowIndex modelRowIndex) {
        Box box = Box.createHorizontalBox()
        JButton deleteButton = new JButton()
        deleteButton.setText("Delete")
        deleteButton.addActionListener(
                new ActionListener() {
                    @Override
                    void actionPerformed(ActionEvent e) {
                        // commonLog("delete button clicked for row ${modelRowIndex}")
                        if (editor) {
                            // commonLog("cancelCellEditing for row ${modelRowIndex}")
                            editor.cancelCellEditing()
                        }
                        ProjectClassPathsTableModel model = table.getModel()
                        model.removeRow(modelRowIndex.Value)
                    }
                }
        )
        box.add(deleteButton)
        return box
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
        TableModelColumnMeta columnMeta = this.getColumnMetaByIndex(new TableModelColumnIndex(columnIndex));
        // commonLog("Model.getValueAt(${rowIndex}, ${columnIndex}) - projectClasspathEntries:\n" + projectClasspathEntries)
        try {
            if (columnMeta.isHasData()) {
                String fieldName = columnMeta.getFieldName()
                ProjectClassPathEntry projectClassPathEntry = projectClasspathEntries[rowIndex]
                return projectClassPathEntry[fieldName];
            } else {
                return null
            }
        } catch (ex) {
            commonLog("Model.getValueAt(${rowIndex}, ${columnIndex}) has exception.")
            throw ex
        }
    }

    @Override
    boolean isCellEditable(int rowIndex, int columnIndex) {
        return true
    }

    @Override
    public void doSetValueAt(Object newValue, int rowIndex, int columnIndex) {
        TableModelColumnMeta columnMeta = this.getColumnMetaByIndex(new TableModelColumnIndex(columnIndex));
        if (columnMeta.isHasData()) {
            String fieldName = getFieldNameByColumnIndex(new TableModelColumnIndex(columnIndex))
            ProjectClassPathEntry projectClassPathEntry = projectClasspathEntries[rowIndex]
            projectClassPathEntry[fieldName] = newValue
        }
    }

    public void addRowAtEnd(ProjectClassPathEntry entry) {
        projectClasspathEntries.add(entry)
        int indexInserted = projectClasspathEntries.indexOf(entry)
        super.fireTableRowsInserted(indexInserted, indexInserted)
    }

    public void setRowsData(List<ProjectClassPathEntry> classPathEntries) {
        this.projectClasspathEntries.clear()
        this.projectClasspathEntries.addAll(classPathEntries)
        fireTableDataChanged()
    }

    public void removeRow(int modelRowIndex) {
        projectClasspathEntries.remove(modelRowIndex)
        // commonLog("projectClasspathEntries.remove(${modelRowIndex})")
        fireTableRowsDeleted(modelRowIndex, modelRowIndex);
    }

    public void removeAllRows() {
        projectClasspathEntries.clear()
        fireTableDataChanged()
    }
}
