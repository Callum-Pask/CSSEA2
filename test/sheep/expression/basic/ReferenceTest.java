package sheep.expression.basic;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

public class ReferenceTest {
    public static final int testWeight = 3;

    private Reference base;
    private Reference other;
    private Reference same;

    @Before
    public void setUp() {
        base = new Reference("A0");
        other = new Reference("A1");
        same = new Reference("A0");
    }

    /**
     * Assert that the dependencies of a reference is the set containing itself.
     */
    @Test
    @Deprecated
    public void testDependencies() {
        assertEquals(new HashSet<>(Collections.singleton("A0")), base.dependencies());
        assertEquals(new HashSet<>(Collections.singleton("A1")), other.dependencies());
        assertEquals(new HashSet<>(Collections.singleton("A0")), same.dependencies());
    }

    @Test
    public void testEquals() {
        assertNotEquals(other, base);
        assertEquals(same, base);
        assertNotEquals(other, same);
    }

    /**
     * Assert that the result of `getIdentifier` is the identifier stored by a reference.
     */
    @Test
    @Deprecated
    public void testGetIdentifier() {
        assertEquals("A0", base.getIdentifier());
        assertEquals("A1", other.getIdentifier());
        assertEquals("A0", same.getIdentifier());
    }

    @Test
    public void testHashCode() {
        assertEquals(base.hashCode(), same.hashCode());
    }

    @Test
    public void testIsReference() {
        assertTrue(base.isReference());
        assertTrue(other.isReference());
        assertTrue(same.isReference());
    }

    @Test
    public void testRender() {
        assertEquals("A0", base.render());
        assertEquals("A1", other.render());
        assertEquals("A0", same.render());
    }

    /**
     * Assert that the reference has an appropriate `toString()` result.
     */
    @Test
    @Deprecated
    public void testToString() {
        assertEquals("REFERENCE(A0)", base.toString());
        assertEquals("REFERENCE(A1)", other.toString());
        assertEquals("REFERENCE(A0)", same.toString());
    }

    @Test(expected = TypeError.class)
    public void testValue() throws TypeError {
        base.value();
    }

    @Test(expected = TypeError.class)
    public void testValue2() throws TypeError {
        other.value();
    }

    @Test
    public void testValueStateNoValue() throws TypeError {
        assertEquals(base, base.value(new HashMap<>()));
        assertEquals(other, other.value(new HashMap<>()));
        assertEquals(same, same.value(new HashMap<>()));
        assertEquals(base, same.value(new HashMap<>()));
    }

    @Test
    public void testValueState() throws TypeError {
        Map<String, Expression> state = new HashMap<>();
        Nothing nothing = new Nothing();
        state.put("A0", new Reference("A2"));
        state.put("A1", nothing);
        state.put("A2", nothing);
        assertEquals(nothing, base.value(state));
        assertEquals(nothing, other.value(state));
        assertEquals(nothing, same.value(state));
        assertEquals(nothing, same.value(state));
    }
}
