package sheep.sheets;

import sheep.expression.Expression;
import sheep.parsing.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder pattern to construct {@link Sheet} instances.
 * <p>
 * A sheet builder maintains a collection of built-in expressions.
 * These map identifiers to expressions such that any expression
 * within the constructed sheet can reference the identifier and
 * evaluate to the expression.
 * <p>
 * For example, if the identifier 'hundred' was mapped to the number 100,
 * then any cell in the constructed sheet could use 'hundred' in place of
 * 100 in a formula.
 * <pre>
 * {@code
 * SheetBuilder builder = new SheetBuilder(parser, exp);
 * builder.includeBuiltIn("hundred", new Number(100));
 * Sheet sheet = builder.empty(2, 2);
 * sheet.update(new GridLocation(1, 1), new Identifier("hundred"));
 * sheet.valueAt(new GridLocation(1, 1)) // 100
 * }
 * </pre>
 * @stage2
 */
public class SheetBuilder {
    private final Map<String, Expression> builtins = new HashMap<>();
    private final Parser parser;
    private final Expression defaultExpression;

    /**
     * Construct an instance of SheetBuilder than will create
     * Sheet instances using the given Parser and Expression instances.
     */
    public SheetBuilder(Parser parser, Expression defaultExpression) {
        this.parser = parser;
        this.defaultExpression = defaultExpression;
    }

    /**
     * Include a new built-in expression for the given identifier
     * within any sheet constructed by this builder instance.
     *
     * <pre>
     * {@code
     * Sheet sheet = new SheetBuilder(parser, exp)
     *         .includeBuiltIn("cafe", new Constant(3405691582))
     *         .includeBuiltIn("dood", new Constant(3490524077))
     *         .empty(10, 10);
     * }</pre>
     *
     * @param identifier A string identifier to be used in the constructed sheet.
     * @param expression The value that the identifier should resolve to within
     *                   the constructed sheet.
     * @requires identifier cannot be a valid cell location reference, e.g. A1.
     * @return The current instance of the SheetBuilder.
     */
    public SheetBuilder includeBuiltIn(String identifier, Expression expression) {
        this.builtins.put(identifier, expression);
        return this;
    }

    /**
     * Construct a new empty sheet with the given number of rows and columns.
     * <p>
     * If the built-ins are updated (i.e. {@link SheetBuilder#includeBuiltIn(String, Expression)} is called)
     * after a sheet has been constructed,
     * this must not affect the constructed sheet.
     *
     * <pre>
     * {@code
     * SheetBuilder builder = new SheetBuilder(parser, exp);
     * builder.includeBuiltIn("cafe", new Constant(3405691582));
     * Sheet sheet = builder.empty(10, 10);
     * builder.includeBuiltIn("hello", new Constant(20)); // Should not update the built-ins of `sheet`
     * }</pre>
     *
     * @param rows Amount of rows for the new sheet.
     * @param columns Amount of columns for the sheet.
     * @return A new sheet with the appropriate built-ins and of the specified dimensions.
     */
    public Sheet empty(int rows, int columns) {
        return new Sheet(parser, new HashMap<>(builtins), defaultExpression, rows, columns);
    }

}