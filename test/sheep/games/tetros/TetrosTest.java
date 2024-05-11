package sheep.games.tetros;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.CoreFactory;
import sheep.expression.ExpressionFactory;
import sheep.features.Feature;
import sheep.games.MockUI;
import sheep.games.random.RandomTile;
import sheep.parsing.SimpleParser;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.sheets.SheetBuilder;

import java.util.Map;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TetrosTest {
    private Sheet sheet;
    private MockUI ui;
    private PickPiece picker;

    private static class PickPiece implements RandomTile {
        private int fixedPiece = 3;

        public void setFixedPiece(int value) {
            this.fixedPiece = value;
        }

        @Override
        public int pick() {
            return fixedPiece;
        }
    }

    @Before
    public void setup() {
        setupSheet(10, 5);
    }

    private void setupSheet(int rows, int columns) {
        ExpressionFactory factory = new CoreFactory();
        this.sheet = new SheetBuilder(new SimpleParser(factory), factory.createEmpty())
                .empty(rows, columns);
        ui = new MockUI(sheet, sheet);

        picker = new PickPiece();
        Feature game = new Tetros(sheet, picker);
        game.register(ui);
    }

    private String expectedSheet(Map<CellLocation, String> exceptions) {
        StringJoiner builder = new StringJoiner(System.lineSeparator());
        for (int row = 0; row < sheet.getRows(); row++) {
            StringJoiner rowString = new StringJoiner("|");
            for (int column = 0; column < sheet.getColumns(); column++) {
                String expected = exceptions.getOrDefault(new CellLocation(row, column), "");
                rowString.add(expected);
            }
            builder.add(rowString.toString());
        }
        return builder.toString();
    }

    private void assertEmptyExcept(Map<CellLocation, String> exceptions) {
        for (CellLocation location : exceptions.keySet()) {
            assertTrue(sheet.contains(location));
        }
        String expectedSheet = expectedSheet(exceptions);
        String actualSheet = sheet.encode();
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                CellLocation location = new CellLocation(row, column);
                String expected = exceptions.getOrDefault(location, "");
                String actual = sheet.valueAt(row, column).getContent();
                assertEquals(
                        "Difference value at (" + row + ", " + column + "): Expected "
                                + expected + " Got " + actual
                                + System.lineSeparator()
                                + "Expected" + System.lineSeparator()
                                + expectedSheet + System.lineSeparator()
                                + "Got" + System.lineSeparator()
                                + actualSheet,
                        expected, actual
                );
            }
        }
    }

    /**
     * Test the initial state of the game.
     */
    @Test
    public void startGame() {
        assertEmptyExcept(Map.of());
        ui.simulateFeature("tetros", 1, 1);
        assertEmptyExcept(Map.of(
                new CellLocation(1, 1), "8",
                new CellLocation(0, 0), "8",
                new CellLocation(0, 1), "8",
                new CellLocation(0, 2), "8"
        ));
    }

    /**
     * Test that a tile can be moved right.
     */
    @Test
    public void moveTile() {
        assertEmptyExcept(Map.of());
        ui.simulateFeature("tetros", 1, 1);
        assertEmptyExcept(Map.of(
                new CellLocation(1, 1), "8",
                new CellLocation(0, 0), "8",
                new CellLocation(0, 1), "8",
                new CellLocation(0, 2), "8"
        ));
        ui.simulatePress("d", 1, 1);
        assertEmptyExcept(Map.of(
                new CellLocation(1, 2), "8",
                new CellLocation(0, 1), "8",
                new CellLocation(0, 2), "8",
                new CellLocation(0, 3), "8"
        ));
    }

    /**
     * Test that a tile is dropped by after a tick.
     */
    @Test
    public void dropTile() {
        assertEmptyExcept(Map.of());
        ui.simulateFeature("tetros", 1, 1);
        assertEmptyExcept(Map.of(
                new CellLocation(1, 1), "8",
                new CellLocation(0, 0), "8",
                new CellLocation(0, 1), "8",
                new CellLocation(0, 2), "8"
        ));
        ui.simulateTick();
        assertEmptyExcept(Map.of(
                new CellLocation(2, 1), "8",
                new CellLocation(1, 0), "8",
                new CellLocation(1, 1), "8",
                new CellLocation(1, 2), "8"
        ));
    }

    /**
     * Test that quick dropping (s) a tile places it on the bottom of the board.
     */
    @Test
    public void quickDropTile() {
        assertEmptyExcept(Map.of());
        ui.simulateFeature("tetros", 1, 1);
        assertEmptyExcept(Map.of(
                new CellLocation(1, 1), "8",
                new CellLocation(0, 0), "8",
                new CellLocation(0, 1), "8",
                new CellLocation(0, 2), "8"
        ));
        ui.simulatePress("s", 1, 1);
        assertEmptyExcept(Map.of(
                new CellLocation(9, 1), "8",
                new CellLocation(8, 0), "8",
                new CellLocation(8, 1), "8",
                new CellLocation(8, 2), "8"
        ));
    }


    /**
     * Test moving and rotating a tile works as expected.
     */
    @Test
    public void smallGameplay() {
        setupSheet(20, 10);
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(0);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(2);
        ui.simulateTick();

        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1),"5"),
                Map.entry(new CellLocation(18, 1),"6"),
                Map.entry(new CellLocation(1, 1),"5"),
                Map.entry(new CellLocation(17, 1),"6"),
                Map.entry(new CellLocation(18, 2),"4"),
                Map.entry(new CellLocation(19, 3),"4"),
                Map.entry(new CellLocation(0, 1),"5"),
                Map.entry(new CellLocation(16, 1),"6"),
                Map.entry(new CellLocation(17, 2),"4"),
                Map.entry(new CellLocation(18, 3),"4"),
                Map.entry(new CellLocation(2, 0),"5"),
                Map.entry(new CellLocation(19, 1),"6")
        ));
    }

    /**
     * Test that rotating an L piece right works as expected.
     */
    @Test
    public void rotateL() {
        picker.setFixedPiece(1);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(4, 0), "7"),
                Map.entry(new CellLocation(3, 0), "7"),
                Map.entry(new CellLocation(4, 1), "7"),
                Map.entry(new CellLocation(4, 2), "7")
        ));
    }

    /**
     * Test that multiple right rotations of an L piece works as expected.
     */
    @Test
    public void rotateLTwice() {
        picker.setFixedPiece(2);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "5"),
                Map.entry(new CellLocation(2, 2), "5"),
                Map.entry(new CellLocation(3, 0), "5"),
                Map.entry(new CellLocation(2, 0), "5")
        ));
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "5"),
                Map.entry(new CellLocation(4, 0), "5"),
                Map.entry(new CellLocation(4, 1), "5"),
                Map.entry(new CellLocation(3, 1), "5")
        ));
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(6, 0), "5"),
                Map.entry(new CellLocation(6, 1), "5"),
                Map.entry(new CellLocation(5, 1), "5"),
                Map.entry(new CellLocation(4, 1), "5")
        ));
    }

    /**
     * Test that rotating an L piece left works as expected.
     */
    @Test
    public void rotateLLeft() {
        picker.setFixedPiece(2);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("q", -2, -2);
        ui.simulateTick();
        ui.simulatePress("q", -2, -2);
        ui.simulatePress("q", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(6, 1), "5"),
                Map.entry(new CellLocation(6, 2), "5"),
                Map.entry(new CellLocation(6, 3), "5"),
                Map.entry(new CellLocation(5, 3), "5")
        ));
        ui.simulateTick();
        ui.simulatePress("q", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(7, 2), "5"),
                Map.entry(new CellLocation(7, 3), "5"),
                Map.entry(new CellLocation(6, 3), "5"),
                Map.entry(new CellLocation(5, 3), "5")
        ));
    }

    /**
     * Test that rotating an S piece both left and right works as expected.
     */
    @Test
    public void rotateSBothWays() {
        picker.setFixedPiece(0);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "4"),
                Map.entry(new CellLocation(3, 2), "4"),
                Map.entry(new CellLocation(1, 1), "4"),
                Map.entry(new CellLocation(2, 2), "4")
        ));
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(3, 0), "4"),
                Map.entry(new CellLocation(4, 1), "4"),
                Map.entry(new CellLocation(3, 1), "4"),
                Map.entry(new CellLocation(4, 2), "4")
        ));
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(5, 1), "4"),
                Map.entry(new CellLocation(6, 2), "4"),
                Map.entry(new CellLocation(4, 1), "4"),
                Map.entry(new CellLocation(5, 2), "4")
        ));
        ui.simulatePress("q", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(6, 0), "4"),
                Map.entry(new CellLocation(7, 1), "4"),
                Map.entry(new CellLocation(6, 1), "4"),
                Map.entry(new CellLocation(7, 2), "4")
        ));
    }

    /**
     * Test that multiple tiles will stack.
     */
    @Test
    public void buildStack() {
        setupSheet(20, 10);
        picker.setFixedPiece(3);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(0);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(18, 0), "8"),
                Map.entry(new CellLocation(19, 1), "8"),
                Map.entry(new CellLocation(17, 0), "6"),
                Map.entry(new CellLocation(18, 1), "8"),
                Map.entry(new CellLocation(16, 0), "6"),
                Map.entry(new CellLocation(17, 1), "7"),
                Map.entry(new CellLocation(18, 2), "8"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(16, 1), "7"),
                Map.entry(new CellLocation(17, 2), "7"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(15, 1), "7"),
                Map.entry(new CellLocation(13, 0), "4"),
                Map.entry(new CellLocation(14, 1), "4"),
                Map.entry(new CellLocation(13, 1), "4"),
                Map.entry(new CellLocation(14, 2), "4")
        ));
        picker.setFixedPiece(2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "5"),
                Map.entry(new CellLocation(1, 1), "5"),
                Map.entry(new CellLocation(0, 1), "5"),
                Map.entry(new CellLocation(18, 0), "8"),
                Map.entry(new CellLocation(19, 1), "8"),
                Map.entry(new CellLocation(17, 0), "6"),
                Map.entry(new CellLocation(18, 1), "8"),
                Map.entry(new CellLocation(16, 0), "6"),
                Map.entry(new CellLocation(17, 1), "7"),
                Map.entry(new CellLocation(18, 2), "8"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(16, 1), "7"),
                Map.entry(new CellLocation(17, 2), "7"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(15, 1), "7"),
                Map.entry(new CellLocation(13, 0), "4"),
                Map.entry(new CellLocation(14, 1), "4"),
                Map.entry(new CellLocation(12, 0), "5"),
                Map.entry(new CellLocation(13, 1), "4"),
                Map.entry(new CellLocation(14, 2), "4"),
                Map.entry(new CellLocation(11, 0), "5"),
                Map.entry(new CellLocation(11, 1), "5"),
                Map.entry(new CellLocation(11, 2), "5"),
                Map.entry(new CellLocation(2, 0), "5")
        ));
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(2);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(19, 1), "8"),
                Map.entry(new CellLocation(17, 0), "6"),
                Map.entry(new CellLocation(17, 1), "7"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(17, 2), "7"),
                Map.entry(new CellLocation(15, 1), "7"),
                Map.entry(new CellLocation(13, 0), "4"),
                Map.entry(new CellLocation(13, 1), "4"),
                Map.entry(new CellLocation(11, 0), "5"),
                Map.entry(new CellLocation(11, 1), "5"),
                Map.entry(new CellLocation(11, 2), "5"),
                Map.entry(new CellLocation(9, 1), "5"),
                Map.entry(new CellLocation(7, 0), "5"),
                Map.entry(new CellLocation(7, 1), "5"),
                Map.entry(new CellLocation(5, 1), "5"),
                Map.entry(new CellLocation(18, 0), "8"),
                Map.entry(new CellLocation(18, 1), "8"),
                Map.entry(new CellLocation(16, 0), "6"),
                Map.entry(new CellLocation(18, 2), "8"),
                Map.entry(new CellLocation(16, 1), "7"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(14, 1), "4"),
                Map.entry(new CellLocation(12, 0), "5"),
                Map.entry(new CellLocation(14, 2), "4"),
                Map.entry(new CellLocation(10, 0), "5"),
                Map.entry(new CellLocation(10, 1), "5"),
                Map.entry(new CellLocation(8, 1), "5"),
                Map.entry(new CellLocation(6, 1), "5")
        ));
        picker.setFixedPiece(4);
    }

    /**
     * Test that a straight piece can be rotated right and left as expected.
     */
    @Test
    public void rotateLinePiece() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(4, 3), "6"),
                Map.entry(new CellLocation(4, 0), "6"),
                Map.entry(new CellLocation(4, 1), "6"),
                Map.entry(new CellLocation(4, 2), "6")
        ));
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(7, 2), "6"),
                Map.entry(new CellLocation(6, 2), "6"),
                Map.entry(new CellLocation(5, 2), "6"),
                Map.entry(new CellLocation(4, 2), "6")
        ));
        ui.simulateTick();
        ui.simulatePress("q", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(7, 1), "6"),
                Map.entry(new CellLocation(7, 2), "6"),
                Map.entry(new CellLocation(7, 3), "6"),
                Map.entry(new CellLocation(7, 4), "6")
        ));
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(9, 2), "6"),
                Map.entry(new CellLocation(8, 2), "6"),
                Map.entry(new CellLocation(7, 2), "6"),
                Map.entry(new CellLocation(6, 2), "6")
        ));
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(8, 0), "6"),
                Map.entry(new CellLocation(8, 1), "6"),
                Map.entry(new CellLocation(8, 2), "6"),
                Map.entry(new CellLocation(8, 3), "6")
        ));
    }

    /**
     * Test that building a stack such that a new piece cannot be spawned causes a game over.
     */
    @Test
    public void testGameOverStack() {
        setupSheet(20, 10);
        picker.setFixedPiece(1);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(3);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(3);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(0);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(3);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(0);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(0, 0), "4"),
                Map.entry(new CellLocation(0, 1), "4"),
                Map.entry(new CellLocation(19, 0), "7"),
                Map.entry(new CellLocation(19, 1), "7"),
                Map.entry(new CellLocation(17, 0), "7"),
                Map.entry(new CellLocation(17, 1), "8"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(13, 0), "6"),
                Map.entry(new CellLocation(11, 0), "8"),
                Map.entry(new CellLocation(11, 1), "8"),
                Map.entry(new CellLocation(9, 0), "4"),
                Map.entry(new CellLocation(11, 2), "8"),
                Map.entry(new CellLocation(9, 1), "4"),
                Map.entry(new CellLocation(7, 0), "8"),
                Map.entry(new CellLocation(7, 1), "8"),
                Map.entry(new CellLocation(7, 2), "8"),
                Map.entry(new CellLocation(1, 1), "4"),
                Map.entry(new CellLocation(1, 2), "4"),
                Map.entry(new CellLocation(18, 0), "7"),
                Map.entry(new CellLocation(16, 0), "8"),
                Map.entry(new CellLocation(16, 1), "8"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(16, 2), "8"),
                Map.entry(new CellLocation(12, 0), "6"),
                Map.entry(new CellLocation(12, 1), "8"),
                Map.entry(new CellLocation(10, 1), "4"),
                Map.entry(new CellLocation(10, 2), "4"),
                Map.entry(new CellLocation(8, 1), "8")
        ));
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(0);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(3);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(0, 0), "8"),
                Map.entry(new CellLocation(0, 1), "8"),
                Map.entry(new CellLocation(0, 2), "8"),
                Map.entry(new CellLocation(19, 0), "7"),
                Map.entry(new CellLocation(19, 1), "7"),
                Map.entry(new CellLocation(17, 0), "7"),
                Map.entry(new CellLocation(17, 1), "8"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(13, 0), "6"),
                Map.entry(new CellLocation(11, 0), "8"),
                Map.entry(new CellLocation(11, 1), "8"),
                Map.entry(new CellLocation(9, 0), "4"),
                Map.entry(new CellLocation(11, 2), "8"),
                Map.entry(new CellLocation(9, 1), "4"),
                Map.entry(new CellLocation(7, 0), "8"),
                Map.entry(new CellLocation(7, 1), "8"),
                Map.entry(new CellLocation(5, 0), "4"),
                Map.entry(new CellLocation(7, 2), "8"),
                Map.entry(new CellLocation(5, 1), "4"),
                Map.entry(new CellLocation(3, 0), "4"),
                Map.entry(new CellLocation(3, 1), "4"),
                Map.entry(new CellLocation(1, 1), "8"),
                Map.entry(new CellLocation(18, 0), "7"),
                Map.entry(new CellLocation(16, 0), "8"),
                Map.entry(new CellLocation(16, 1), "8"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(16, 2), "8"),
                Map.entry(new CellLocation(12, 0), "6"),
                Map.entry(new CellLocation(12, 1), "8"),
                Map.entry(new CellLocation(10, 1), "4"),
                Map.entry(new CellLocation(10, 2), "4"),
                Map.entry(new CellLocation(8, 1), "8"),
                Map.entry(new CellLocation(6, 1), "4"),
                Map.entry(new CellLocation(6, 2), "4"),
                Map.entry(new CellLocation(4, 1), "4"),
                Map.entry(new CellLocation(4, 2), "4")
        ));
        ui.simulateTick();
        picker.setFixedPiece(4);
        ui.expectMessage("Game Over!");
        ui.simulateTick();
        assertEquals("Game Over!", ui.getActualMessages().get(0));
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "8"),
                Map.entry(new CellLocation(19, 0), "7"),
                Map.entry(new CellLocation(19, 1), "7"),
                Map.entry(new CellLocation(17, 0), "7"),
                Map.entry(new CellLocation(17, 1), "8"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(13, 0), "6"),
                Map.entry(new CellLocation(11, 0), "8"),
                Map.entry(new CellLocation(11, 1), "8"),
                Map.entry(new CellLocation(9, 0), "4"),
                Map.entry(new CellLocation(11, 2), "8"),
                Map.entry(new CellLocation(9, 1), "4"),
                Map.entry(new CellLocation(7, 0), "8"),
                Map.entry(new CellLocation(7, 1), "8"),
                Map.entry(new CellLocation(5, 0), "4"),
                Map.entry(new CellLocation(7, 2), "8"),
                Map.entry(new CellLocation(5, 1), "4"),
                Map.entry(new CellLocation(3, 0), "4"),
                Map.entry(new CellLocation(3, 1), "4"),
                Map.entry(new CellLocation(1, 0), "8"),
                Map.entry(new CellLocation(1, 1), "8"),
                Map.entry(new CellLocation(1, 2), "8"),
                Map.entry(new CellLocation(18, 0), "7"),
                Map.entry(new CellLocation(16, 0), "8"),
                Map.entry(new CellLocation(16, 1), "8"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(16, 2), "8"),
                Map.entry(new CellLocation(12, 0), "6"),
                Map.entry(new CellLocation(12, 1), "8"),
                Map.entry(new CellLocation(10, 1), "4"),
                Map.entry(new CellLocation(10, 2), "4"),
                Map.entry(new CellLocation(8, 1), "8"),
                Map.entry(new CellLocation(6, 1), "4"),
                Map.entry(new CellLocation(6, 2), "4"),
                Map.entry(new CellLocation(4, 1), "4"),
                Map.entry(new CellLocation(4, 2), "4")
        ));
    }

    /**
     * Test that trying to move right outside of the spreadsheet is not possible.
     */
    @Test
    public void cannotMoveOutsideRightBounds() {
        picker.setFixedPiece(1);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(4, 3), "7"),
                Map.entry(new CellLocation(5, 4), "7"),
                Map.entry(new CellLocation(3, 3), "7"),
                Map.entry(new CellLocation(5, 3), "7")
        ));
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(4, 3), "7"),
                Map.entry(new CellLocation(6, 3), "7"),
                Map.entry(new CellLocation(5, 3), "7"),
                Map.entry(new CellLocation(6, 4), "7")
        ));
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(8, 3), "7"),
                Map.entry(new CellLocation(7, 3), "7"),
                Map.entry(new CellLocation(8, 4), "7"),
                Map.entry(new CellLocation(6, 3), "7")
        ));
        ui.simulatePress("d", -2, -2);
    }

    /**
     * Test that trying to move left outside of the spreadsheet is not possible.
     */
    @Test
    public void cannotMoveOutsideLeftBounds() {
        picker.setFixedPiece(4);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("a",  -2, -2);
        ui.simulatePress("a",  -2, -2);
        ui.simulateTick();
        ui.simulatePress("a",  -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(4, 0), "3"),
                Map.entry(new CellLocation(3, 0), "3"),
                Map.entry(new CellLocation(4, 1), "3"),
                Map.entry(new CellLocation(3, 1), "3")
        ));
        ui.simulatePress("a",  -2, -2);
        ui.simulateTick();
        ui.simulatePress("a",  -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(5, 0), "3"),
                Map.entry(new CellLocation(4, 0), "3"),
                Map.entry(new CellLocation(5, 1), "3"),
                Map.entry(new CellLocation(4, 1), "3")
        ));
    }

    /**
     * Test that dropping incrementally by ticks will stop when the tile reaches the floor.
     */
    @Test
    public void naturalDropsFitFloor() {
        picker.setFixedPiece(2);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(8, 0), "5"),
                Map.entry(new CellLocation(8, 1), "5"),
                Map.entry(new CellLocation(7, 1), "5"),
                Map.entry(new CellLocation(6, 1), "5")
        ));
        ui.simulateTick();
        picker.setFixedPiece(2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "5"),
                Map.entry(new CellLocation(1, 1), "5"),
                Map.entry(new CellLocation(0, 1), "5"),
                Map.entry(new CellLocation(9, 0), "5"),
                Map.entry(new CellLocation(9, 1), "5"),
                Map.entry(new CellLocation(8, 1), "5"),
                Map.entry(new CellLocation(7, 1), "5"),
                Map.entry(new CellLocation(2, 0), "5")
        ));
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "5"),
                Map.entry(new CellLocation(9, 0), "5"),
                Map.entry(new CellLocation(9, 1), "5"),
                Map.entry(new CellLocation(8, 1), "5"),
                Map.entry(new CellLocation(7, 1), "5"),
                Map.entry(new CellLocation(4, 0), "5"),
                Map.entry(new CellLocation(4, 1), "5"),
                Map.entry(new CellLocation(3, 1), "5")
        ));
    }

    /**
     * Test that a row is cleared when it is full.
     */
    @Test
    public void clearRow() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(9, 0), "6"),
                Map.entry(new CellLocation(9, 1), "6"),
                Map.entry(new CellLocation(9, 2), "6"),
                Map.entry(new CellLocation(9, 3), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(2, 0), "6")
        ));
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(5, 4), "6"),
                Map.entry(new CellLocation(4, 4), "6"),
                Map.entry(new CellLocation(3, 4), "6"),
                Map.entry(new CellLocation(2, 4), "6"),
                Map.entry(new CellLocation(9, 0), "6"),
                Map.entry(new CellLocation(9, 1), "6"),
                Map.entry(new CellLocation(9, 2), "6"),
                Map.entry(new CellLocation(9, 3), "6")
        ));
    }

    /**
     * Test that when a tile moves into another tile from the left or right,
     * it will 'eat' that tile.
     */
    @Test
    public void eatOtherTile() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("a",  -2, -2);
        ui.simulatePress("a",  -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(9, 0), "6"),
                Map.entry(new CellLocation(8, 0), "6"),
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(6, 0), "6"),
                Map.entry(new CellLocation(5, 0), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(2, 0), "6")
        ));
    }

    /**
     * Test that when a tile moves into another tile from the left or right,
     * it will 'eat' that tile.
     * This ensures they can move into the left or right tiles multiple times
     * prior to the next tick.
     */
    @Test
    public void eatManyTiles() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(3, 2), "6"),
                Map.entry(new CellLocation(2, 2), "6"),
                Map.entry(new CellLocation(9, 0), "6"),
                Map.entry(new CellLocation(8, 0), "6"),
                Map.entry(new CellLocation(9, 1), "6"),
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(8, 1), "6"),
                Map.entry(new CellLocation(6, 0), "6"),
                Map.entry(new CellLocation(7, 1), "6"),
                Map.entry(new CellLocation(6, 1), "6"),
                Map.entry(new CellLocation(5, 2), "6"),
                Map.entry(new CellLocation(4, 2), "6")
        ));
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("a",  -2, -2);
        ui.simulatePress("a",  -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(9, 0), "6"),
                Map.entry(new CellLocation(8, 0), "6"),
                Map.entry(new CellLocation(9, 1), "6"),
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(6, 0), "6"),
                Map.entry(new CellLocation(5, 0), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(2, 0), "6")
        ));
    }

    /**
     * Test that when a tile moves into another tile from the left or right,
     * it will 'eat' that tile, even if it is a different type of tile.
     */
    @Test
    public void eatTilesDifferentTypes() {
        picker.setFixedPiece(3);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(8, 3), "8"),
                Map.entry(new CellLocation(9, 4), "8"),
                Map.entry(new CellLocation(8, 4), "8"),
                Map.entry(new CellLocation(7, 4), "8")
        ));
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        picker.setFixedPiece(1);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "7"),
                Map.entry(new CellLocation(2, 1), "7"),
                Map.entry(new CellLocation(5, 4), "6"),
                Map.entry(new CellLocation(0, 0), "7"),
                Map.entry(new CellLocation(9, 4), "8"),
                Map.entry(new CellLocation(8, 4), "6"),
                Map.entry(new CellLocation(7, 4), "6"),
                Map.entry(new CellLocation(2, 0), "7"),
                Map.entry(new CellLocation(6, 4), "6")
        ));
    }

    /**
     * Test that rotating a cube piece works as expected.
     */
    @Test
    public void rotateCube() {
        picker.setFixedPiece(4);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "3"),
                Map.entry(new CellLocation(2, 1), "3"),
                Map.entry(new CellLocation(1, 1), "3"),
                Map.entry(new CellLocation(2, 0), "3")
        ));
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(2, 1), "3"),
                Map.entry(new CellLocation(3, 2), "3"),
                Map.entry(new CellLocation(2, 2), "3"),
                Map.entry(new CellLocation(3, 1), "3")
        ));
        ui.simulateTick();
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(5, 0), "3"),
                Map.entry(new CellLocation(4, 0), "3"),
                Map.entry(new CellLocation(5, 1), "3"),
                Map.entry(new CellLocation(4, 1), "3")
        ));
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("q", -2, -2);
        ui.simulateTick();
        ui.simulatePress("q", -2, -2);
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(7, 1), "3"),
                Map.entry(new CellLocation(6, 1), "3"),
                Map.entry(new CellLocation(7, 2), "3"),
                Map.entry(new CellLocation(6, 2), "3")
        ));
    }

    /**
     * Test that the tile can be moved after a quick drop prior to the next tick.
     */
    @Test
    public void quickDropSlide() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        ui.simulatePress("a",  -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(9, 0), "7"),
                Map.entry(new CellLocation(8, 0), "7"),
                Map.entry(new CellLocation(9, 1), "7"),
                Map.entry(new CellLocation(7, 0), "7"),
                Map.entry(new CellLocation(6, 0), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(2, 0), "6")
        ));
    }

    /**
     * Test that a stack of tiles can be constructed.
     */
    @Test
    public void oddStack() {
        picker.setFixedPiece(3);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(3);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("a",  -2, -2);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(3, 2), "7"),
                Map.entry(new CellLocation(4, 3), "7"),
                Map.entry(new CellLocation(2, 2), "7"),
                Map.entry(new CellLocation(8, 0), "8"),
                Map.entry(new CellLocation(9, 1), "8"),
                Map.entry(new CellLocation(8, 1), "8"),
                Map.entry(new CellLocation(6, 0), "8"),
                Map.entry(new CellLocation(7, 1), "8"),
                Map.entry(new CellLocation(8, 2), "8"),
                Map.entry(new CellLocation(5, 0), "5"),
                Map.entry(new CellLocation(6, 1), "8"),
                Map.entry(new CellLocation(5, 1), "5"),
                Map.entry(new CellLocation(6, 2), "8"),
                Map.entry(new CellLocation(4, 1), "5"),
                Map.entry(new CellLocation(3, 1), "5"),
                Map.entry(new CellLocation(4, 2), "7")
        ));
        ui.simulateTick();
        picker.setFixedPiece(3);
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "8"),
                Map.entry(new CellLocation(2, 1), "8"),
                Map.entry(new CellLocation(3, 2), "7"),
                Map.entry(new CellLocation(1, 1), "8"),
                Map.entry(new CellLocation(1, 2), "8"),
                Map.entry(new CellLocation(8, 0), "8"),
                Map.entry(new CellLocation(9, 1), "8"),
                Map.entry(new CellLocation(8, 1), "8"),
                Map.entry(new CellLocation(6, 0), "8"),
                Map.entry(new CellLocation(7, 1), "8"),
                Map.entry(new CellLocation(8, 2), "8"),
                Map.entry(new CellLocation(5, 0), "5"),
                Map.entry(new CellLocation(6, 1), "8"),
                Map.entry(new CellLocation(5, 1), "5"),
                Map.entry(new CellLocation(6, 2), "8"),
                Map.entry(new CellLocation(4, 1), "5"),
                Map.entry(new CellLocation(5, 2), "7"),
                Map.entry(new CellLocation(3, 1), "5"),
                Map.entry(new CellLocation(4, 2), "7"),
                Map.entry(new CellLocation(5, 3), "7")
        ));
    }

    /**
     * Test that while a tile is falling, if it fills a row,
     * then the row will be cleared.
     */
    @Test
    public void clearRowsWhileDropping() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(9, 3), "6"),
                Map.entry(new CellLocation(8, 3), "6"),
                Map.entry(new CellLocation(9, 4), "6"),
                Map.entry(new CellLocation(7, 3), "6"),
                Map.entry(new CellLocation(8, 4), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(6, 3), "6"),
                Map.entry(new CellLocation(7, 4), "6"),
                Map.entry(new CellLocation(2, 0), "6"),
                Map.entry(new CellLocation(6, 4), "6")
        ));
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulateTick();
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(9, 1), "6"),
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(8, 1), "6"),
                Map.entry(new CellLocation(9, 2), "6"),
                Map.entry(new CellLocation(6, 0), "6"),
                Map.entry(new CellLocation(7, 1), "6"),
                Map.entry(new CellLocation(8, 2), "6"),
                Map.entry(new CellLocation(9, 3), "6"),
                Map.entry(new CellLocation(5, 0), "6"),
                Map.entry(new CellLocation(6, 1), "6"),
                Map.entry(new CellLocation(8, 3), "6"),
                Map.entry(new CellLocation(9, 4), "6"),
                Map.entry(new CellLocation(5, 1), "6"),
                Map.entry(new CellLocation(8, 4), "6"),
                Map.entry(new CellLocation(4, 1), "6")
        ));
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(9, 0), "6"),
                Map.entry(new CellLocation(8, 0), "6"),
                Map.entry(new CellLocation(9, 1), "6"),
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(8, 1), "6"),
                Map.entry(new CellLocation(7, 1), "6"),
                Map.entry(new CellLocation(6, 1), "6")
        ));
    }

    /**
     * Test that multiple rows can be cleared at once.
     */
    @Test
    public void clearManyRows() {
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(2, 0), "6")
        ));
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(6, 0), "6"),
                Map.entry(new CellLocation(5, 0), "6"),
                Map.entry(new CellLocation(4, 0), "6")
        ));
        ui.simulateTick();
    }

    /**
     * Test that the clearing while falling functionality
     * enables the player to clear more rows than they would
     * normally be able to clear.
     */
    @Test
    public void clearTooMany() {
        setupSheet(20, 5);
        picker.setFixedPiece(5);
        ui.simulateFeature("tetros", 1, 1);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("a",  -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulateTick();
        ui.simulatePress("e", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("e", -2, -2);
        ui.simulateTick();
        ui.simulatePress("d", -2, -2);
        ui.simulatePress("s", -2, -2);
        picker.setFixedPiece(5);
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(19, 1), "6"),
                Map.entry(new CellLocation(19, 2), "6"),
                Map.entry(new CellLocation(17, 1), "6"),
                Map.entry(new CellLocation(19, 3), "6"),
                Map.entry(new CellLocation(17, 2), "6"),
                Map.entry(new CellLocation(19, 4), "6"),
                Map.entry(new CellLocation(15, 1), "6"),
                Map.entry(new CellLocation(17, 3), "6"),
                Map.entry(new CellLocation(15, 2), "6"),
                Map.entry(new CellLocation(17, 4), "6"),
                Map.entry(new CellLocation(13, 1), "6"),
                Map.entry(new CellLocation(15, 3), "6"),
                Map.entry(new CellLocation(13, 2), "6"),
                Map.entry(new CellLocation(15, 4), "6"),
                Map.entry(new CellLocation(11, 1), "6"),
                Map.entry(new CellLocation(13, 3), "6"),
                Map.entry(new CellLocation(11, 2), "6"),
                Map.entry(new CellLocation(13, 4), "6"),
                Map.entry(new CellLocation(11, 3), "6"),
                Map.entry(new CellLocation(7, 0), "6"),
                Map.entry(new CellLocation(11, 4), "6"),
                Map.entry(new CellLocation(5, 0), "6"),
                Map.entry(new CellLocation(18, 1), "6"),
                Map.entry(new CellLocation(18, 2), "6"),
                Map.entry(new CellLocation(16, 1), "6"),
                Map.entry(new CellLocation(18, 3), "6"),
                Map.entry(new CellLocation(16, 2), "6"),
                Map.entry(new CellLocation(18, 4), "6"),
                Map.entry(new CellLocation(14, 1), "6"),
                Map.entry(new CellLocation(16, 3), "6"),
                Map.entry(new CellLocation(14, 2), "6"),
                Map.entry(new CellLocation(16, 4), "6"),
                Map.entry(new CellLocation(12, 1), "6"),
                Map.entry(new CellLocation(14, 3), "6"),
                Map.entry(new CellLocation(12, 2), "6"),
                Map.entry(new CellLocation(14, 4), "6"),
                Map.entry(new CellLocation(12, 3), "6"),
                Map.entry(new CellLocation(8, 0), "6"),
                Map.entry(new CellLocation(12, 4), "6"),
                Map.entry(new CellLocation(6, 0), "6")
        ));
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(19, 1), "6"),
                Map.entry(new CellLocation(18, 1), "6"),
                Map.entry(new CellLocation(19, 2), "6"),
                Map.entry(new CellLocation(17, 1), "6"),
                Map.entry(new CellLocation(18, 2), "6"),
                Map.entry(new CellLocation(19, 3), "6"),
                Map.entry(new CellLocation(15, 0), "6"),
                Map.entry(new CellLocation(16, 1), "6"),
                Map.entry(new CellLocation(17, 2), "6"),
                Map.entry(new CellLocation(18, 3), "6"),
                Map.entry(new CellLocation(19, 4), "6"),
                Map.entry(new CellLocation(14, 0), "6"),
                Map.entry(new CellLocation(16, 2), "6"),
                Map.entry(new CellLocation(17, 3), "6"),
                Map.entry(new CellLocation(18, 4), "6"),
                Map.entry(new CellLocation(13, 0), "6"),
                Map.entry(new CellLocation(16, 3), "6"),
                Map.entry(new CellLocation(17, 4), "6"),
                Map.entry(new CellLocation(16, 4), "6")
        ));
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        ui.simulateTick();
        picker.setFixedPiece(5);
        ui.simulateTick();
        assertEmptyExcept(Map.ofEntries(
                Map.entry(new CellLocation(1, 0), "6"),
                Map.entry(new CellLocation(17, 0), "6"),
                Map.entry(new CellLocation(0, 0), "6"),
                Map.entry(new CellLocation(16, 0), "6"),
                Map.entry(new CellLocation(3, 0), "6"),
                Map.entry(new CellLocation(19, 0), "6"),
                Map.entry(new CellLocation(2, 0), "6"),
                Map.entry(new CellLocation(18, 0), "6")
        ));
    }
}
