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

import groovy.lang.Tuple2;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModelCentricTableModel extends AbstractTableModel {
    protected final List<TableModelColumnMeta> columnMetas;

    protected final Map<Tuple2<TableModelRowIndex, TableModelColumnIndex>, TableModelCellCachedData> cachedDataByCell = new LinkedHashMap<>();

    protected AbstractModelCentricTableModel(List<TableModelColumnMeta> columnMetas) {
        this.columnMetas = Collections.unmodifiableList(columnMetas);
    }

    @Override
    public int getColumnCount() {
        return columnMetas.size();
    }

    public abstract void doSetValueAt(Object newValue, int rowIndex, int columnIndex);

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        doSetValueAt(aValue, row, column);
        fireTableCellUpdated(row, column);
    }

    public TableModelColumnMeta getColumnMetaByIndex(TableModelColumnIndex columnIndex) {
        return columnMetas.get(columnIndex.Value);
    }

    public String getFieldNameByColumnIndex(TableModelColumnIndex columnIndex) {
        return this.getColumnMetaByIndex(columnIndex).getFieldName();
    }

    public TableModelCellCachedData getCachedDataForCell(TableModelRowIndex rowIndex, TableModelColumnIndex columnIndex) {
        return cachedDataByCell.get(new Tuple2<>(rowIndex, columnIndex));
    }

    public void setCachedDataForCell(TableModelRowIndex rowIndex, TableModelColumnIndex columnIndex, TableModelCellCachedData cachedData) {
        cachedDataByCell.put(new Tuple2<>(rowIndex, columnIndex), cachedData);
    }
}
