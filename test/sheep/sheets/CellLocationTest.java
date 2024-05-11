package sheep.sheets;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class CellLocationTest {
    public static final int testWeight = 6;

    /**
     * Attempt to construct a CellLocation with row=0 and column=0.
     * Assert appropriate getRow and getColumn values.
     */
    @Test
    public void testNumeric0_0Construction() {
        CellLocation location = new CellLocation(0, 0);
        assertEquals(0, location.getRow());
        assertEquals(0, location.getColumn());
    }

    /**
     * Attempt to construct a CellLocation with row=10 and column=0.
     * Assert appropriate getRow and getColumn values.
     */
    @Test
    @Deprecated
    public void testNumeric10_0Construction() {
        CellLocation location = new CellLocation(10, 0);
        assertEquals(10, location.getRow());
        assertEquals(0, location.getColumn());
    }

    /**
     * Attempt to construct a CellLocation with row=10 and column=24.
     * Assert appropriate getRow and getColumn values.
     */
    @Test
    public void testNumeric10_24Construction() {
        CellLocation location = new CellLocation(10, 24);
        assertEquals(10, location.getRow());
        assertEquals(24, location.getColumn());
    }

    /**
     * Attempt to construct a CellLocation with row=0 and column='A'.
     * Assert appropriate getRow and getColumn values (0, 0).
     */
    @Test
    public void testChar0_0Construction() {
        CellLocation location = new CellLocation(0, 'A');
        assertEquals(0, location.getRow());
        assertEquals(0, location.getColumn());
    }

    /**
     * Attempt to construct a CellLocation with row=10 and column='A'.
     * Assert appropriate getRow and getColumn values (10, 0).
     */
    @Test
    @Deprecated
    public void testChar10_0Construction() {
        CellLocation location = new CellLocation(10, 'A');
        assertEquals(10, location.getRow());
        assertEquals(0, location.getColumn());
    }

    /**
     * Attempt to construct a CellLocation with row=10 and column='Y'.
     * Assert appropriate getRow and getColumn values (10, 24).
     */
    @Test
    public void testChar10_24Construction() {
        CellLocation location = new CellLocation(10, 'Y');
        assertEquals(10, location.getRow());
        assertEquals(24, location.getColumn());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "A0".
     * Assert that a CellLocation is created and has appropriate getRow() and
     * getColumn() values.
     */
    @Test
    public void testMaybeReferenceA0() {
        Optional<CellLocation> location = CellLocation.maybeReference("A0");
        assertTrue(location.isPresent());
        assertEquals(0, location.get().getRow());
        assertEquals(0, location.get().getColumn());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "A11".
     * Assert that a CellLocation is created and has appropriate getRow() and
     * getColumn() values.
     */
    @Test
    public void testMaybeReferenceA5() {
        Optional<CellLocation> location = CellLocation.maybeReference("A11");
        assertTrue(location.isPresent());
        assertEquals(11, location.get().getRow());
        assertEquals(0, location.get().getColumn());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "F12".
     * Assert that a CellLocation is created and has appropriate getRow() and
     * getColumn() values (12, 5).
     */
    @Test
    @Deprecated
    public void testMaybeReferenceF12() {
        Optional<CellLocation> location = CellLocation.maybeReference("F12");
        assertTrue(location.isPresent());
        assertEquals(12, location.get().getRow());
        assertEquals(5, location.get().getColumn());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "12F".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReference12F() {
        Optional<CellLocation> location = CellLocation.maybeReference("12F");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "B".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReferenceB() {
        Optional<CellLocation> location = CellLocation.maybeReference("B");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "14".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReference14() {
        Optional<CellLocation> location = CellLocation.maybeReference("14");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReferenceEmpty() {
        Optional<CellLocation> location = CellLocation.maybeReference("");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of " ".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReferenceSpace() {
        Optional<CellLocation> location = CellLocation.maybeReference(" ");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "MM".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReferenceMM() {
        Optional<CellLocation> location = CellLocation.maybeReference("MM");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "B-12".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReferenceBdash12() {
        Optional<CellLocation> location = CellLocation.maybeReference("B-12");
        assertFalse(location.isPresent());
    }

    /**
     * Attempt to construct a CellLocation using maybeReference of "????".
     * Assert that a CellLocation cannot be created.
     */
    @Test
    public void testMaybeReferenceQMarks() {
        Optional<CellLocation> location = CellLocation.maybeReference("????");
        assertFalse(location.isPresent());
    }

    /**
     * Assert that two cells at 'A0' are equal.
     */
    @Test
    public void testEqualsA0() {
        CellLocation first = new CellLocation(0, 'A');
        CellLocation second = new CellLocation(0, 'A');
        assertEquals(first, second);
    }

    /**
     * Assert that two cells at 'B1' are equal.
     * One constructed with column=1, one constructed with column='B'.
     */
    @Test
    public void testEqualsB12() {
        CellLocation first = new CellLocation(12, 'B');
        CellLocation second = new CellLocation(12, 1);
        assertEquals(first, second);
    }

    /**
     * Assert that two cells at 'Z12' are equal.
     * One constructed with column=25, one constructed with column='Z'.
     */
    @Test
    public void testEqualsZ12() {
        CellLocation first = new CellLocation(12, 'Z');
        CellLocation second = new CellLocation(12, 25);
        assertEquals(first, second);
    }

    /**
     * Assert that two 'A0' cells have an equal hashCode.
     */
    @Test
    @Deprecated
    public void testHashCodeA0() {
        CellLocation first = new CellLocation(0, 'A');
        CellLocation second = new CellLocation(0, 'A');
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Assert that two 'B1' cells have an equal hashCode.
     * One constructed with column=1, one constructed with column='B'.
     */
    @Test
    public void testHashCodeB12() {
        CellLocation first = new CellLocation(12, 'B');
        CellLocation second = new CellLocation(12, 1);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Assert that two 'Z25' cells have an equal hashCode.
     * One constructed with column=25, one constructed with column='Z'.
     */
    @Test
    public void testHashCodeZ25() {
        CellLocation first = new CellLocation(12, 'Z');
        CellLocation second = new CellLocation(12, 25);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Assert that the toString of an A0 cell equals "A0".
     */
    @Test
    public void testToStringA0() {
        CellLocation cell = new CellLocation(0, 'A');
        assertEquals("A0", cell.toString());
    }

    /**
     * Assert that the toString of an Z100 cell equals "Z100".
     */
    @Test
    public void testToStringZ100() {
        CellLocation cell = new CellLocation(100, 'Z');
        assertEquals("Z100", cell.toString());
    }

    /**
     * Assert that the toString of an F10 cell equals "F10".
     */
    @Test
    public void testToStringF10() {
        CellLocation cell = new CellLocation(10, 5);
        assertEquals("F10", cell.toString());
    }

}
