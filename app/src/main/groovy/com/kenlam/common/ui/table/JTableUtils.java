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

import com.google.common.collect.Streams;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kenlam.common.SimpleLog.commonLog;

public class JTableUtils {
    public static void addAutoHeightListenersToModelCentricJTable(ModelCentricJTable table) {
        AbstractModelCentricTableModel model = (AbstractModelCentricTableModel) table.getModel();
        model.addTableModelListener((TableModelEvent event) -> {
            int firstRow = event.getFirstRow();
            int lastRow = event.getLastRow();
            // commonLog("TableModelEvent - firstRow " + firstRow + ", lastRow " + lastRow + ", type " + event.getType());
            if (event.getType() != TableModelEvent.DELETE) {
                int rowCount = model.getRowCount();
                // commonLog("TableModelEvent - rowCount: " + rowCount);
                int maxRowExclusive = Math.min(rowCount, (lastRow >= Integer.MAX_VALUE ? lastRow : lastRow + 1));
                // commonLog("TableModelEvent - maxRowExclusive: " + maxRowExclusive);
                IntStream.range(firstRow, maxRowExclusive).forEachOrdered((int modelRowIndexInt) -> {
                    // commonLog("Table model change event - modelRowIndexInt: " + modelRowIndexInt);

                    TableModelRowIndex modelRowIndex = new TableModelRowIndex(modelRowIndexInt);

                    TableViewRowIndex viewRowIndex = new TableViewRowIndex(table.convertRowIndexToView(modelRowIndexInt));

                    List<TableModelColumnMeta> columnMetas = table.modelCentricTableModel.columnMetas;

                /*
                    Call the renderer for each cell to get their preferred heights.
                    The renderers must be subclasses of AbstractModelCentricJTableCellRenderer to support that.
                    Note that this won't actually do the rendering.
                 */
                    List<Integer> allPreferredHeights =
                            Streams.mapWithIndex(columnMetas.stream(), (TableModelColumnMeta columnMeta, long modelColumnIndexLong) -> {
                                TableModelColumnIndex modelColumnIndex = new TableModelColumnIndex((int) modelColumnIndexLong);
                                TableViewColumnIndex viewColumnIndex = new TableViewColumnIndex(table.convertColumnIndexToView(modelColumnIndex.Value));
                                AbstractModelCentricJTableCellRenderer cellRenderer = (AbstractModelCentricJTableCellRenderer) columnMeta.getCellRenderer();
                                Object cellValue = model.getValueAt(modelRowIndexInt, (int) modelColumnIndexLong);
                                JTableRendererReturnValues rendererReturnValues = cellRenderer.renderTableCellComponent(table,
                                        cellValue, false, false,
                                        columnMeta, modelRowIndex, modelColumnIndex, viewRowIndex, viewColumnIndex);
                                // commonLog("  modelColumnIndexLong: " + modelColumnIndexLong +
                                //         ", rendererReturnValues.preferredHeight: " + rendererReturnValues.preferredHeight);
                                return rendererReturnValues.preferredHeight;
                            })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                    // commonLog("allPreferredHeights - " + allPreferredHeights);
                    if (allPreferredHeights.size() > 0) {
                        Optional<Integer> maxPreferredHeightOption = allPreferredHeights.stream().max(Comparator.naturalOrder());
                        Integer maxPreferredHeight = maxPreferredHeightOption.get();  // Should always exist, after filtering null and checking for list emptiness

                        /*
                           If call table.setRowHeight() immediately,
                           when I load a new project and load the old classpaths,
                           and after I switch tab to project classpaths panel,
                           the row height doesn't reflect the value I set here.
                           I don't know why.
                         */
                        SwingUtilities.invokeLater(() -> {
                            // commonLog("  Going to setRowHeight modelRowIndexInt: " + modelRowIndexInt + ", maxPreferredHeight: " + maxPreferredHeight);
                            TableViewRowIndex _viewRowIndex = new TableViewRowIndex(table.convertRowIndexToView(modelRowIndex.Value));
                            table.setRowHeight(_viewRowIndex.Value, maxPreferredHeight);  // After some testing by sorting, I know it takes in the view row index rather than the model row index.
                        });
                    }
                });
            }
        });
    }
}
