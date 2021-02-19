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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class ModelCentricJTable extends JTable {
    protected AbstractModelCentricTableModel modelCentricTableModel;

    public ModelCentricJTable(AbstractModelCentricTableModel dm) {
        super(dm);
        this.modelCentricTableModel = dm;
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        this.modelCentricTableModel = (AbstractModelCentricTableModel) dataModel;
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        TableViewRowIndex viewRowIndex = new TableViewRowIndex(row);
        TableViewColumnIndex viewColumnIndex = new TableViewColumnIndex(column);
        TableModelColumnIndex modelColumnIndex = new TableModelColumnIndex(this.convertColumnIndexToModel(viewColumnIndex.Value));
        TableModelRowIndex modelRowIndex = new TableModelRowIndex(this.convertRowIndexToModel(viewRowIndex.Value));

        TableModelColumnMeta columnMeta = this.modelCentricTableModel.getColumnMetaByIndex(modelColumnIndex);

        TableCellRenderer renderer = columnMeta.getCellRenderer();

        if (renderer == null) {
            renderer = getDefaultRenderer(getColumnClass(column));
        }
        return renderer;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        TableViewRowIndex viewRowIndex = new TableViewRowIndex(row);
        TableViewColumnIndex viewColumnIndex = new TableViewColumnIndex(column);
        TableModelColumnIndex modelColumnIndex = new TableModelColumnIndex(this.convertColumnIndexToModel(viewColumnIndex.Value));
        TableModelRowIndex modelRowIndex = new TableModelRowIndex(this.convertRowIndexToModel(viewRowIndex.Value));

        TableModelColumnMeta columnMeta = this.modelCentricTableModel.getColumnMetaByIndex(modelColumnIndex);

        TableCellEditor editor = columnMeta.getCellEditor();
        if (editor == null) {
            editor = getDefaultEditor(getColumnClass(column));
        }
        return editor;
    }
}
