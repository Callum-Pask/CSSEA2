package sheep.ui.graphical;

import sheep.core.SheetView;
import sheep.core.ViewElement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * A custom cell renderer that uses the {@link SheetView} instance
 * to lookup the value to render based on the {@link Location}
 * stored in the table model.
 */
class CellRenderer extends DefaultTableCellRenderer {

    private final SheetView view;

    /**
     * Construct a new cell renderer.
     * @param view The view to use for rendering values.
     */
    public CellRenderer(SheetView view) {
        this.view = view;
    }

    /**
     * Use reflection to grab a {@link Color} by its name.
     * @param name The name of the colour.
     * @return A colour based on the given colour name.
     */
    public static Color getColorByName(String name) {
        try {
            return (Color) Color.class.getField(name.toUpperCase()).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component result = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        if (column == 0 || row == 0) {
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground(Configuration.HEADER_COLUMN_BACKGROUND);
            setForeground(Configuration.HEADER_COLUMN_FOREGROUND);
        }
        if (row > 0 && column > 0) {
            setHorizontalAlignment(SwingConstants.LEFT);
            ViewElement element = view.valueAt(row - 1, column - 1);
            setBackground(getColorByName(element.getBackground()));
            setForeground(getColorByName(element.getForeground()));
        }
        return result;
    }

    /**
     * Intercept the call to setValue to use the {@link SheetView} value
     * if a {@link Location} instance is called, otherwise, use the default.
     * Additionally, sets the tooltip so that long values can be previewed.
     */
    @Override
    protected void setValue(Object value) {
        if (value instanceof Location location) {
            String rendered = view.valueAt(location.row(), location.column()).getContent();
            setText(rendered);
            setToolTipText(rendered);
            return;
        }
        super.setValue(value);
    }
}
