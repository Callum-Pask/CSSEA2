package sheep.sheets;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import sheep.core.UpdateResponse;
import sheep.core.ViewElement;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Reference;
import sheep.parsing.ParseException;
import sheep.parsing.Parser;

import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.*;


class FormulaExpr extends Expression {

    private final String id;

    public FormulaExpr(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return getID();
    }

    @Override
    public Set<String> dependencies() {
        return new HashSet<>();
    }

    @Override
    public long value() throws TypeError {
        return 0;
    }

    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        return new ValueExpr(id);
    }

    @Override
    public String render() {
        return "Formula(" + id + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FormulaExpr i) {
            return i.id.equals(this.id);
        }
        return false;
    }
}

class ValueExpr extends Expression {

    private final String id;

    public ValueExpr(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return getID();
    }

    @Override
    public Set<String> dependencies() {
        return new HashSet<>();
    }

    @Override
    public long value() throws TypeError {
        return 0;
    }

    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        return this;
    }

    @Override
    public String render() {
        return "Value(" + id + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValueExpr i) {
            return i.id.equals(this.id);
        }
        return false;
    }
}


class RefExpr extends Expression {

    private final String id;

    public RefExpr(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return getID();
    }

    @Override
    public Set<String> dependencies() {
        return Set.of(id);
    }

    @Override
    public long value() throws TypeError {
        return 0;
    }

    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        if (state.containsKey(id)) {
            return state.get(id).value(state);
        }
        return this;
    }

    @Override
    public String render() {
        return "Ref(" + id + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RefExpr i) {
            return i.id.equals(this.id);
        }
        return false;
    }
}

enum SpecialStrings {
    ThrowTypeError("Throw Type Error"),
    ThrowParseException("Throw Parse Exception");

    public final String label;

    private SpecialStrings(String label) {
        this.label = label;
    }
}

class EchoParser implements Parser {
    @Override
    public Expression parse(String input) throws ParseException {
        if (input.startsWith("->")) {
            return new RefExpr(input.substring(2));
        }
        if (input.equals(SpecialStrings.ThrowTypeError.label)) {
            return new FormulaExpr(SpecialStrings.ThrowTypeError.label){
                @Override
                public Expression value(Map<String, Expression> state) throws TypeError {
                    throw new TypeError();
                }
            };
        }
        if (input.equals(SpecialStrings.ThrowParseException.label)) {
            throw new ParseException();
        }
        return new FormulaExpr(input);
    }
}

public class SheetTest {
    public static final int testWeight = 10;

    @Rule
    public Timeout timeout = new Timeout(60000);

    private Sheet base;

    @Before
    public void setUp() {
        Parser parser = new EchoParser();
        base = new Sheet(parser, new HashMap<>(), new FormulaExpr("Default"), 5, 3);
    }

    /**
     * Asserts that a Sheet with 5 rows returns 5 for getRows.
     */
    @Test
    public void testRows() {
        assertEquals(5, base.getRows());
    }

    /**
     * Asserts that a Sheet with 5 columns returns 5 for getColumns.
     */
    @Test
    public void testColumns() {
        assertEquals(3, base.getColumns());
    }

    /**
     * Asserts that a Sheet with 3 rows and 6 columns has correct row and column information.
     */
    @Test
    public void testDimensions() {
        Sheet otherSheet = new Sheet(new EchoParser(), new HashMap<>(), new FormulaExpr("Default"), 3, 6);
        assertEquals(3, otherSheet.getRows());
        assertEquals(6, otherSheet.getColumns());
    }

    /**
     * Assert that in an empty Sheet,
     * calling formulaAt(CellLocation) will result in an expression equal to the default expression.
     */
    @Test
    public void testEmptyFormulas() {
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 3; column++) {
                assertEquals(new FormulaExpr("Default"), base.formulaAt(new CellLocation(row, column)));
            }
        }
    }

    /**
     * Assert that in an empty Sheet,
     * calling formulaAt(int, int) will result in a ViewElement with content
     * matching the render() of the default expression.
     */
    @Test
    public void testEmptyFormulasUI() {
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 3; column++) {
                ViewElement view = base.formulaAt(row, column);
                assertEquals("Formula(Default)", view.getContent());
            }
        }
    }

    @Test(expected = TypeError.class, timeout = 6000)
    public void testUpdateCellTypeError() throws TypeError {
        base.update(new CellLocation(1, 2),
                new FormulaExpr(SpecialStrings.ThrowTypeError.label){
                    @Override
                    public Expression value(Map<String, Expression> state) throws TypeError {
                        throw new TypeError();
                    }
                });
    }

    @Test(timeout = 6000)
    public void testUpdateCellSuccess() throws TypeError {
        base.update(new CellLocation(2, 2), new FormulaExpr("MyCellContent"));
        assertEquals(new FormulaExpr("MyCellContent"), base.formulaAt(new CellLocation(2, 2)));
        assertEquals(new ValueExpr("MyCellContent"), base.valueAt(new CellLocation(2, 2)));
    }

    @Test(timeout = 6000)
    public void testUpdateCellSuccessWithRender() throws TypeError {
        base.update(new CellLocation(2, 2), new FormulaExpr("MyCellContent"));
        assertEquals("Formula(MyCellContent)", base.formulaAt(2, 2).getContent());
        assertEquals("Value(MyCellContent)", base.valueAt(2, 2).getContent());
    }

    @Test(timeout = 6000)
    public void testUpdateCellReference() throws TypeError {
        base.update(new CellLocation(1, 0), new FormulaExpr("AtA1"));
        assertEquals(new FormulaExpr("AtA1"), base.formulaAt(new CellLocation(1, 0)));

        base.update(new CellLocation(2, 2), new RefExpr("A1"));
        assertEquals(new RefExpr("A1"), base.formulaAt(new CellLocation(2, 2)));
        assertEquals(new ValueExpr("AtA1"), base.valueAt(new CellLocation(2, 2)));
    }

    @Test(timeout = 6000)
    public void testUpdateCellReferenceWithRender() throws TypeError {
        base.update(new CellLocation(1, 0), new FormulaExpr("AtA1"));
        assertEquals("Formula(AtA1)", base.formulaAt(1, 0).getContent());

        base.update(new CellLocation(2, 2), new RefExpr("A1"));
        assertEquals("Ref(A1)", base.formulaAt(2, 2).getContent());
        assertEquals("Value(AtA1)", base.valueAt(2, 2).getContent());
    }

    private void establishChainCell() throws TypeError {
        base.update(new CellLocation(0, 2), new FormulaExpr("AtC0"));
        assertEquals("Formula(AtC0)", base.formulaAt(0, 2).getContent());

        base.update(new CellLocation(0, 1), new RefExpr("C0"));
        assertEquals("Ref(C0)", base.formulaAt(0, 1).getContent());
        assertEquals("Value(AtC0)", base.valueAt(0, 1).getContent());

        base.update(new CellLocation(0, 0), new RefExpr("B0"));
        assertEquals("Ref(B0)", base.formulaAt(0, 0).getContent());
        assertEquals("Value(AtC0)", base.valueAt(0, 0).getContent());
    }

    @Test(timeout = 6000)
    public void testInsertReferenceChainCell() throws TypeError {
        establishChainCell();
    }

    @Test(timeout = 6000)
    public void testUpdateReferenceChainCell() throws TypeError {
        establishChainCell();

        base.update(new CellLocation(0, 2), new FormulaExpr("NewValue"));

        assertEquals("Formula(NewValue)", base.formulaAt(0, 2).getContent());
        assertEquals("Ref(C0)", base.formulaAt(0, 1).getContent());
        assertEquals("Ref(B0)", base.formulaAt(0, 0).getContent());

        assertEquals("Value(NewValue)", base.valueAt(0, 2).getContent());
        assertEquals("Value(NewValue)", base.valueAt(0, 1).getContent());
        assertEquals("Value(NewValue)", base.valueAt(0, 0).getContent());
    }

    @Test(timeout = 6000)
    public void testUpdateTypeError() {
        UpdateResponse response = base.update(1, 2, SpecialStrings.ThrowTypeError.label);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().startsWith("Type error: sheep.expression.TypeError"));
    }

    @Test(timeout = 6000)
    public void testUpdateParseError() {
        UpdateResponse response = base.update(2, 1, SpecialStrings.ThrowParseException.label);
        assertFalse(response.isSuccess());
        assertEquals("Unable to parse: " + SpecialStrings.ThrowParseException.label, response.getMessage());
    }

    @Test(timeout = 6000)
    public void testUpdateSuccess() {
        UpdateResponse response = base.update(2, 2, "MyCellContent");
        assertTrue(response.isSuccess());
        assertEquals("Formula(MyCellContent)", base.formulaAt(2, 2).getContent());
        assertEquals("Value(MyCellContent)", base.valueAt(2, 2).getContent());
    }

    @Test(timeout = 6000)
    public void testUpdateReference() {
        UpdateResponse response = base.update(1, 0, "AtA1");
        assertTrue(response.isSuccess());
        assertEquals("Formula(AtA1)", base.formulaAt(1, 0).getContent());

        response = base.update(2, 2, "->A1");
        assertTrue(response.isSuccess());
        assertEquals("Ref(A1)", base.formulaAt(2, 2).getContent());
        assertEquals("Value(AtA1)", base.valueAt(2, 2).getContent());
    }

    private void establishChain() {
        UpdateResponse response = base.update(0, 2, "AtC0");
        assertTrue(response.isSuccess());
        assertEquals("Formula(AtC0)", base.formulaAt(0, 2).getContent());

        response = base.update(0, 1, "->C0");
        assertTrue(response.isSuccess());
        assertEquals("Ref(C0)", base.formulaAt(0, 1).getContent());
        assertEquals("Value(AtC0)", base.valueAt(0, 1).getContent());

        response = base.update(0, 0, "->B0");
        assertTrue(response.isSuccess());
        assertEquals("Ref(B0)", base.formulaAt(0, 0).getContent());
        assertEquals("Value(AtC0)", base.valueAt(0, 0).getContent());
    }

    @Test(timeout = 6000)
    public void testInsertReferenceChain() {
        establishChain();
    }

    @Test(timeout = 6000)
    public void testUpdateReferenceChain() {
        establishChain();

        UpdateResponse response = base.update(0, 2, "NewValue");
        assertTrue(response.isSuccess());

        assertEquals("Formula(NewValue)", base.formulaAt(0, 2).getContent());
        assertEquals("Ref(C0)", base.formulaAt(0, 1).getContent());
        assertEquals("Ref(B0)", base.formulaAt(0, 0).getContent());

        assertEquals("Value(NewValue)", base.valueAt(0, 2).getContent());
        assertEquals("Value(NewValue)", base.valueAt(0, 1).getContent());
        assertEquals("Value(NewValue)", base.valueAt(0, 0).getContent());
    }

    @Test(timeout = 6000)
    public void testResolveBuiltIn() throws TypeError {
        Parser parser = new EchoParser();
        Map<String, Expression> builtins = new HashMap<>();
        builtins.put("dood", new FormulaExpr("3490524077"));
        base = new Sheet(parser, builtins, new FormulaExpr("Default"), 5, 3);

        base.update(new CellLocation(1, 1), new RefExpr("dood"));
        assertEquals("Ref(dood)", base.formulaAt(1, 1).getContent());
        assertEquals("Value(3490524077)", base.valueAt(1, 1).getContent());
    }

    /**
     * Test that in an empty sheet,
     * all calls to usedBy for every cell location returns an empty set.
     */
    @Test
    public void testUsedByNone() {
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 3; column++) {
                Set<CellLocation> usages = base.usedBy(new CellLocation(row, column));
                assertTrue(usages.isEmpty());
            }
        }
    }

    /**
     * Test that after inserting a reference to A1 at A0,
     * calling usedBy for A1 will return a set containing A0.
     */
    @Test
    @Deprecated
    public void testUsedByOne() throws TypeError {
        base.update(new CellLocation(0, 0), new Reference("A1"));
        assertEquals(new HashSet<>(Collections.singleton(new CellLocation(0, 0))),
                base.usedBy(new CellLocation(1, 0)));
    }

    /**
     * Test that after inserting a reference to A2 at cells A0 and A1,
     * calling usedBy for A2 will return a set containing A0 and A1.
     */
    @Test
    @Deprecated
    public void testUsedByTwo() throws TypeError {
        base.update(new CellLocation(0, 0), new Reference("A2"));
        base.update(new CellLocation(1, 0), new Reference("A2"));
        assertEquals(new HashSet<>(List.of(new CellLocation(0, 0), new CellLocation(1, 0))),
                base.usedBy(new CellLocation(2, 0)));
    }

    /**
     * Test that after inserting a reference to B1 at cells A0, A1, A2, A3, and A4,
     * calling usedBy for B1 will return a set of {A0, A1, A2, A3, A4}.
     */
    @Test
    @Deprecated
    public void testUsedByN() throws TypeError {
        base.update(new CellLocation(0, 0), new Reference("B1"));
        base.update(new CellLocation(1, 0), new Reference("B1"));
        base.update(new CellLocation(2, 0), new Reference("B1"));
        base.update(new CellLocation(3, 0), new Reference("B1"));
        base.update(new CellLocation(4, 0), new Reference("B1"));
        assertEquals(new HashSet<>(List.of(
                        new CellLocation(0, 0),
                        new CellLocation(1, 0),
                        new CellLocation(2, 0),
                        new CellLocation(3, 0),
                        new CellLocation(4, 0)
                )),
                base.usedBy(new CellLocation(1, 1)));
    }

    /**
     * Inserts a reference to A3 at A1 and A2.
     * Inserts a reference to A4 at A3.
     * Asserts that calling usedBy for A3 will return a set containing A1 and A2.
     * Asserts that calling usedBy for A4 will return the set {A1, A2, A3}.
     */
    @Test
    @Deprecated
    public void testUsedByTransitive() throws TypeError {
        base.update(new CellLocation(1, 0), new Reference("A3"));
        base.update(new CellLocation(2, 0), new Reference("A3"));
        base.update(new CellLocation(3, 0), new Reference("A4"));
        assertEquals(new HashSet<>(List.of(
                        new CellLocation(1, 0),
                        new CellLocation(2, 0)
                )),
                base.usedBy(new CellLocation(3, 0)));
        assertEquals(new HashSet<>(List.of(
                        new CellLocation(1, 0),
                        new CellLocation(2, 0),
                        new CellLocation(3, 0)
                )),
                base.usedBy(new CellLocation(4, 0)));
    }
}
