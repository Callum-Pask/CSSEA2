package sheep.expression.basic;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.TypeError;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class NothingTest {
    public static final int testWeight = 2;

    private Nothing base;

    @Before
    public void setUp() {
        base = new Nothing();
    }

    @Test
    public void testDependencies() {
        assertEquals(new HashSet<>(), base.dependencies());
    }

    /**
     * Assert that the value of `Nothing.render()` is the empty string "".
     */
    @Test
    @Deprecated
    public void testRender() {
        assertEquals("", base.render());
    }

    @Test
    public void testToString() {
        assertEquals("NOTHING", base.toString());
    }

    /**
     * Assert that calling `Nothing.value()` results in a TypeError.
     */
    @Test(expected = TypeError.class)
    @Deprecated
    public void testValue() throws TypeError {
        base.value();
    }

    /**
     * Assert that calling `Nothing.value(Map)` returns in the same instance of nothing.
     * assertEquals(nothing, nothing.value(new HashMap<>()));
     */
    @Test
    @Deprecated
    public void testValueState() throws TypeError {
        assertEquals(base, base.value(new HashMap<>()));
    }
}
