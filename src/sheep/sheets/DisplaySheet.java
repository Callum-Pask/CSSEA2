package sheep.sheets;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.core.UpdateResponse;
import sheep.core.ViewElement;
import sheep.expression.Expression;
import sheep.parsing.ParseException;
import sheep.parsing.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Spreadsheet that displays the expressions it holds without evaluating the expressions.
 * <p>
 * A display sheet can hold expressions, but it must not attempt to evaluate the expression
 * to a value.
 * <p>
 * For example, if the expression {@code A1} is inserted, then {@code A1} must be displayed
 * verbatim rather than displaying the value stored in {@code A1}.
 * @stage1
 */
public class DisplaySheet implements SheetView, SheetUpdate {
    private final Parser parser;
    private final int rows;
    private final int columns;
    private final Map<CellLocation, Expression> contents = new HashMap<>();

    /**
     * Construct a new display spreadsheet of the specified size.
     *
     * @param parser A parser to use for parsing any updates to the sheet.
     * @param defaultExpression The default expression with which to populate the empty sheet.
     * @param rows Amount of rows for the new sheet.
     * @param columns Amount of columns for the new sheet.
     * @requires rows &gt; 0
     * @requires columns &gt; 0
     */
    public DisplaySheet(Parser parser, Expression defaultExpression, int rows, int columns) {
        this.parser = parser;
        this.rows = rows;
        this.columns = columns;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                contents.put(new CellLocation(row, column), defaultExpression);
            }
        }
    }

    /**
     * Attempt to update a cell in the position.
     * <p>
     * The input string will attempt to be parsed using the sheet's parser.
     * If the string cannot be parsed,
     * then the update response must fail with "Unable to parse: [input]".
     * otherwise the sheet must be updated to render the parsed expression.
     * <p>
     * Display sheets must not attempt to evaluate the parsed expressions.
     *
     * <pre>
     * {@code
     * UpdateResponse response = sheet.update(0, 0, "%.!");
     * response.isSuccess(); // false
     * response.getMessage(); // "Unable to parse: %.!"
     * }</pre>
     *
     * @param row The row index to update.
     * @param column The column index to update.
     * @param input The value as a string to replace within the sheet.
     * @require 0 &leq; row &lt; {@link DisplaySheet#getRows()}
     * @require 0 &leq; column &lt; {@link DisplaySheet#getColumns()}
     * @return Whether the update was successful or not with error details.
     */
    @Override
    public UpdateResponse update(int row, int column, String input) {
        try {
            Expression expr = parser.parse(input);
            contents.put(new CellLocation(row, column), expr);
            return UpdateResponse.success();
        } catch (ParseException e) {
            return UpdateResponse.fail("Unable to parse: " + input);
        }
    }

    /**
     * The number of rows in the sheet.
     * @return Amount of rows in the sheet.
     */
    @Override
    public int getRows() {
        return rows;
    }

    /**
     * The number of columns in the sheet.
     * @return Amount of columns in the sheet.
     */
    @Override
    public int getColumns() {
        return columns;
    }

    /**
     * Determine the value to display at this cell.
     * <p>
     * The value to display is either the default expression passed to the constructor,
     * or, if a cell has been successfully updated by {@link DisplaySheet#update(int, int, String)},
     * the expression constructed by the input string.
     * The content must match the {@link Expression#render()} of the corresponding expression.
     * <p>
     * The background and foreground colours may be any appropriate combination.
     *
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @require 0 &leq; row &lt; {@link DisplaySheet#getRows()}
     * @require 0 &leq; column &lt; {@link DisplaySheet#getColumns()}
     * @return A {@link ViewElement} that details how to render the cell's formula.
     */
    @Override
    public ViewElement valueAt(int row, int column) {
        Expression expr = contents.get(new CellLocation(row, column));
        return new ViewElement(expr.render(), "white", "black");
    }

    /**
     * Determine the formula to display at this cell.
     * <p>
     * For any cell, a display sheet will render the same formula and value
     * as formulae are not evaluated to a value (see {@link DisplaySheet#valueAt(int, int)}).
     *
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @require 0 &leq; row &lt; {@link DisplaySheet#getRows()}
     * @require 0 &leq; column &lt; {@link DisplaySheet#getColumns()}
     * @return A {@link ViewElement} that details how to render the cell's formula.
     */
    @Override
    public ViewElement formulaAt(int row, int column) {
        return valueAt(row, column);
    }
}
