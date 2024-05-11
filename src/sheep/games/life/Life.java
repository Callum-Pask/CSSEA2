package sheep.games.life;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.*;


public class Life implements Feature, Tick {
    private final Sheet sheet;
    private boolean running = false;

    public Life(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("gol-start", "Start Game of Life", new StartGame());
        ui.addFeature("gol-end", "Stop Game of Life", new StopGame());
    }

    @Override
    public boolean onTick(Prompt prompt) {
        if (running) {
            updateSheet();
            return true; // Spreadsheet needs to be re-rendered
        }
        return false; // No changes in the spreadsheet
    }

    private void updateSheet() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int col = 0; col < sheet.getColumns(); col++) {
                int neighbors = countOnNeighbors(row, col);
                if (sheet.valueAt(row, col).getContent().equals("1")) {
                    // Cell is on
                    if (neighbors < 2 || neighbors > 3) {
                        // Cell turns off due to underpopulation or overpopulation
                        sheet.update(row, col, "");
                    } else {
                        // Cell survives
                        sheet.update(row, col, "1");
                    }
                } else {
                    // Cell is off
                    if (neighbors == 3) {
                        // Cell turns on due to reproduction
                        sheet.update(row, col, "1");
                    } else {
                        // Cell remains off
                        sheet.update(row, col, "");
                    }
                }
            }
        }
    }


    private int countOnNeighbors(int row, int col) {
        int count = 0;
        int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] offset : offsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];
            if (newRow >= 0 && newRow < sheet.getRows() && newCol >= 0 && newCol < sheet.getColumns()) {
                if (sheet.valueAt(newRow, newCol).getContent().equals("1")) {
                    count++;
                }
            }
        }
        return count;
    }

    public class StartGame implements Perform {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = true;
        }
    }

    public class StopGame implements Perform {
        @Override
        public void perform(int row, int column, Prompt prompt) {
            running = false;
        }
    }
}
