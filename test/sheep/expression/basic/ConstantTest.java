package sheep.expression.basic;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ConstantTest {
    public static final int testWeight = 3;

    private Constant base;
    private Constant other;
    private Constant same;

    @Before
    public void setUp() {
        base = new Constant(24);
        other = new Constant(20);
        same = new Constant(24);
    }

    @Test
    public void testDependencies() {
        assertEquals(new HashSet<>(), base.dependencies());
    }

    @Test
    public void testEquals() {
        assertNotEquals(other, base);
        assertEquals(same, base);
        assertNotEquals(other, same);
    }

    @Test
    public void testGetValue() {
        assertEquals(24, base.getValue());
        assertEquals(20, other.getValue());
        assertEquals(24, same.getValue());
    }

    @Test
    public void testHashCode() {
        assertEquals(base.hashCode(), same.hashCode());
    }

    @Test
    public void testRender() {
        assertEquals("24", base.render());
        assertEquals("20", other.render());
        assertEquals("24", same.render());
    }

    @Test
    public void testToString() {
        assertEquals("CONSTANT(24)", base.toString());
        assertEquals("CONSTANT(20)", other.toString());
        assertEquals("CONSTANT(24)", same.toString());
    }

    @Test
    public void testValue() {
        assertEquals(24, base.value());
        assertEquals(20, other.value());
        assertEquals(24, same.value());
    }

    @Test
    public void testValueState() {
        assertEquals(base, base.value(new HashMap<>()));
        assertEquals(other, other.value(new HashMap<>()));
        assertEquals(same, same.value(new HashMap<>()));
        assertEquals(base, same.value(new HashMap<>()));
    }
}
