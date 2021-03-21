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
import javax.swing.WindowConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;

public abstract class AbstractModelCentricJTableCellPopupEditor extends AbstractModelCentricJTableCellEditor {
    /*
        Why require renderer here:
        As a dummy, read-only editor to return to the table.

        For the editor, why not reuse com.kenlam.common.ui.table.StringListAsTextAreaEditor:
        Because the text area layout is not the same when used in table cell and when used in a separate popup window.
     */
    protected final TableCellRenderer renderer;
    protected final PopupDialogModifier popupDialogModifier;
    private TableEditorPopupDialog popup;
    // private JTextArea dummyEditorComponent;

    public AbstractModelCentricJTableCellPopupEditor(TableCellRenderer renderer, PopupDialogModifier popupDialogModifier) {
        this.renderer = renderer;
        this.popupDialogModifier = popupDialogModifier;

        // setClickCountToStart(1);   // Perhaps we could set requiring double-click to edit instead of singe-click in the future.
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        // commonLog("isCellEditable - event: " + event);
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                return super.isCellEditable(mouseEvent);
            }
        }
        // commonLog("isCellEditable -   will return false");
        return false;
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

        TableCellRenderer renderer = this.renderer;

        Component renderedComponent = renderer.getTableCellRendererComponent(table, value, isSelected, false,
                viewRowIndex.Value, viewColumnIndex.Value);

        //  Set up the dialog where we do the actual editing
        popup = new TableEditorPopupDialog(editorComponent, this);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = gd.getDisplayMode();
        int screenWidth = displayMode.getWidth();
        int screenHeight = displayMode.getHeight();
        int popupWidth = Math.min(screenWidth * 2 / 3, 1600);
        int popupHeight = Math.min(screenHeight * 2 / 3, 200);
        popup.setPreferredSize(new Dimension(popupWidth, popupHeight));

        SwingUtilities.invokeLater(() -> {
            // popup.setText(currentText);
            //              popup.setLocationRelativeTo( editorComponent );
            Point p = renderedComponent.getLocationOnScreen();
            popup.setLocation(Math.min(p.x, screenWidth - popupWidth),
                    Math.min(p.y + renderedComponent.getSize().height, screenHeight - popupHeight));
            popup.pack();
            popup.setVisible(true);
            fireEditingStopped();
        });

        if (this.popupDialogModifier != null) {
            this.popupDialogModifier.modifyPopup(popup);
        }

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

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // commonLog("TableEditorPopupDialog closing");
                // Please test that the Ok and Cancel buttons won't trigger this event, while the close (X) button will.
                TableEditorPopupDialog.this.editor.cancelCellEditing();
                super.windowClosing(e);
            }

            // @Override
            // public void windowClosed(WindowEvent e) {
            //     commonLog("TableEditorPopupDialog closed");
            //     super.windowClosed(e);
            // }
        });
    }

    public Component getEditorComponent() {
        return editorComponent;
    }

    public TableCellEditor getEditor() {
        return editor;
    }

}
