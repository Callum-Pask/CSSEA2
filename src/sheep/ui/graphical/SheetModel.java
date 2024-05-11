package sheep.ui.graphical;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.core.UpdateResponse;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link TableModel} that stores
 * {@link Location} elements so that the renderer can query the
 * {@link SheetView}.
 * When a cell is updated, {@link SheetUpdate} is invoked to
 * update the underlying sheet model.
 * A popup is displayed if the update is invalid.
 */
class SheetModel implements TableModel {
    private final JFrame parent;
    private final SheetView view;
    private final SheetUpdate updater;

    private final List<TableModelListener> listeners = new ArrayList<>();

    /**
     * Create a new sheet model.
     *
     * @param parent The window the table is in, used to create a popup.
     * @param view The view to render, this is used to calculate the model dimensions.
     * @param updater The updater to use when the model is edited.
     */
    public SheetModel(JFrame parent, SheetView view, SheetUpdate updater) {
        this.parent = parent;
        this.view = view;
        this.updater = updater;
    }

    @Override
    public int getRowCount() {
        // Offset by one to account for headers.
        return view.getRows() + 1;
    }

    @Override
    public int getColumnCount() {
        // Offset by one to account for headers.
        return view.getColumns() + 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return "" + columnIndex;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Location.class;
    }

    /**
     * Editable if the cell is not a header (i.e. row = 0 or column = 0).
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return rowIndex > 0 && columnIndex > 0;
    }

    /**
     * Retrieve the value stored in the model at the given row and column.
     * <p>
     * If the cell is a header, the string value of that header is returned.
     * Otherwise, a {@link Location} representing the cell is returned.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == 0 && columnIndex == 0) {
            return "";
        }
        if (rowIndex == 0) {
            return Character.toString(columnIndex + 64);
        }
        if (columnIndex == 0) {
            return rowIndex - 1;
        }

        return new Location(rowIndex - 1, columnIndex - 1);
    }

    /**
     * When a value is updated, use the {@link SheetUpdate} to try to update
     * the underlying model.
     * If the update is unsuccessful, render a message box and prevent exiting
     * the editing mode by throwing a runtime exception.
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue instanceof String value) {
            UpdateResponse response = updater.update(rowIndex - 1, columnIndex - 1, value);
            if  (!response.isSuccess()) {
                JOptionPane.showMessageDialog(parent, response.getMessage());
                throw new RuntimeException();
            }
        }

        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
}