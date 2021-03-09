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
import java.util.List;

import static com.kenlam.common.SimpleLog.commonLog;

public class StringListAsTextAreaCellRenderer extends AbstractModelCentricJTableCellRenderer {
    // public StringListAsTextAreaCellRenderer(String fieldName) {
    //     super(fieldName);
    // }

    @Override
    public JTableRendererReturnValues renderTableCellComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus,
                                                               TableModelColumnMeta columnMeta,
                                                               TableModelRowIndex modelRowIndex,
                                                               TableModelColumnIndex modelColumnIndex,
                                                               TableViewRowIndex viewRowIndex,
                                                               TableViewColumnIndex viewColumnIndex) {
        // commonLog("renderTableCellComponent - table: " + table.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(table)));
        // commonLog("renderTableCellComponent - field: " + columnMeta.getFieldName() + ", value: " + value +
        //         ", modelRowIndex: " + modelRowIndex + ", modelColumnIndex:" + modelColumnIndex);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        List<String> classPaths = (List<String>) value;
        if (value != null) {
            textArea.setText(String.join("\n", classPaths));
        }
        textArea.setBounds(table.getCellRect(viewRowIndex.Value, viewColumnIndex.Value, true));
        int preferredHeight = textArea.getPreferredSize().height;
        // commonLog("renderTableCellComponent - preferredHeight :" + preferredHeight);
        JTableRendererReturnValues returnValues = new JTableRendererReturnValues(textArea, preferredHeight);
        return returnValues;
    }

}
