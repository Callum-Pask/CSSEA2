package sheep.expression.arithmetic;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Reference;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

class Exp extends Arithmetic {
    public Exp(Expression[] arguments) {
        super("^", arguments);
    }

    @Override
    protected long perform(long[] arguments) {
        return 0;
    }
}

public class ArithmeticTest {
    public static final int testWeight = 8;
    private Arithmetic base;

    @Before
    public void setUp() {
        base = new Exp(new Expression[]{new Constant(1)});
    }

    @Test
    public void testDependencies() {
        assertEquals(new HashSet<>(), base.dependencies());
    }

    @Test
    public void testOneDependency() {
        base = new Exp(new Expression[]{new Constant(1), new Reference("Hi")});
        assertEquals(new HashSet<>(Collections.singleton("Hi")), base.dependencies());
    }

    @Test
    public void testMultipleDependencies() {
        base = new Exp(new Expression[]{new Reference("Hello"), new Reference("Hi")});
        assertEquals(new HashSet<>(List.of("Hello", "Hi")), base.dependencies());
    }

    @Test
    public void testNestedDependencies() {
        base = new Exp(new Expression[]{new Exp(new Expression[]{new Reference("Hello")}), new Reference("Hi")});
        assertEquals(new HashSet<>(List.of("Hello", "Hi")), base.dependencies());
    }

    /**
     * Assert that the result of `Arithmetic.divide` is an instance of the `Divide` class.
     */
    @Test
    @Deprecated
    public void testDivide() {
        base = Arithmetic.divide(new Expression[]{new Constant(1), new Reference("A0")});
        assertTrue("Arithmetic.divide does not produce a Divide instance",
                base instanceof Divide);
    }

    @Test
    public void testEqual() {
        base = Arithmetic.equal(new Expression[]{new Constant(1), new Reference("A0")});
        assertTrue("Arithmetic.equal does not produce a Equal instance",
                base instanceof Equal);
    }

    @Test
    public void testLess() {
        base = Arithmetic.less(new Expression[]{new Constant(1), new Reference("A0")});
        assertTrue("Arithmetic.less does not produce a Less instance",
                base instanceof Less);
    }

    /**
     * Assert that the result of `Arithmetic.minus` is an instance of the `Minus` class.
     */
    @Test
    @Deprecated
    public void testMinus() {
        base = Arithmetic.minus(new Expression[]{new Constant(1), new Reference("A0")});
        assertTrue("Arithmetic.minus does not produce a Minus instance",
                base instanceof Minus);
    }

    @Test
    public void testPlus() {
        base = Arithmetic.plus(new Expression[]{new Constant(1), new Reference("A0")});
        assertTrue("Arithmetic.plus does not produce a Plus instance",
                base instanceof Plus);
    }

    @Test
    public void testTimes() {
        base = Arithmetic.times(new Expression[]{new Constant(1), new Reference("A0")});
        assertTrue("Arithmetic.times does not produce a Times instance",
                base instanceof Times);
    }

    /**
     * Ensure that the `value(Map)` method of an `Arithmetic` subclass will call the `perform(long[])` method.
     * `Arithmetic` should handle the resolution of sub-expressions to long values then
     * perform the calculation within subclasses by using the `perform(long[])`.
     */
    @Test
    @Deprecated
    public void testCallPerform() throws TypeError {
        final boolean[] calledRight = {false};
        class Exp extends Arithmetic {
            public Exp(Expression[] arguments) {
                super("^", arguments);
            }

            @Override
            protected long perform(long[] arguments) {
                if (arguments.length == 2) {
                    if (arguments[0] == 4 && arguments[1] == 6) {
                        calledRight[0] = true;
                    }
                }
                return 0;
            }
        }

        Arithmetic exp = new Exp(new Expression[]{new Constant(4), new Constant(6)});
        Expression result = exp.value(new HashMap<>());

        assertTrue("The perform abstract method is not correctly called by Arithmetic",
                calledRight[0]);
        assertTrue("Result of value(Map<String, Expression>) is not a constant", result instanceof Constant);
        assertEquals("Result of value(Map<String, Expression>) is not zero", 0, ((Constant) result).getValue());
    }

    /**
     * Assert that an `Arithmetic` with one subexpression will render as just the `render` of the sub-expression.
     */
    @Test
    @Deprecated
    public void testRenderOne() {
        assertEquals("1", base.render());
    }

    /**
     * Assert that an `Arithmetic` with two subexpression will render as
     * the two result of calling `render` on each subexpression,
     * joined by the operator.
     */
    @Test
    @Deprecated
    public void testRenderTwo() {
        Expression exp = new Exp(new Expression[]{new Constant(2), new Constant(3)});
        assertEquals("2 ^ 3", exp.render());
    }

    @Test
    public void testRenderMultiple() {
        Expression exp = new Exp(new Expression[]{new Constant(2), new Constant(3), new Constant(4), new Constant(5)});
        assertEquals("2 ^ 3 ^ 4 ^ 5", exp.render());
    }

    /**
     * Assert that an `Arithmetic` with one subexpression will return the `render` of the sub-expression in `toString`.
     */
    @Test
    @Deprecated
    public void testToStringOne() {
        assertEquals("1", base.toString());
    }

    /**
     * Assert that when `Arithmetic.toString()` is called on an `Arithmetic` with two sub-expressions,
     * the result of calling `toString()` is the result of `render()` on each subexpression joined by the operator.
     */
    @Test
    @Deprecated
    public void testToStringTwo() {
        Expression exp = new Exp(new Expression[]{new Constant(2), new Constant(3)});
        assertEquals("2 ^ 3", exp.toString());
    }

    @Test
    public void testToStringMultiple() {
        Expression exp = new Exp(new Expression[]{new Constant(2), new Constant(3), new Constant(4), new Constant(5)});
        assertEquals("2 ^ 3 ^ 4 ^ 5", exp.toString());
    }

    @Test(expected = TypeError.class)
    public void testValue() throws TypeError {
        base.value();
    }
}
