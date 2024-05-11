package sheep.ui.graphical;

import sheep.core.SheetView;

import javax.swing.*;
import java.awt.*;

/**
 * A custom cell editor that when an edit box is opened
 * on a cell containing a {@link Location},
 * will look up the formula at the location
 * using the given {@link SheetView}.
 */
class CellEditor extends DefaultCellEditor {
    private final JTextField field;
    private final SheetView view;

    /**
     * Construct a new custom cell editor.
     *
     * @param view The view to use to lookup the formula.
     * @param field The default text field to use for the edit box.
     */
    public CellEditor(SheetView view, JTextField field) {
        super(field);
        this.view = view;
        this.field = field;
    }

    /**
     * Extends the existing cell editor component,
     * replacing any stored {@link Location} instances
     * with their formula in the {@link SheetView}.
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        Component comp = super.getTableCellEditorComponent(table, value,
                isSelected, row, column);
        if (value instanceof Location location) {
            field.setText(view.formulaAt(location.row(), location.column()).getContent());
        }
        return comp;
    }
}