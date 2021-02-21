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

import com.google.common.base.Strings;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.kenlam.common.SimpleLog.commonLog;

public class StringListAsTextAreaEditor extends AbstractModelCentricJTableCellEditor {
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, TableModelRowIndex modelRowIndex, TableModelColumnIndex modelColumnIndex, TableViewRowIndex viewRowIndex, TableViewColumnIndex viewColumnIndex) {
        textArea = new JTextArea();
        if (value != null) {
            List<String> classPaths = (List<String>) value;
            this.textArea.setText(String.join("\n", classPaths));
        }

        return this.textArea;
    }

    @Override
    public Object getCellEditorValue() {
        // commonLog("getCellEditorValue()");
        List<String> lines = Arrays.stream(textArea.getText().split("\n"))
                .map(line -> line != null ? line.trim() : null)
                .filter(line -> !Strings.isNullOrEmpty(line)).collect(Collectors.toList());

        return lines;
    }

    protected JTextArea textArea;
}
