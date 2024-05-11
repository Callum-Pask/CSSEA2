package sheep.fun;

import sheep.core.SheetUpdate;

public class BreakoutFun implements Fun {
    private int columns = 0;

    public BreakoutFun(int columns) {
        this.columns = columns;
    }

    @Override
    public void draw(SheetUpdate sheet) throws FunException {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < columns; x++) {
                if (!sheet.update(y, x, "1").isSuccess()) {
                    break;
                }
            }
        }
    }
}
