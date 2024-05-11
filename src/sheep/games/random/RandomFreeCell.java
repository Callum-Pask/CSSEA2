package sheep.games.random;

import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;

import java.util.Random;

public class RandomFreeCell implements RandomCell {
    private Sheet sheet;
    private Random random;

    public RandomFreeCell(Sheet sheet, Random random) {
        this.sheet = sheet;
        this.random = random;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                if (sheet.valueAt(row, column).getContent().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public CellLocation pick() {
        CellLocation location;
        do {
            location = new CellLocation(
                    random.nextInt(sheet.getRows()),
                    random.nextInt(sheet.getColumns()));
        } while (!sheet.valueAt(location.getRow(), location.getColumn()).getContent().equals(""));
        return location;
    }

    public CellLocation pickExcludeTopLeft() {
        CellLocation location;
        int rows = sheet.getRows();
        int columns = sheet.getColumns();
        int row, column;
        do {
            row = random.nextInt(rows);
            column = random.nextInt(columns);
            location = new CellLocation(row, column);
        } while ((row == 0 && column == 0) || !sheet.valueAt(row, column).getContent().equals(""));
        return location;
    }
}
