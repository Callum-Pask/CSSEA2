package sheep.sheets;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.core.UpdateResponse;
import sheep.core.ViewElement;

/**
 * Spreadsheet that has fixed values in every cell.
 * Useful for initial development.
 * @stage0
 */
public class FixedSheet implements SheetView, SheetUpdate {
    /**
     * Construct an instance of a fixed sheet.
     * @hint By default Java will create an empty constructor for all classes,
     * so if your constructor does nothing, you do not need to create one.
     */
    public FixedSheet() {

    }

    /**
     * Attempt to update a cell in the position.
     * <p>
     * Whenever the update method is called on a fixed sheet,
     * it must report that the update failed because the
     * sheet is view only.
     * <p>
     * The returned {@link UpdateResponse} must fail because
     * "Sheet is view only.".
     *
     * <pre>
     * {@code
     * FixedSheet sheet = new FixedSheet();
     * UpdateResponse response = sheet.update(1, 1, "10");
     * response.isSuccess(); // false
     * response.getMessage(); // "Sheet is view only."
     * }</pre>
     *
     * @param row The row index to update.
     * @param column The column index to update.
     * @param input The value as a string to replace within the sheet.
     * @require 0 &leq; row &lt; {@link FixedSheet#getRows()}
     * @require 0 &leq; column &lt; {@link FixedSheet#getColumns()}
     * @require input != null
     * @return A failed update as the sheet is view only.
     *
     * @hint {@link UpdateResponse} only has a private constructor, this means
     * that one of its public static methods: {@link UpdateResponse#fail(String)}
     * or {@link UpdateResponse#success()} should be used to construct an instance.
     */
    @Override
    public UpdateResponse update(int row, int column, String input) {
        return UpdateResponse.fail("Sheet is view only.");
    }

    /**
     * The number of columns in the sheet.
     * A fixed sheet will always have exactly 6 columns.
     *
     * <pre>
     * {@code
     * FixedSheet sheet = new FixedSheet();
     * sheet.getColumns(); // 6
     * }</pre>
     *
     * @return 6
     */
    @Override
    public int getColumns() {
        return 6;
    }

    /**
     * The number of rows in the sheet.
     * A fixed sheet will always have exactly 6 rows.
     *
     * <pre>
     * {@code
     * FixedSheet sheet = new FixedSheet();
     * sheet.getRows(); // 6
     * }</pre>
     *
     * @return 6
     */
    @Override
    public int getRows() {
        return 6;
    }

    private boolean isHighlighted(int row, int column) {
        if (row >= 2 && row <= 3) {
            return column >= 2 && column <= 3;
        }
        return false;
    }

    /**
     * Determine the value to display at this cell.
     * <p>
     * A fixed sheet has two types of cells, highlighted and unhighlighted.
     * <p>
     * Highlighted cells should contain the string "W" and have a green background colour.
     * Unhighlighted cells should have no content and a white background colour.
     * Both cells types should have a black foreground.
     * <p>
     * Highlighted cells are between rows 2 and 3 (inclusive)
     * and columns 2 and 3 (inclusive), that is, there are 4 highlighted cells total.
     *
     * <pre>
     * {@code
     * FixedSheet sheet = new FixedSheet();
     * ViewElement element = sheet.valueAt(0, 0);
     * element.getContent(); // ""
     * element.getBackground(); // "white"
     * element.getForeground(); // "black"
     * element = sheet.valueAt(2, 2);
     * element.getContent(); // "W"
     * element.getBackground(); // "green"
     * element.getForeground(); // "black"
     * }</pre>
     *
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @require 0 &leq; row &lt; {@link FixedSheet#getRows()}
     * @require 0 &leq; column &lt; {@link FixedSheet#getColumns()}
     * @return An appropriately formatted cell based on whether it is highlighted or not.
     */
    @Override
    public ViewElement valueAt(int row, int column) {
        if (isHighlighted(row, column)) {
            return new ViewElement("W", "green", "black");
        }
        return new ViewElement("", "white", "black");
    }

    /**
     * Determine the formula to display at this cell.
     * <p>
     * A fixed sheet has two types of cells, highlighted and unhighlighted,
     * as per the description of {@link FixedSheet#valueAt(int, int)}.
     * <p>
     * In contrast to the values, formulas of highlighted cells
     * must have the text "GREEN" rather than "W".
     *
     * <pre>
     * {@code
     * FixedSheet sheet = new FixedSheet();
     * ViewElement element = sheet.formulaAt(0, 0);
     * element.getContent(); // ""
     * element.getBackground(); // "white"
     * element.getForeground(); // "black"
     * element = sheet.formulaAt(2, 2);
     * element.getContent(); // "GREEN"
     * element.getBackground(); // "green"
     * element.getForeground(); // "black"
     * }</pre>
     *
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @require 0 &leq; row &lt; {@link FixedSheet#getRows()}
     * @require 0 &leq; column &lt; {@link FixedSheet#getColumns()}
     * @return An appropriately formatted cell based on whether it is highlighted or not.
     */
    @Override
    public ViewElement formulaAt(int row, int column) {
        if (isHighlighted(row, column)) {
            return new ViewElement("GREEN", "green", "black");
        }
        return new ViewElement("", "white", "black");
    }
}
