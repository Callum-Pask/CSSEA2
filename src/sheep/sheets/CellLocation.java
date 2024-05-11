package sheep.sheets;


import java.util.Objects;
import java.util.Optional;

/**
 * A location of a cell within a grid.
 * This class represents a location via a row, column coordinate system.
 * <p>
 * Notably columns are represented as character, e.g. in cell A1,
 * the column is 0 and the row is 1.
 * @stage2
 */
public class CellLocation {
    private final int row;
    private final int column;

    /**
     * Construct a new cell location at the given row and column.
     * <pre>
     * {@code
     * CellLocation location = new CellLocation(4, 'D');
     * location.getColumn() // 3
     * location.getRow() // 4
     * }</pre>
     *
     * In this constructor column chars are converted to column indexes,
     * e.g. column 'A' would become 0 and 'B' would become 1 and so on.
     *
     * @requires row is greater than or equal to zero, column is between 'A' and 'Z' inclusive.
     * @param row A number representing the row number.
     * @param column A character representing the column.
     */
    public CellLocation(int row, char column) {
        this.row = row;
        this.column = (column - 'A');
    }

    /**
     * Construct a new cell location at the given row and column.
     * <pre>
     * {@code
     * CellLocation location = new CellLocation(4, 3);
     * location.getColumn() // 3
     * location.getRow() // 4
     * }</pre>
     *
     * @requires row and column are greater than or equal to zero.
     * @requires column is less than 26.
     * @param row A number representing the row number.
     * @param column A number representing the column (see description of method).
     */
    public CellLocation(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Attempt to parse a string as a reference to a cell location.
     * If the string is not a reference to a cell location, returns {@link Optional#empty()}.
     * The format of the reference is a single uppercase character followed by
     * an integer without spaces and without extraneous characters after the integer or before the character.
     *
     * <pre>
     * {@code
     * CellLocation.maybeReference("A2") // Optional.of(new CellLocation(2, 'A'))
     * CellLocation.maybeReference("2A") // Optional.empty()
     * CellLocation.maybeReference("A 2") // Optional.empty()
     * CellLocation.maybeReference(" A2 ") // Optional.empty()
     * }</pre>
     *
     * @param ref A string that may represent a cell location.
     * @requires ref != null
     * @return An optional containing a cell reference if the string is a reference,
     *         otherwise the empty optional.
     */
    public static Optional<CellLocation> maybeReference(String ref) {
        if (ref.length() < 2 || ref.charAt(0) < 'A' || ref.charAt(0) > 'Z') {
            return Optional.empty();
        }
        char column = ref.charAt(0);
        StringBuilder rowString = new StringBuilder();
        for (int i = 1; i < ref.length(); i++) {
            if (!Character.isDigit(ref.charAt(i))) {
                return Optional.empty();
            }
            rowString.append(ref.charAt(i));
        }
        int row = Integer.parseInt(rowString.toString());
        return Optional.of(new CellLocation(row, column));
    }

    /**
     * The row number of this cell location.
     * @return The row number of this cell location.
     */
    public int getRow() {
        return row;
    }

    /**
     * The column number of this cell location.
     * @return The column number of this cell location.
     */
    public int getColumn() {
        return column;
    }

    /**
     * If two instances of cell location are equal to each other.
     * Equality is defined by having the same row and column.
     * @param obj another instance to compare against.
     * @return true if the other object is a cell location
     *         with the same row number and column number as the current cell location.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CellLocation other)) {
            return false;
        }
        return other.getRow() == getRow() && other.getColumn() == getColumn();
    }

    /**
     * A hashcode method that respects the {@link CellLocation#equals(Object)} method.
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * A string representation of a cell location.
     * @return A string representation of this cell location, e.g. A2 or C23.
     */
    @Override
    public String toString() {
        return "" + Character.toString(getColumn() + 'A') + getRow();
    }
}

