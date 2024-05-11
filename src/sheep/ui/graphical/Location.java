package sheep.ui.graphical;

/**
 * Stored within the {@link SheetModel} as an indirect reference
 * to a value that can later be queried.
 * This enables the table model to 'store' multiple values in the
 * same cell.
 * @param row The row to query.
 * @param column The column to query.
 */
record Location(int row, int column) {

}
