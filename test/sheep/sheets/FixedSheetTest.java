package sheep.sheets;

import org.junit.Test;
import static org.junit.Assert.*;
import sheep.core.UpdateResponse;
import sheep.core.ViewElement;

public class FixedSheetTest {
    public static final int testWeight = 20;

    /**
     * Attempt to update the sheet a couple of times.
     * Assert that each time the UpdateResponse is a failure
     * with the message "Sheet is view only.".
     */
    @Test
    @Deprecated
    public void updateAlwaysFails() {
        FixedSheet sheet = new FixedSheet();
        UpdateResponse response = sheet.update(0, 0, "");
        assertEquals("Sheet is view only.", response.getMessage());
        assertFalse(response.isSuccess());

        response = sheet.update(0, 0, "2");
        assertEquals("Sheet is view only.", response.getMessage());
        assertFalse(response.isSuccess());

        response = sheet.update(2, 1, "");
        assertEquals("Sheet is view only.", response.getMessage());
        assertFalse(response.isSuccess());
    }

    /**
     * Assert that the sheet has 6 columns.
     */
    @Test
    @Deprecated
    public void columns() {
        FixedSheet sheet = new FixedSheet();
        assertEquals(6, sheet.getColumns());
    }

    /**
     * Assert that the sheet has 6 rows.
     */
    @Test
    @Deprecated
    public void rows() {
        FixedSheet sheet = new FixedSheet();
        assertEquals(6, sheet.getRows());
    }

    /**
     * Check each cell that should be highlighted.
     * Assert that for valueAt it's content is W,
     * it's background colour is green,
     * and foreground colour is black.
     */
    @Test
    @Deprecated
    public void highlightedValues() {
        FixedSheet sheet = new FixedSheet();
        for (int row = 2; row < 4; row++) {
            for (int column = 2; column < 4; column++) {
                ViewElement element = sheet.valueAt(row, column);
                assertEquals("W", element.getContent());
                assertEquals("green", element.getBackground().toLowerCase());
                assertEquals("black", element.getForeground().toLowerCase());
            }
        }
    }

    /**
     * Check each cell that should be highlighted.
     * Assert that for formulaAt it's content is GREEN,
     * it's background colour is green,
     * and foreground colour is black.
     */
    @Test
    @Deprecated
    public void highlightedFormulae() {
        FixedSheet sheet = new FixedSheet();
        for (int row = 2; row < 4; row++) {
            for (int column = 2; column < 4; column++) {
                ViewElement element = sheet.formulaAt(row, column);
                assertEquals("GREEN", element.getContent());
                assertEquals("green", element.getBackground().toLowerCase());
                assertEquals("black", element.getForeground().toLowerCase());
            }
        }
    }

    /**
     * Check each cell that should not be highlighted.
     * Assert that for valueAt it's content is the empty string,
     * it's background colour is white,
     * and foreground colour is black.
     */
    @Test
    @Deprecated
    public void unhighlightedValues() {
        FixedSheet sheet = new FixedSheet();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                if ((row < 2 || row > 3) && (column < 2 || column > 4)) {
                    ViewElement element = sheet.valueAt(row, column);
                    assertEquals("", element.getContent());
                    assertEquals("white", element.getBackground().toLowerCase());
                    assertEquals("black", element.getForeground().toLowerCase());
                }
            }
        }
    }

    /**
     * Check each cell that should not be highlighted.
     * Assert that for formulaAt it's content is the empty string,
     * it's background colour is white,
     * and foreground colour is black.
     */
    @Test
    @Deprecated
    public void unhighlightedFormulae() {
        FixedSheet sheet = new FixedSheet();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 6; column++) {
                if ((row < 2 || row > 3) && (column < 2 || column > 4)) {
                    ViewElement element = sheet.formulaAt(row, column);
                    assertEquals("", element.getContent());
                    assertEquals("white", element.getBackground().toLowerCase());
                    assertEquals("black", element.getForeground().toLowerCase());
                }
            }
        }
    }
}
