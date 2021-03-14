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

import com.kenlam.common.ui.table.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;

public abstract class AbstractModelCentricJTableCellPopupEditor extends AbstractModelCentricJTableCellEditor {
    /*
        Why require renderer here:
        As a dummy, read-only editor to return to the table.

        For the editor, why not reuse com.kenlam.common.ui.table.StringListAsTextAreaEditor:
        Because the text area layout is not the same when used in table cell and when used in a separate popup window.
     */
    protected final TableCellRendererGetter rendererGetter;
    private TableEditorPopupDialog popup;
    // private JTextArea dummyEditorComponent;

    /*
        To-do list:
        1. Allow to change popup title
        2. Allow to set the popup width
        3. Consider whether to focus on the table or cell after edit
     */

    public AbstractModelCentricJTableCellPopupEditor(TableCellRendererGetter rendererGetter) {
        this.rendererGetter = rendererGetter;

        // setClickCountToStart(1);   // Perhaps we could set requiring double-click to edit instead of singe-click in the future.
    }

    protected abstract Component getTableCellEditorComponentInsideDialog(JTable table, Object value, boolean isSelected,
                                                                         TableModelRowIndex modelRowIndex, TableModelColumnIndex modelColumnIndex,
                                                                         TableViewRowIndex viewRowIndex, TableViewColumnIndex viewColumnIndex);

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 TableModelRowIndex modelRowIndex, TableModelColumnIndex modelColumnIndex,
                                                 TableViewRowIndex viewRowIndex, TableViewColumnIndex viewColumnIndex) {

        Component editorComponent = this.getTableCellEditorComponentInsideDialog(table, value, isSelected,
                modelRowIndex, modelColumnIndex, viewRowIndex, viewColumnIndex);

        TableCellRenderer renderer = rendererGetter.getCellRenderer();

        Component renderedComponent = renderer.getTableCellRendererComponent(table, value, isSelected, false,
                viewRowIndex.Value, viewColumnIndex.Value);

        //  Set up the dialog where we do the actual editing
        popup = new TableEditorPopupDialog(editorComponent, this);

        SwingUtilities.invokeLater(() -> {
            // popup.setText(currentText);
            //              popup.setLocationRelativeTo( editorComponent );
            Point p = renderedComponent.getLocationOnScreen();
            popup.setLocation(p.x, p.y + renderedComponent.getSize().height);
            popup.setVisible(true);
            fireEditingStopped();
        });

        // renderedComponent.setText("frog");
        // renderedComponent.setEditable(false);
        // renderedComponent.setBounds(table.getCellRect(viewRowIndex.Value, viewColumnIndex.Value, true));
        // renderedComponent.setBackground(Color.white);
        // dummyEditorComponent.setBorderPainted(false);
        // dummyEditorComponent.setContentAreaFilled(false);

        // Make sure focus goes back to the table when the dialog is closed
        renderedComponent.setFocusable(false);
        return renderedComponent;
    }


    @Override
    public Object getCellEditorValue() {
        Component lastEditorComponent = popup != null ? popup.getEditorComponent() : null;
        Object processedCellEditorValue = processCellEditorValue(lastEditorComponent);
        return processedCellEditorValue;
    }

    protected abstract Object processCellEditorValue(Component lastEditorComponent);


}

class TableEditorPopupDialog extends JDialog {
    protected final Component editorComponent;

    protected final TableCellEditor editor;

    public TableEditorPopupDialog(Component editorComponent, TableCellEditor editor) {
        super((Frame) null, "Change Description", true);

        this.editorComponent = editorComponent;
        this.editor = editor;

        // textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(editorComponent);
        getContentPane().add(scrollPane);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((ActionEvent actionEvent) -> {
            this.setVisible(false);
            // this.editorComponent.requestFocusInWindow();
            this.editor.cancelCellEditing();
            this.dispose();
        });
        JButton ok = new JButton("Ok");
        ok.setPreferredSize(cancel.getPreferredSize());
        ok.addActionListener((ActionEvent actionEvent) -> {
            this.setVisible(false);
            // this.editorComponent.requestFocusInWindow();
            this.dispose();
        });

        JPanel buttons = new JPanel();
        buttons.add(ok);
        buttons.add(cancel);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();

        getRootPane().setDefaultButton(ok);
    }

    public Component getEditorComponent() {
        return editorComponent;
    }

    public TableCellEditor getEditor() {
        return editor;
    }

}
