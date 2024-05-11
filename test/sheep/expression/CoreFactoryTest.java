package sheep.expression;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.arithmetic.Arithmetic;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.expression.basic.Reference;

import static org.junit.Assert.*;

public class CoreFactoryTest {
    public static final double testWeight = 10;

    private CoreFactory factory;

    @Before
    public void setUp() {
        this.factory = new CoreFactory();
    }

    /**
     * Assert that `createReference(String)` returns an instance of Reference
     * with an appropriate identifier.
     */
    @Test
    @Deprecated
    public void referenceA0() {
        Expression expression = factory.createReference("A0");
        if (!(expression instanceof Reference ref)) {
            fail("Expression created by createReference is not a Reference.");
            return;
        }
        assertEquals("A0", ref.getIdentifier());
    }

    @Test
    public void referenceF() {
        Expression expression = factory.createReference("F");
        if (!(expression instanceof Reference ref)) {
            fail("Expression created by createReference is not a Reference.");
            return;
        }
        assertEquals("F", ref.getIdentifier());
    }

    @Test
    public void referenceMYWORD() {
        Expression expression = factory.createReference("MYWORD");
        if (!(expression instanceof Reference ref)) {
            fail("Expression created by createReference is not a Reference.");
            return;
        }
        assertEquals("MYWORD", ref.getIdentifier());
    }

    /**
     * Assert that `createConstant(long)` returns an instance of Constant
     * with an appropriate value.
     */
    @Test
    @Deprecated
    public void constant0() {
        Expression expression = factory.createConstant(0);
        if (!(expression instanceof Constant con)) {
            fail("Expression created by createConstant is not a Constant");
            return;
        }
        assertEquals(0, con.getValue());
    }

    @Test
    public void constantNeg() {
        Expression expression = factory.createConstant(-24);
        if (!(expression instanceof Constant con)) {
            fail("Expression created by createConstant is not a Constant");
            return;
        }
        assertEquals(-24, con.getValue());
    }

    @Test
    public void constantLarge() {
        Expression expression = factory.createConstant(4294967296L);
        if (!(expression instanceof Constant con)) {
            fail("Expression created by createConstant is not a Constant");
            return;
        }
        assertEquals(4294967296L, con.getValue());
    }

    @Test
    public void constant48() {
        Expression expression = factory.createConstant(48);
        if (!(expression instanceof Constant con)) {
            fail("Expression created by createConstant is not a Constant");
            return;
        }
        assertEquals(48, con.getValue());
    }

    /**
     * Assert that `createEmpty()` returns an instance of Nothing.
     */
    @Test
    @Deprecated
    public void empty() {
        Expression expression = factory.createEmpty();
        if (!(expression instanceof Nothing)) {
            fail("Expression created by createConstant is not a Constant");
        }
    }

    /**
     * Assert that `createOperator` when passed "+" creates
     * an instance of Arithmetic.
     */
    @Test
    @Deprecated
    public void operatorPlus() throws InvalidExpression {
        Expression expression = factory.createOperator("+", new Expression[]{new Constant(1), new Constant(2)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test
    public void operatorMinus() throws InvalidExpression {
        Expression expression = factory.createOperator("-", new Expression[]{new Constant(2), new Constant(1)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test
    public void operatorUnaryMinus() throws InvalidExpression {
        Expression expression = factory.createOperator("-", new Expression[]{new Constant(2)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test
    public void operatorTimes() throws InvalidExpression {
        Expression expression = factory.createOperator("*", new Expression[]{new Constant(2), new Constant(64)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test
    public void operatorDivide() throws InvalidExpression {
        Expression expression = factory.createOperator("/", new Expression[]{new Constant(64), new Constant(2)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test
    public void operatorLess() throws InvalidExpression {
        Expression expression = factory.createOperator("<", new Expression[]{new Reference("A0"), new Constant(2)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test
    public void operatorEqual() throws InvalidExpression {
        Expression expression = factory.createOperator("=", new Expression[]{new Constant(12), new Constant(24)});
        if (!(expression instanceof Arithmetic)) {
            fail("Expression created by createOperator is not a Arithmetic");
        }
    }

    @Test(expected = InvalidExpression.class)
    public void operatorExp() throws InvalidExpression {
        factory.createOperator("^", new Expression[]{new Constant(12), new Constant(24)});
    }

    @Test(expected = InvalidExpression.class)
    public void operatorNoArgs() throws InvalidExpression {
        factory.createOperator("+", new Expression[]{});
    }

    @Test(expected = InvalidExpression.class)
    public void operatorWrongType() throws InvalidExpression {
        factory.createOperator("+", new Object[]{1, 2});
    }

    @Test(expected = InvalidExpression.class)
    public void operatorWrongType2() throws InvalidExpression {
        factory.createOperator("+", new Object[]{"1", "2"});
    }
}
