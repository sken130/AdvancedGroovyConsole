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

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TableModelColumnMeta {
    private final String fieldName;
    private final String columnHeader;

    private final TableCellRenderer cellRenderer;
    private final TableCellEditorGetter cellEditorGetter;

    private final boolean hasData;

    public TableModelColumnMeta(String fieldName, String columnHeader,
                                TableCellRenderer cellRenderer,
                                TableCellEditorGetter cellEditorGetter) {
        this(fieldName, columnHeader, cellRenderer, cellEditorGetter, true);
    }

    public TableModelColumnMeta(String fieldName, String columnHeader,
                                TableCellRenderer cellRenderer,
                                TableCellEditorGetter cellEditorGetter,
                                boolean hasData) {
        this.fieldName = fieldName;
        this.columnHeader = columnHeader;
        this.cellRenderer = cellRenderer;
        this.cellEditorGetter = cellEditorGetter;
        this.hasData = hasData;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnHeader() {
        return columnHeader;
    }

    public TableCellRenderer getCellRenderer() {
        return this.cellRenderer;
    }

    public TableCellEditor getCellEditor() {
        return cellEditorGetter.getCellEditor();
    }

    public TableCellEditorGetter getCellEditorGetter() {
        return cellEditorGetter;
    }

    public boolean isHasData() {
        return hasData;
    }
}
