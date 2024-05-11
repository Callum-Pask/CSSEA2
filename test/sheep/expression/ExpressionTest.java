package sheep.expression;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

class StubExpr extends Expression {
    @Override
    public Set<String> dependencies() {
        return null;
    }

    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        return null;
    }

    @Override
    public long value() throws TypeError {
        return 0;
    }

    @Override
    public String render() {
        return null;
    }
}

public class ExpressionTest {
    public static final double testWeight = 1;

    Class<?> expressionClass;

    @Before
    public void setUp() throws ClassNotFoundException {
        expressionClass = Class.forName("sheep.expression.Expression");
    }

    /**
     * Assert that the Expression class is abstract.
     */
    @Test
    @Deprecated
    public void testExpressionAbstract() {
        int modifiers = expressionClass.getModifiers();
        assertTrue("Expression class is not abstract", Modifier.isAbstract(modifiers));
    }

    /**
     * Assert that `isReference` returns false by default.
     */
    @Test
    @Deprecated
    public void testIsReferenceDefault() {
        Expression expr = new StubExpr();
        assertFalse("Expression base class does not return false for isReference.",
                expr.isReference());
    }
}
