package sheep.sheets;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.core.UpdateResponse;
import sheep.core.ViewElement;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.parsing.ParseException;
import sheep.parsing.Parser;

import java.util.*;

/**
 * Spreadsheet that evaluates its expressions and updates dependant cells.
 * Sheet is an implementation of a spreadsheet capable of evaluating its expressions.
 * <p>
 * A sheet consists of cells in a fixed number of rows and columns.
 * Each cell location of a spreadsheet has a formula and a value.
 * The formula is what has been written in the cell by a user
 * whereas the value is what value the cell contains.
 * @stage2
 */
public class Sheet implements SheetView, SheetUpdate {

    private final Map<CellLocation, Expression> formulas = new HashMap<>();
    private final Map<CellLocation, Expression> values = new HashMap<>();
    private final Map<CellLocation, Set<CellLocation>> usages = new HashMap<>();
    private final Map<String, Expression> builtins;
    private final Expression defaultExpression;
    private int rows;
    private int columns;

    private final Parser parser;

    /**
     * Construct a new instance of the sheet class.
     * <p>
     * A sheet should initially be populated in every cell with the defaultExpression.
     *
     * @param parser The parser instance used to create expressions.
     * @param builtins A mapping of built-in identifiers to expressions.
     * @param defaultExpression The default expression to load in every cell.
     * @param rows Amount of rows for the new sheet.
     * @param columns Amount of columns for the new sheet.
     * @requires rows &gt; 0
     * @requires columns &gt; 0 &amp;&amp; columns &lt; 26
     */
    protected Sheet(Parser parser, Map<String, Expression> builtins,
          Expression defaultExpression, int rows, int columns) {
        this.parser = parser;
        this.rows = rows;
        this.columns = columns;
        this.defaultExpression = defaultExpression;
        this.builtins = new HashMap<>(builtins);
        this.populate();
    }

    /**
     * Clear the current sheet so that it contains the default
     * formula in every cell.
     */
    public void clear() {
        populate();
    }

    private void populate() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                CellLocation location = new CellLocation(i, j);
                populate(location);
            }
        }
    }

    private void populate(CellLocation location) {
        values.put(location, defaultExpression);
        formulas.put(location, defaultExpression);
        usages.put(location, new HashSet<>());
    }

    /**
     * Update the dimensions of the spreadsheet.
     * If the spreadsheet shrinks, the values of removed cells are remembered
     * and may be accessed if the spreadsheet grows again.
     * If the spreadsheet grows, the default values are populated.
     *
     * @param rows New number of rows.
     * @param columns New number of columns.
     */
    public void updateDimensions(int rows, int columns) {
        for (int newRow = 0; newRow < rows - this.rows; newRow++) {
            for (int column = 0; column < columns; column++) {
                populate(new CellLocation(newRow + this.rows, column));
            }
        }
        for (int newCol = 0; newCol < columns - this.columns; newCol++) {
            for (int row = 0; row < columns; row++) {
                populate(new CellLocation(row, newCol + this.columns));
            }
        }
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * The number of rows for this spreadsheet.
     * @return The number of rows for this spreadsheet.
     */
    @Override
    public int getRows() {
        return rows;
    }

    /**
     * The number of columns for this spreadsheet.
     * @return The number of columns for this spreadsheet.
     */
    @Override
    public int getColumns() {
        return columns;
    }

    /**
     * Whether the given cell location exists within the sheet.
     *
     * @param location A cell location to check.
     * @return true if the cell location is within the sheet bounds.
     */
    public boolean contains(CellLocation location) {
        if (location.getRow() >= 0 && location.getColumn() >= 0) {
            return location.getColumn() < getColumns()
                    && location.getRow() < getRows();
        }
        return false;
    }

    /**
     * The value to render at this location.
     * The content of the {@link ViewElement} should correspond to the result
     * of the {@link Expression#render()} method on {@link #valueAt(CellLocation)}.
     * The foreground and background colours may be any compatible colours.
     *
     * @param row A row within the spreadsheet.
     * @param column A column within the spreadsheet.
     * @requires location is within the bounds (row/columns) of the spreadsheet.
     * @return The value to render at this location.
     */
    @Override
    public ViewElement valueAt(int row, int column) {
        return new ViewElement(valueAt(new CellLocation(row, column)).render(), "white", "black");
    }

    /**
     * The formula to render at this location.
     * The content of the {@link ViewElement} should correspond to the result
     * of the {@link Expression#render()} method on {@link #formulaAt(CellLocation)}.
     * The foreground and background colours may be any compatible colours.
     *
     * @param row A row within the spreadsheet.
     * @param column A column within the spreadsheet.
     * @requires location is within the bounds (row/columns) of the spreadsheet.
     * @return The formula to render at this location.
     */
    @Override
    public ViewElement formulaAt(int row, int column) {
        return new ViewElement(formulaAt(new CellLocation(row, column)).render(), "white", "black");
    }

    /**
     * Attempt to update the cell at row and column within the sheet
     * with the given input.
     * <p>
     * The input string will be parsed using the sheet's {@link Parser}.
     * If the string cannot be parsed,
     * then the update response must fail with "Unable to parse: [input]".
     * <p>
     * Once parsed, the method should function the same as {@link #update(CellLocation, Expression)}.
     * If a {@link TypeError} occurs,
     * then the update response must fail with "Type error: [e]"
     * where e is the result of calling {@link TypeError#toString()} on the thrown exception.
     * <p>
     * Otherwise, the spreadsheet should update as per {@link #update(CellLocation, Expression)}
     * and return a successful {@link UpdateResponse}.
     *
     * @param row The row index to update.
     * @param column The column index to update.
     * @param input The value as a string to replace within the sheet.
     * @return Information about the status of performing the update.
     */
    @Override
    public UpdateResponse update(int row, int column, String input) {
        try {
            Expression expr = parser.parse(input);
            update(new CellLocation(row, column), expr);
            return UpdateResponse.success();
        } catch (TypeError e) {
            return UpdateResponse.fail("Type error: " + e);
        } catch (ParseException e) {
            return UpdateResponse.fail("Unable to parse: " + input);
        }
    }

    /**
     * The formula expression currently stored at the location in the spreadsheet.
     * @param location A cell location within the spreadsheet.
     * @requires location is within the bounds (row/columns) of the spreadsheet.
     * @return The formula expression at the given cell location.
     */
    public Expression formulaAt(CellLocation location) {
        return formulas.get(location);
    }

    /**
     * The value expression currently stored at the location in the spreadsheet.
     * The value expression is the result of calling {@link Expression#value(Map)}
     * on the corresponding formula.
     * The {@link Expression#value(Map)} must not be called in this method,
     * it should be called when a formula is updated in {@link #update(CellLocation, Expression)}.
     *
     * @param location A cell location within the spreadsheet.
     * @requires location is within the bounds (row/columns) of the spreadsheet.
     * @return The value expression at the given cell location.
     */
    public Expression valueAt(CellLocation location) {
        return values.get(location);
    }

    /**
     * Determine which cells use the formula at the given cell location
     * <p>
     * That is, for a given location, find all the cells where the given location
     * is a transitive dependency for that cell.
     * <p>
     * For example, if the expressions at A1 and A2 have A3 as a dependency
     * then the result of this method for A3 should be a set containing A1 and A2.
     * If A3 has a dependency on A4, then A4 is used by A1, A2, and A3
     * because A4 is used by A3 to determine its value
     * which is transitively used by A1 and A2 to determine their values.
     * <pre>
     * {@code
     * CellLocation a1 = new CellLocation(1, 0);
     * CellLocation a2 = new CellLocation(2, 0);
     * CellLocation a3 = new CellLocation(3, 0);
     * CellLocation a4 = new CellLocation(4, 0);
     * sheet.formulaAt(a1).dependencies() // {a3}
     * sheet.formulaAt(a2).dependencies() // {a3}
     * sheet.formulaAt(a3).dependencies() // {a4}
     * sheet.usedBy(a3) // {a1, a2}
     * sheet.usedBy(a4) // {a1, a2, a3}
     * }</pre>
     *
     * @see Expression#dependencies()
     * @param location A cell location within the spreadsheet.
     * @requires location is within the bounds (row/columns) of the spreadsheet.
     * @return All the cells which use the given cell as a dependency.
     */
    public Set<CellLocation> usedBy(CellLocation location) {
        Set<CellLocation> usages = new HashSet<>();
        usedBy(location, usages);
        return usages;
    }

    private void usedBy(CellLocation location, Set<CellLocation> seen) {
        for (CellLocation next : usages.get(location)) {
            if (seen.contains(next)) {
                return;
            }
            seen.add(next);
            usedBy(next, seen);
        }
    }

    private Map<String, Expression> createState() {
        Map<String, Expression> symbols = new HashMap<>(builtins);
        for (CellLocation location : formulas.keySet()) {
            symbols.put(location.toString(), values.get(location));
        }
        return symbols;
    }

    /**
     * Insert an expression into a cell location, updating the sheet as required.
     * <p>
     * After calling this function, the spreadsheet should update such that
     * <ul>
     *     <li>The result of calling {@link Sheet#formulaAt(CellLocation)} for
     *     the given cell location returns the given expression.</li>
     *     <li>The result of calling {@link Sheet#valueAt(CellLocation)} for
     *     the given cell location returns the value of the given expression.</li>
     *     <li>Any cell that directly, or indirectly, utilizes the value of the
     *     given cell is updated such that calling {@link Sheet#valueAt(CellLocation)}
     *     will return an appropriate result for the new value at this cell.
     *     </li>
     * </ul>
     * <p>
     * If a {@link TypeError} is thrown at any point during the update of this cell or any dependant cells,
     * the sheet should return to the same state as before this method was called.
     * <p>
     * The behaviour of inserting a reference loop into the sheet,
     * e.g. A0 refers to A1, A1 refers to A2, A2 refers to A0 or B1 refers to B1,
     * is unspecified and will not be tested.
     *
     * @param location A cell location to insert the expression into the sheet.
     * @param cell An expression to insert at the given location.
     * @requires location is within the bounds (row/columns) of the spreadsheet.
     * @throws TypeError If the evaluation of the inserted cell or any of its usages
     *                  results in a TypeError being thrown.
     */
    public void update(CellLocation location, Expression cell) throws TypeError {
        Map<String, Expression> state = createState();
        state.put(location.toString(), cell.value(state));
        // recursively pre-calculate all the updated values
        // this approach triggers a TypeError before the state is modified
        applyUpdate(state, location);

        // no type error, update the state
        updateUsage(location, cell);
        formulas.put(location, cell);
        for (CellLocation existing : values.keySet()) {
            if (state.containsKey(existing.toString())) {
                values.put(existing, state.get(existing.toString()));
            }
        }
    }

    private void applyUpdate(Map<String, Expression> state, CellLocation location)
            throws TypeError {
        for (CellLocation usage : usages.get(location)) {
            Expression value = formulas.get(usage).value(state);
            state.put(usage.toString(), value);
            applyUpdate(state, usage);
        }
    }

    private void updateUsage(CellLocation location, Expression newExpression) {
        // remove all usages of old formula
        Expression oldExpression = formulas.get(location);
        for (String oldDep : oldExpression.dependencies()) {
            Optional<CellLocation> ref = CellLocation.maybeReference(oldDep);
            if (ref.isPresent()) {
                usages.get(ref.get()).remove(location);
            }
        }

        // insert all new usages
        for (String dep : newExpression.dependencies()) {
            Optional<CellLocation> ref = CellLocation.maybeReference(dep);
            if (ref.isPresent()) {
                usages.get(ref.get()).add(location);
            }
        }
    }

    /**
     * Create an appropriate string representation of the current spreadsheet.
     * A spreadsheet is represented as cells separated by pipes (|) and
     * rows separated by new lines.
     * Each cell contains the formula at that cell location.
     * The first line is the number of rows and number of columns separated by a pipe.
     * <p>
     *
     * For instance, a simple spreadsheet that contained 4 as the formula (and value)
     * in cell A1, A1+A1 as the formula (but not the value) in A2, and
     * A2*4 as the formula (but not the value) in B1,
     * would be represented as
     * <pre>
     * {@code
     * 2|2
     * 4|A2*4
     * A1*a1|
     * }</pre>
     * Notice that
     * <ol>
     *     <li>The first line is the dimensions of the spreadsheet.</li>
     *     <li>The lines do not start or end with pipes.</li>
     *     <li>As B2 has no value, the string representation is the empty string.</li>
     *     <li>The values are not stored, only the formulas.</li>
     * </ol>
     *
     * @return The string representation of the current spreadsheet.
     */
    public String encode() {
        StringJoiner builder = new StringJoiner("\n");
        for (int row = 0; row < rows; row++) {
            StringJoiner rowEncoding = new StringJoiner("|");
            for (int column = 0; column < columns; column++) {
                rowEncoding.add(formulas.get(new CellLocation(row, column)).render());
            }
            builder.add(rowEncoding.toString());
        }
        return builder.toString();
    }

    public String toCSV() {
        StringBuilder csvBuilder = new StringBuilder();

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                ViewElement viewElement = valueAt(row, col);
                String content = viewElement.getContent();
                csvBuilder.append(content);

                if (col < getColumns() - 1) {
                    csvBuilder.append(",");
                }
            }
            csvBuilder.append(System.lineSeparator());
        }

        return csvBuilder.toString();
    }

    public void loadFromCSV(String csvData) {
        String[] rows = csvData.split("\n");
        int numRows = rows.length;
        int numCols = 0;

        // Determine the number of columns by finding the maximum number of columns in any row
        for (String row : rows) {
            String[] columns = row.split(",", -1);
            numCols = Math.max(numCols, columns.length);
        }

        // Update dimensions of the sheet if necessary
        updateDimensions(numRows, numCols);

        // Populate sheet with CSV data
        for (int row = 0; row < numRows; row++) {
            String[] columns = rows[row].split(",", -1); // Split without trimming
            for (int col = 0; col < numCols; col++) {
                String cellData = (col < columns.length) ? columns[col] : "";
                update(row, col, cellData);
            }
        }
    }


}



