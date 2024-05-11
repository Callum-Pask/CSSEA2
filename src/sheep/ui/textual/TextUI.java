package sheep.ui.textual;

import sheep.core.UpdateResponse;
import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.ui.UI;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringJoiner;

public class TextUI extends UI {
    private final Scanner input;
    private final PrintStream output;

    public TextUI(SheetView view, SheetUpdate updater) {
        this(view, updater, System.out);
    }

    public TextUI(SheetView view, SheetUpdate updater, PrintStream output) {
        this(view, updater, output, new Scanner(System.in));
    }

    public TextUI(SheetView view, SheetUpdate updater, PrintStream output, Scanner input) {
        super(view, updater);
        this.input = input;
        this.output = output;
    }

    private String columnHeaders(int columns) {
        String[] values = new String[columns];
        for (int column = 0; column < columns; column++) {
            values[column] = String.valueOf(Character.valueOf((char) (column + 65)));
        }
        return renderRow(-1, values);
    }

    private int maxWidth(int column) {
        int max = 0;
        for (int row = 0; row < view.getRows(); row++) {
            int width = view.valueAt(row, column).getContent().length();
            if (width > max) {
                max = width;
            }
        }
        return max;
    }

    private String renderRow(int row, String[] values) {
        StringJoiner renderedRow = new StringJoiner(" | ");
        String rowHeader = row == -1 ? "" : "" + (row + 1);
        int headerPadding = (view.getRows() / 10) - rowHeader.length();
        renderedRow.add(rowHeader + " ".repeat(headerPadding));
        for (int column = 0; column < view.getColumns(); column++) {
            int maxWidth = maxWidth(column);
            String cellValue = values[column];
            int padding = maxWidth - cellValue.length();
            renderedRow.add(cellValue + " ".repeat(padding));
        }
        renderedRow.add("");
        return renderedRow.toString();
    }

    public String renderSheet() {
        StringJoiner rendered = new StringJoiner("\n");
        rendered.add(columnHeaders(view.getColumns()));
        for (int row = 0; row < view.getRows(); row++) {
            String[] values = new String[view.getColumns()];
            for (int column = 0; column < view.getColumns(); column++) {
                values[column] = view.valueAt(row, column).getContent();
            }
            rendered.add(renderRow(row, values));
        }
        return rendered.toString();
    }

    private Optional<Integer> readRow(String input) {
        try {
            return Optional.of(Integer.parseInt(input.substring(1)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> readColumn(String input) {
        if (Character.isAlphabetic(input.charAt(0))) {
            return Optional.of(input.charAt(0) - 64);
        }
        return Optional.empty();
    }

    private boolean handleAction(String action) {
        switch (action) {
            case "view", "v" -> {
                output.print("reference: ");
                String reference = input.nextLine();
                Optional<Integer> maybeRow = readRow(reference);
                Optional<Integer> maybeColumn = readColumn(reference);
                if (maybeRow.isEmpty() || maybeColumn.isEmpty()) {
                    output.println("Invalid reference");
                    return false;
                }
                String formula = view.formulaAt(
                        maybeRow.get() - 1,
                        maybeColumn.get() - 1
                ).getContent();
                output.println(formula);
            }
            case "update", "u" -> {
                output.print("reference: ");
                String reference = input.nextLine();
                Optional<Integer> maybeRow = readRow(reference);
                Optional<Integer> maybeColumn = readColumn(reference);
                if (maybeRow.isEmpty() || maybeColumn.isEmpty()) {
                    output.println("Invalid reference");
                    return false;
                }
                output.print("value: ");
                String value = input.nextLine();
                UpdateResponse response = updater.update(
                        maybeRow.get()- 1,
                        maybeColumn.get() - 1, value);
                if (!response.isSuccess()) {
                    output.println("Error: " + response.getMessage());
                }
            }
            case "quit", "q" -> {
                return true;
            }
            default ->
                output.println("Unknown action");
        }
        return false;
    }

    public void render() {
        while (true) {
            output.println(renderSheet());
            output.print("action: ");
            String action = input.nextLine();
            if (handleAction(action)) {
                break;
            }
        }
    }
}
