package sheep.sheets;

import sheep.core.ViewElement;
import sheep.expression.Expression;
import sheep.parsing.Parser;

import java.util.Map;

/**
 * The GameSheet class is a small extension to Sheet
 * that renders cells different colours based on the content.
 * <p>
 * Cells containing a value of 1-8 will have a different background colour.
 */
public class GameSheet extends Sheet {
    /**
     * Construct a new instance of the sheet class.
     * <p>
     * A sheet should initially be populated in every cell with the defaultExpression.
     *
     * @param parser            The parser instance used to create expressions.
     * @param builtins          A mapping of built-in identifiers to expressions.
     * @param defaultExpression The default expression to load in every cell.
     * @param rows              Amount of rows for the new sheet.
     * @param columns           Amount of columns for the new sheet.
     * @requires rows &gt; 0
     * @requires columns &gt; 0 &amp;&amp; columns &lt; 26
     */
    public GameSheet(Parser parser, Map<String, Expression> builtins,
                     Expression defaultExpression, int rows, int columns) {
        super(parser, builtins, defaultExpression, rows, columns);
    }

    @Override
    public ViewElement valueAt(int row, int column) {
        ViewElement result = super.valueAt(row, column);
        return new ViewElement(result.getContent(), colourOf(result.getContent()), "black");
    }

    /**
     * Determine an appropriate colour to render the cell
     * based on its content.
     */
    private static String colourOf(String colourId) {
        return switch (colourId) {
            case "1" -> "black";
            case "2" -> "red";
            case "3" -> "yellow";
            case "4" -> "green";
            case "5" -> "blue";
            case "6" -> "cyan";
            case "7" -> "orange";
            case "8" -> "pink";
            default -> "white";
        };
    }
}
