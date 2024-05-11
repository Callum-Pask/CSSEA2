package sheep.sheets;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import sheep.expression.TypeError;
import sheep.parsing.Parser;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class SheetBuilderTest {
    public static final double testWeight = 2;

    @Rule
    public Timeout timeout = new Timeout(60000);

    private SheetBuilder base;

    @Before
    public void setUp() {
        Parser parser = new EchoParser();
        base = new SheetBuilder(parser, new FormulaExpr("Default"));
    }

    @Test
    public void testEmpty() {
        Sheet sheet = base.empty(5, 3);
        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 3; column++) {
                assertEquals(new FormulaExpr("Default"), sheet.formulaAt(new CellLocation(row, column)));
            }
        }
    }

    @Test
    public void testAddBuiltin() throws TypeError {
        base = base.includeBuiltIn("dood", new FormulaExpr("3490524077"));
        Sheet sheet = base.empty(5, 3);

        sheet.update(new CellLocation(1, 1), new RefExpr("dood"));
        assertEquals("Ref(dood)", sheet.formulaAt(1, 1).getContent());
        assertEquals("Value(3490524077)", sheet.valueAt(1, 1).getContent());
    }
}
