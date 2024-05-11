package sheep.expression;

import sheep.expression.arithmetic.Arithmetic;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.expression.basic.Reference;

/**
 * An expression factory for the core expressions.
 * <p>
 * The core expressions are those which will be a part of assignment one.
 * @stage1
 */
public class CoreFactory implements ExpressionFactory {
    /**
     * An instance of {@link Reference} that stores the given identifier.
     *
     * @requires identifier != ""
     * @param identifier A reference to either a cell or a built-in.
     * @return An instance of {@link Reference} that stores the given identifier.
     */
    @Override
    public Expression createReference(String identifier) {
        return new Reference(identifier);
    }

    /**
     * An instance of {@link Constant} that stores the given value.
     *
     * @param value A constant long value of the expression.
     * @return An instance of {@link Constant} that stores the given value.
     */
    @Override
    public Expression createConstant(long value) {
        return new Constant(value);
    }

    /**
     * An instance of {@link Nothing}.
     * @return An instance of {@link Nothing}.
     */
    @Override
    public Expression createEmpty() {
        return new Nothing();
    }

    private Expression[] castArguments(String name, Object[] args) throws InvalidExpression {
        Expression[] arguments = new Expression[args.length];
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof Expression)) {
                throw new InvalidExpression(
                        "Argument to operator [" + name + "] not an expression");
            }
            arguments[i] = (Expression) args[i];
        }
        return arguments;
    }

    /**
     * An instance of {@link Arithmetic} based on the given operator name.
     * <p>
     * This method should handle operator names of;
     * <ul>
     *     <li>+,</li>
     *     <li>-,</li>
     *     <li>*,</li>
     *     <li>/,</li>
     *     <li>&lt;, or</li>
     *     <li>=;</li>
     * </ul>
     * and create the appropriate {@link Arithmetic} subclass.
     * If the operator name is not listed above, {@link InvalidExpression} should be thrown.
     *
     * @param name An identifier for the operator, e.g. +, *.
     * @param args A list of {@link Expression} instances as arguments to the {@link Arithmetic} instance.
     * @return An appropriate operator expression.
     * @throws InvalidExpression If the operator name is unknown or
     * if any of the given {@link Object} arguments are not subclasses of {@link Expression} or
     * if there are no arguments given.
     * @hint The {@code instanceof} operator can determine if an instance is a subclass of a class.
     * @stage2
     */
    @Override
    public Expression createOperator(String name, Object[] args) throws InvalidExpression {
        if (args.length < 1) {
            throw new InvalidExpression("No arguments provided");
        }
        return switch (name) {
            case "+" -> Arithmetic.plus(castArguments(name, args));
            case "-" -> Arithmetic.minus(castArguments(name, args));
            case "*" -> Arithmetic.times(castArguments(name, args));
            case "/" -> Arithmetic.divide(castArguments(name, args));
            case "<" -> Arithmetic.less(castArguments(name, args));
            case "=" -> Arithmetic.equal(castArguments(name, args));
            default ->
                throw new InvalidExpression("Unknown operator: " + name);
        };
    }
}
