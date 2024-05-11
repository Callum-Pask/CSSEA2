package sheep.expression;

/**
 * Factory of {@link Expression} instances.
 * <p>
 * This interface is a factory, a way of hiding the creation of objects.
 * By hiding the object creation, we can swap which objects are created later.
 * We will do this in the second assignment.
 * @stage1
 */
public interface ExpressionFactory {
    /**
     * Construct an expression representing a reference to the given identifier.
     *
     * @param identifier A reference to either a cell or a built-in.
     * @return A reference expression to the identifier.
     */
    Expression createReference(String identifier);

    /**
     * Construct a numeric constant expression that holds the given value.
     *
     * @param value A constant long value of the expression.
     * @return A numeric constant expression.
     */
    Expression createConstant(long value);

    /**
     * Create an expression that represents an empty cell and stores no information.
     *
     * @return An empty expression.
     */
    Expression createEmpty();

    /**
     * Construct an operator based on the given identifying name and with
     * the provided operator arguments.
     *
     * @param name An identifier for the operator, e.g. +, *.
     * @param args Arguments to the operator.
     * @return An appropriate operator expression.
     * @throws InvalidExpression If the operator name is invalid (i.e. not known),
     * or the arguments to the operator are inappropriate (e.g. wrong type, too few, too many, etc).
     */
    Expression createOperator(String name, Object[] args) throws InvalidExpression;
}
