package sheep.fun;

import sheep.core.SheetUpdate;
import sheep.core.UpdateResponse;

/**
 * @provided
 */
public class Fibonacci implements Fun {
    private final int rows;

    public Fibonacci(int rows) {
        this.rows = rows;
    }

    @Override
    public void draw(SheetUpdate sheet) throws FunException {
        sheet.update(0, 0, "0");
        sheet.update(1, 0, "1");
        sheet.update(2, 0, "1");

        for (int i = 3; i < rows; i++) {
            String minus2 = "A" + (i - 2);
            String minus1 = "A" + (i - 1);

            UpdateResponse response = sheet.update(i, 0, minus1 + " + " + minus2);
            if (!response.isSuccess()) {
                throw new FunException(response.getMessage());
            }
        }
    }
}
