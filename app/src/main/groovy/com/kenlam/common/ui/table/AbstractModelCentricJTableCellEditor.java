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

package com.kenlam.common.ui.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public abstract class AbstractModelCentricJTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    public AbstractModelCentricJTableCellEditor() {

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // AbstractModelCentricTableModel modelCentricTableModel = (AbstractModelCentricTableModel) table.getModel();

        TableViewRowIndex viewRowIndex = new TableViewRowIndex(row);
        TableViewColumnIndex viewColumnIndex = new TableViewColumnIndex(column);
        TableModelColumnIndex modelColumnIndex = new TableModelColumnIndex(table.convertColumnIndexToModel(column));
        TableModelRowIndex modelRowIndex = new TableModelRowIndex(table.convertRowIndexToModel(viewRowIndex.Value));

        Component editorComponent = this.getTableCellEditorComponent(table, value, isSelected,
                modelRowIndex, modelColumnIndex, viewRowIndex, viewColumnIndex);

        return editorComponent;
    }

    public abstract Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                          TableModelRowIndex modelRowIndex,
                                                          TableModelColumnIndex modelColumnIndex,
                                                          TableViewRowIndex viewRowIndex,
                                                          TableViewColumnIndex viewColumnIndex);

    @Override
    public abstract Object getCellEditorValue();
}
