package sheep.parsing;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.ExpressionFactory;
import sheep.expression.InvalidExpression;
import sheep.expression.TypeError;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

class InfoExpr extends Expression {

    private String info;

    public InfoExpr(String info) {
        this.info = info;
    }

    public String getInfo() {
        return "(" + info + ")";
    }

    @Override
    public String toString() {
        return getInfo();
    }

    @Override
    public Set<String> dependencies() {
        return null;
    }

    @Override
    public long value() throws TypeError {
        return 0;
    }

    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        return null;
    }

    @Override
    public String render() {
        return null;
    }
}

class EchoFactory implements ExpressionFactory {

    @Override
    public Expression createReference(String identifier) {
        return new InfoExpr("Reference: " + identifier);
    }

    @Override
    public Expression createConstant(long value) {
        return new InfoExpr("Constant: " + value);
    }

    @Override
    public Expression createEmpty() {
        return new InfoExpr("Empty");
    }

    @Override
    public Expression createOperator(String name, Object[] args) throws InvalidExpression {
        return new InfoExpr("Operator: " + name + " " + Arrays.toString(args));
    }
}

public class SimpleParserTest {
    public static final int testWeight = 10;

    private Parser parser;

    @Before
    public void setUp() {
        parser = new SimpleParser(new EchoFactory());
    }

    /**
     * Assert that parsing the empty string returns the
     * result of `createEmpty()` from the parsers factory.
     * Note: this is not necessarily an instance of `Nothing`.
     */
    @Test
    @Deprecated
    public void testNothing() throws ParseException {
        Expression result = parser.parse("");
        assertEquals("(Empty)", result.toString());
    }

    @Test
    public void testNothingSpace() throws ParseException {
        Expression result = parser.parse(" ");
        assertEquals("(Empty)", result.toString());
    }

    /**
     * Assert that parsing the string "\t    " returns the
     * result of `createEmpty()` from the parsers factory.
     * Note: this is not necessarily an instance of `Nothing`.
     */
    @Test
    @Deprecated
    public void testNothingWhiteSpace() throws ParseException {
        Expression result = parser.parse("\t    ");
        assertEquals("(Empty)", result.toString());
    }

    @Test
    public void testConstant() throws ParseException {
        Expression expression = parser.parse("42");
        assertEquals("(Constant: 42)", expression.toString());
    }

    /**
     * Assert that parsing the string "  42\t" returns the
     * result of an appropriate call to `createConstant(long)` from the parsers factory.
     */
    @Test
    @Deprecated
    public void testConstantWhitespace() throws ParseException {
        Expression expression = parser.parse("  42\t");
        assertEquals("(Constant: 42)", expression.toString());
    }

    /**
     * Assert that parsing the string "42.0" results in a `ParseException`.
     */
    @Test(expected = ParseException.class)
    @Deprecated
    public void testConstantFloat() throws ParseException {
        Expression expression = parser.parse("42.0");
    }

    @Test
    public void testConstantNegative() throws ParseException {
        Expression expression = parser.parse("-42");
        assertEquals("(Constant: -42)", expression.toString());
    }

    /**
     * Assert that parsing the string "00000" returns the
     * result of an appropriate call to `createConstant(long)` from the parsers factory.
     */
    @Test
    @Deprecated
    public void testConstantStripZeros() throws ParseException {
        Expression expression = parser.parse("00000");
        assertEquals("(Constant: 0)", expression.toString());
    }

    /**
     * Assert that parsing the string "   -42" returns the
     * result of an appropriate call to `createConstant(long)` from the parsers factory.
     */
    @Test
    @Deprecated
    public void testConstantNegativeSpace() throws ParseException {
        Expression expression = parser.parse("   -42");
        assertEquals("(Constant: -42)", expression.toString());
    }

    @Test
    public void testArithmeticPlus() throws ParseException {
        Expression expression = parser.parse("3 + 20 + 12 + 100");
        assertEquals("(Operator: + [(Constant: 3), (Constant: 20), (Constant: 12), (Constant: 100)])", expression.toString());
    }

    /**
     * Assert that parsing the string "3+20+12+100" returns the
     * result of an appropriate call to `createOperator(String, Object[])` from the parsers factory.
     * i.e.
     * Expression three = factory.createConstant(3);
     * Expression twenty = factory.createConstant(20);
     * Expression twelve = factory.createConstant(12);
     * Expression hundred = factory.createConstant(100);
     * Expression result = factory.createOperator("+", new Expression[]{three, twenty, twelve, hundred});
     */
    @Test
    @Deprecated
    public void testArithmeticPlusNoSpace() throws ParseException {
        Expression expression = parser.parse("3+20+12+100");
        assertEquals("(Operator: + [(Constant: 3), (Constant: 20), (Constant: 12), (Constant: 100)])", expression.toString());
    }

    @Test
    public void testArithmeticPlusMixedSpace() throws ParseException {
        Expression expression = parser.parse("3+ 20 +12+ 100");
        assertEquals("(Operator: + [(Constant: 3), (Constant: 20), (Constant: 12), (Constant: 100)])", expression.toString());
    }

    /**
     * Assert that parsing the string "3* 20 *12* 100" returns the
     * result of an appropriate call to `createOperator(String, Object[])` from the parsers factory.
     * i.e.
     * Expression three = factory.createConstant(3);
     * Expression twenty = factory.createConstant(20);
     * Expression twelve = factory.createConstant(12);
     * Expression hundred = factory.createConstant(100);
     * Expression result = factory.createOperator("*", new Expression[]{three, twenty, twelve, hundred});
     */
    @Test
    @Deprecated
    public void testArithmeticTimesMixedSpace() throws ParseException {
        Expression expression = parser.parse("3* 20 *12* 100");
        assertEquals("(Operator: * [(Constant: 3), (Constant: 20), (Constant: 12), (Constant: 100)])", expression.toString());
    }

    /**
     * Assert that parsing the string "3* 2 * 20 - 2/15 +12* 100" returns the
     * result of an appropriate call to `createOperator(String, Object[])` from the parsers factory.
     *
     * The below is an example of how the expression should be parsed, including nesting.
     * (Operator: + [(Operator: - [(Operator: * [(Constant: 3), (Constant: 2), (Constant: 20)]), (Operator: / [(Constant: 2), (Constant: 15)])]), (Operator: * [(Constant: 12), (Constant: 100)])])
     */
    @Test
    @Deprecated
    public void testArithmeticNested() throws ParseException {
        Expression expression = parser.parse("3* 2 * 20 - 2/15 +12* 100");
        assertEquals("(Operator: + [(Operator: - [(Operator: * [(Constant: 3), (Constant: 2), (Constant: 20)]), (Operator: / [(Constant: 2), (Constant: 15)])]), (Operator: * [(Constant: 12), (Constant: 100)])])", expression.toString());
    }

    /**
     * Assert that parsing the string "A0" returns the
     * result of an appropriate call to `createReference(String)` from the parsers factory.
     */
    @Test
    @Deprecated
    public void testReference() throws ParseException {
        Expression expression = parser.parse("A0");
        assertEquals("(Reference: A0)", expression.toString());
    }

    @Test
    public void testOddReference() throws ParseException {
        Expression expression = parser.parse("sd45678fghjk");
        assertEquals("(Reference: sd45678fghjk)", expression.toString());
    }

    /**
     * Assert that parsing the string "   OO  " returns the
     * result of an appropriate call to `createReference(String)` from the parsers factory.
     * i.e. createReference("OO").
     */
    @Test
    @Deprecated
    public void testReferenceSpaces() throws ParseException {
        Expression expression = parser.parse("   OO  ");
        assertEquals("(Reference: OO)", expression.toString());
    }

    /**
     * Assert that parsing "   O_O  " throws a ParseException.
     */
    @Test(expected = ParseException.class)
    @Deprecated
    public void testInvalidChars() throws ParseException {
        Expression expression = parser.parse("   O_O  ");
    }

    /**
     * Assert that parsing "_ =_" throws a ParseException.
     */
    @Test(expected = ParseException.class)
    @Deprecated
    public void testInvalidChars1() throws ParseException {
        Expression expression = parser.parse("_ =_");
    }
}
