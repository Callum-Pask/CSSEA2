package sheep.fun;

import sheep.core.SheetUpdate;

public class GameOfLife implements Fun {
    private static final int ROW = 6;
    private static final int COL = 3;

    @Override
    public void draw(SheetUpdate sheet) throws FunException {
        sheet.update(ROW, COL, "1");
        sheet.update(ROW, COL + 1, "1");
        sheet.update(ROW - 1, COL + 2, "1");
        sheet.update(ROW + 1, COL + 2, "1");
        sheet.update(ROW, COL + 3, "1");
        sheet.update(ROW, COL + 4, "1");
        sheet.update(ROW, COL + 5, "1");
        sheet.update(ROW, COL + 6, "1");
        sheet.update(ROW - 1, COL + 7, "1");
        sheet.update(ROW + 1, COL + 7, "1");
        sheet.update(ROW, COL + 8, "1");
        sheet.update(ROW, COL + 9, "1");
    }
}
