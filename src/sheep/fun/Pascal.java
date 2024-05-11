package sheep.fun;

import sheep.core.SheetUpdate;
import sheep.core.UpdateResponse;

public class Pascal implements Fun {
    private final int startRow;
    private final int startColumn;

    public Pascal(int startRow, int startColumn) {
        this.startRow = startRow;
        this.startColumn = startColumn;
    }

    @Override
    public void draw(SheetUpdate sheet) throws FunException {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 6; x++) {
                int row = y + startRow;
                int column = x + startColumn;
                UpdateResponse response;
                if (x < 1) {
                    response = sheet.update(row, column, "1");
                } else if (x > y) {
                    response = sheet.update(row, column, "1");
                } else {
                    String leftColumn = Character.toString(column - 1 + 65);
                    String rightColumn = Character.toString(column + 65);
                    response = sheet.update(row, column,
                            "" + leftColumn + (row - 1) + " + "
                                    + rightColumn + (row - 1));
                }
                if (!response.isSuccess()) {
                    throw new FunException(response.getMessage());
                }
            }
        }
    }
}
