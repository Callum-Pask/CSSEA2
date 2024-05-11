package sheep.games.snake;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.features.Feature;
import sheep.games.random.*;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.*;

import java.util.*;

public class Snake implements Feature, Tick {
    private final Sheet sheet;
    private final RandomCell randomFreeCell;
    private boolean started = false;
    private List<CellLocation> snake; // List to store snake's body
    private CellLocation foodCell; // Store the location of the food cell
    private int dx = 0; // Delta x for horizontal movement
    private int dy = 0; // Delta y for vertical movement
    private static final int UP = -1;
    private static final int DOWN = 1;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;

    public Snake(Sheet sheet, RandomCell randomFreeCell) {
        this.sheet = sheet;
        this.snake = new ArrayList<>();
        this.randomFreeCell = randomFreeCell;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("snake-start", "Start Snake Game", new GameStart());
        ui.onKey("w", "Move Up", new Move(0, UP));
        ui.onKey("a", "Move Left", new Move(LEFT, 0));
        ui.onKey("s", "Move Down", new Move(0, DOWN));
        ui.onKey("d", "Move Right", new Move(RIGHT, 0));
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        }

        // Move the snake
        move();

        // Check for collision with itself
        if (isSelfCollision()) {
            prompt.message("Game Over!");
            return false;
        }

        // Check for collision with walls
        if (isWallCollision()) {
            prompt.message("Game Over!");
            return false;
        }

        return true;
    }

    private void move() {
        // Update snake's head position
        CellLocation head = snake.get(0);
        CellLocation newHead = new CellLocation(head.getRow() + dy, head.getColumn() + dx);

        // Check if the new head position is the food cell
        if (foodCell != null && foodCell.equals(newHead)) {
            snake.add(0, foodCell); // Grow the snake
            CellLocation newfoodCell = randomFreeCell.pick(); // Place a new food cell
            if (snake.subList(0, snake.size()).contains(newfoodCell)){
                foodCell = null;
            }
            else {
                foodCell = newfoodCell;
            }

        } else {
            // Clear the previous head cell
            clearCell(snake.get(snake.size()-1));

            // Add the new head to the snake
            snake.add(0, newHead);

            // Remove the tail to keep the snake length constant
            snake.remove(snake.size() - 1);
        }

        // Render the snake and food cell on the sheet
        renderSnake();
        renderFoodCell();
    }

    private void clearCell(CellLocation cell) {
        try {
            sheet.update(cell, new Nothing());
        } catch (TypeError e) {
            throw new RuntimeException(e);
        }
    }

    private void renderSnake() {
        // Clear previous snake cells
        for (CellLocation cell : snake) {
            try {
                sheet.update(cell, new Constant(1L));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void renderFoodCell() {
        try {
            if (foodCell != null) {
            sheet.update(foodCell, new Constant(2L));
            } // Place the food cell on the sheet
        } catch (TypeError e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSelfCollision() {
        CellLocation head = snake.get(0);
        // Check if the head is present in any other cell of the snake's body
        return snake.subList(1, snake.size()).contains(head);
    }

    private boolean isWallCollision() {
        CellLocation head = snake.get(0);
        int newRow = head.getRow();
        int newColumn = head.getColumn();

//        System.out.print(newRow);
//        System.out.print(newColumn);
//        System.out.print(sheet.getColumns());
//        System.out.print("Hello");
        // Check if the new position is out of bounds
        return newRow < 0 || newRow >= sheet.getRows() || newColumn < 0 || newColumn >= sheet.getColumns()-1;
    }

    public class GameStart implements Perform {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            started = true;
            // Clear the sheet when starting a new game
            clearSheet();
            // Initialize snake at top-left corner
            snake.clear();
            snake.add(new CellLocation(1, 1));
            // Set initial movement direction
            dx = 0;
            dy = 1; // Move downwards

            // Place the food cell
            foodCell = randomFreeCell.pick();

            // Render initial snake position and food cell
            renderSnake();
            renderFoodCell();
        }
    }

    private void clearSheet() {
        // Clear the sheet
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                try {
                    sheet.update(new CellLocation(row, column), new Nothing());
                } catch (TypeError e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class Move implements Perform {
        private final int dx;
        private final int dy;

        public Move(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public void perform(int row, int column, Prompt prompt) {
            if (!started) {
                return;
            }

            Snake.this.dx = dx;
            Snake.this.dy = dy;
        }
    }
}

