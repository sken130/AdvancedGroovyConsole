package com.kenlam.common.ui.table.popupeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.table.TableCellEditor;

public class TableEditorPopupDialog extends JDialog {
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
