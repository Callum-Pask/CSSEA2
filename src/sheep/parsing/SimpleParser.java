package sheep.parsing;

import sheep.expression.Expression;
import sheep.expression.ExpressionFactory;
import sheep.expression.InvalidExpression;

/**
 * Parser of basic expressions and arithmetic expressions.
 */
public class SimpleParser implements Parser {
    private final ExpressionFactory factory;

    /**
     * Construct a new parser.
     * Parsed expressions are constructed using the expression factory.
     *
     * @param factory Factory used to construct parsed expressions.
     */
    public SimpleParser(ExpressionFactory factory) {
        this.factory = factory;
    }

    private Expression[] tryParse(String[] inputs) throws ParseException, InvalidExpression {
        Expression[] expressions = new Expression[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            expressions[i] = tryParse(inputs[i]);
        }
        return expressions;
    }

    private Expression tryParse(String input) throws ParseException, InvalidExpression {
        input = input.strip();
        try {
            long number = Long.parseLong(input);
            return factory.createConstant(number);
        } catch (NumberFormatException ignored) {
            // ignore unable to parse
        }

        if (input.contains("=")) {
            return factory.createOperator("=", tryParse(input.split("=")));
        } else if (input.contains("<")) {
            return factory.createOperator("<", tryParse(input.split("<")));
        } else if (input.contains("+")) {
            return factory.createOperator("+", tryParse(input.split("\\+")));
        } else if (input.contains("-")) {
            return factory.createOperator("-", tryParse(input.split("-")));
        } else if (input.contains("*")) {
            return factory.createOperator("*", tryParse(input.split("\\*")));
        } else if (input.contains("/")) {
            return factory.createOperator("/", tryParse(input.split("/")));
        }

        for (char character : input.toCharArray()) {
            if (!(Character.isAlphabetic(character) || Character.isDigit(character))) {
                throw new ParseException("Unknown input: " + input);
            }
        }
        if (input.isEmpty()) {
            return factory.createEmpty();
        }
        return factory.createReference(input);
    }

    /**
     * Attempt to parse a string expression into an expression.
     * <ul>
     * <li>Leading and trailing whitespaces must not affect parsing.</li>
     * <li>
     * If the string is just whitespace, an empty expression should be constructed with {@link ExpressionFactory#createEmpty()}.
     * </li>
     * <li>
     * Any number that can be parsed as a long by the rules of {@link Long#parseLong(String)}
     * should be constructed as a constant with {@link ExpressionFactory#createConstant(long)}.
     * </li>
     * <li>
     * Any string that contains one of the operator names from the {@link sheep.expression.arithmetic}
     * package should be split on that operator name and the components between should be parsed.
     * The components between should follow the same rules as the top-level expression,
     * e.g. leading and trailing whitespace should be ignored, etc.
     * If any component cannot be parsed, the whole expression cannot be parsed.
     * Arithmetic expressions should be constructed with {@link ExpressionFactory#createOperator(String, Object[])}.
     * You must attempt to parse the arithmetic operators in the following order:
     * <ul>
     *     <li>=</li>
     *     <li>&lt;</li>
     *     <li>+</li>
     *     <li>-</li>
     *     <li>*</li>
     *     <li>/</li>
     * </ul>
     * You must ensure that the maximum amount of operands are used,
     * i.e. do not parse 4 + 5 + 6 as Plus(4, Plus(5, 6)) instead do Plus(4, 5, 6).
     * <p>
     * Note: This does not need to be implemented until stage 2.
     * </li>
     * <li>
     * Any remaining expressions that 
     * 1) cannot be parsed as a number or arithmetic expression, and
     * 2) only contains alphabetic {@link Character#isAlphabetic(int)} and digit characters {@link Character#isDigit(char)},
     * should be treated as a reference.
     * </li>
     * </ul>
     *
     * <pre>
     * {@code
     * ExpressionFactory factory = new CoreFactory();
     * Parser parser = new SimpleParser(factory);
     * parser.parse("  42  "); // Constant(42)
     * parser.parse("HEY "); // Reference("HEY")
     * parser.parse("hello + world"); // Plus(Reference("hello"), Reference("world"))
     * parser.parse("4 + 5 + 7 * 12 + 3"); // Plus(Constant(4), Constant(5), Times(Constant(7), Constant(12)), Constant(3))
     * }</pre>
     *
     * @param input A string to attempt to parse.
     * @return The result of parsing the expression.
     * @throws ParseException If the string input is not recognisable as an expression.
     */
    @Override
    public Expression parse(String input) throws ParseException {
        try {
            return tryParse(input);
        } catch (InvalidExpression e) {
            throw new ParseException(e);
        }
    }
}
