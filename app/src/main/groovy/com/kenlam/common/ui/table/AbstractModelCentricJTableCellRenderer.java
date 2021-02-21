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
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public abstract class AbstractModelCentricJTableCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        AbstractModelCentricTableModel modelCentricTableModel = (AbstractModelCentricTableModel) table.getModel();
        TableViewRowIndex viewRowIndex = new TableViewRowIndex(row);
        TableViewColumnIndex viewColumnIndex = new TableViewColumnIndex(column);
        TableModelColumnIndex modelColumnIndex = new TableModelColumnIndex(table.convertColumnIndexToModel(column));
        TableModelRowIndex modelRowIndex = new TableModelRowIndex(table.convertRowIndexToModel(viewRowIndex.Value));

        TableModelColumnMeta columnMeta = modelCentricTableModel.getColumnMetaByIndex(modelColumnIndex);

        JTableRendererReturnValues rendererReturnValues = this.renderTableCellComponent(table, value, isSelected, hasFocus,
                columnMeta, modelRowIndex, modelColumnIndex, viewRowIndex, viewColumnIndex);
        Component renderedComponent = rendererReturnValues.component;

        // TableModelCellCachedData cachedData = new TableModelCellCachedData(modelRowIndex, modelColumnIndex);
        // cachedData.setPreferredHeight(rendererReturnValues.preferredHeight);
        // modelCentricTableModel.setCachedDataForCell(modelRowIndex, modelColumnIndex, cachedData);

        return renderedComponent;
    }

    public abstract JTableRendererReturnValues renderTableCellComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                                        TableModelColumnMeta columnMeta,
                                                                        TableModelRowIndex modelRowIndex,
                                                                        TableModelColumnIndex modelColumnIndex,
                                                                        TableViewRowIndex viewRowIndex,
                                                                        TableViewColumnIndex viewColumnIndex);
}
