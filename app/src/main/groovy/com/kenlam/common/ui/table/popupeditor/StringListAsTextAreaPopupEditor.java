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

package com.kenlam.common.ui.table.popupeditor;

import com.google.common.base.Strings;
import com.kenlam.common.ui.table.*;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringListAsTextAreaPopupEditor extends AbstractModelCentricJTableCellPopupEditor {

    public StringListAsTextAreaPopupEditor(PopupDialogModifier popupDialogModifier) {
        super(() -> {
                    return new StringListAsTextAreaCellRenderer();
                },
                popupDialogModifier);
    }

    @Override
    protected Component getTableCellEditorComponentInsideDialog(JTable table, Object value, boolean isSelected,
                                                                TableModelRowIndex modelRowIndex, TableModelColumnIndex modelColumnIndex,
                                                                TableViewRowIndex viewRowIndex, TableViewColumnIndex viewColumnIndex) {
        JTextArea textArea = new JTextArea();
        if (value != null) {
            java.util.List<String> classPaths = (List<String>) value;
            textArea.setText(String.join("\n", classPaths));
        }

        return textArea;
    }

    @Override
    protected Object processCellEditorValue(Component lastEditorComponent) {
        JTextArea editorTextArea = (JTextArea) lastEditorComponent;
        List<String> lines = Arrays.stream(editorTextArea.getText().split("\n"))
                .map(line -> line != null ? line.trim() : null)
                .filter(line -> !Strings.isNullOrEmpty(line)).collect(Collectors.toList());

        return lines;
    }
}
