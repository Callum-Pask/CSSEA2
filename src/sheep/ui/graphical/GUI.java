package sheep.ui.graphical;

import sheep.core.SheetUpdate;
import sheep.core.SheetView;
import sheep.core.ViewElement;
import sheep.ui.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;

/**
 * Graphical interface for the spreadsheet program.
 * <p>
 * Requires a {@link SheetView} and {@link SheetUpdate} to determine
 * what to render and how to update the sheet respectively.
 */
public class GUI extends UI {

    /**
     * Construct a new graphical interface.
     *
     * @param view The viewer that determines the size of the
     *             spreadsheet and the formula and values to display.
     * @param updater A model used to update the spreadsheet whenever
     *                the user makes a change.
     */
    public GUI(SheetView view, SheetUpdate updater) {
        super(view, updater);
    }

    /**
     * The render method will trigger the user interface to be displayed.
     * The interface uses Java Swing and will open a new window.
     */
    public void render() {
        // Use the native file menu on MacOS
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // frame is the whole window that is displayed.
        JFrame frame = new JFrame();
        frame.setTitle("SheeP (Sheet Processing)");

        // JTable operates on a model which tells it what to render
        // and what to do when a cell is updated.
        // We use a small hack of storing Location instances
        // in the table model so that both the value is rendered
        // but the formula is retrieved when editing.
        SheetModel model = new SheetModel(frame, view, updater);
        JTable table = new JTable(model);
        style(table);

        table.addKeyListener(new Shortcuts(table));

        Prompt prompt = new MessagePrompt();
        new Timer(tickSpeed, (e) -> {
            for (Tick tick : tickCallbacks) {
                if (tick.onTick(prompt)) {
                    refreshUI(table);
                }
            }
        }).start();

        // When the model is updated, re-render the table and
        // call any change callbacks (notably, saving).
        model.addTableModelListener((e -> refreshUI(table)));
        for (OnChange callback : changeCallbacks) {
            model.addTableModelListener(e -> callback.change(prompt));
        }
        setupMenu(frame, table);

        // Configure the custom cell renderer and editor for each column.
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            configureColumn(column);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(formulaView(table));
        panel.add(table);

        frame.add(panel);

        frame.pack();
        frame.setVisible(true);
    }

    private TableColumn configureColumn(TableColumn column) {
        column.setCellRenderer(new CellRenderer(view));
        column.setCellEditor(new CellEditor(view, new JTextField()));
        return column;
    }

    private void refreshUI(JTable table) {
        int size = table.getColumnCount() - 1;
        if (size > view.getColumns()) {
            for (int column = 0; column < size - view.getColumns(); column++) {
                int columnIndex = size - column;
                table.removeColumn(table.getColumn(table.getModel().getColumnName(columnIndex)));
            }
        } else if (size < view.getColumns()) {
            for (int column = size; column < view.getColumns(); column++) {
                table.addColumn(configureColumn(new TableColumn(column + 1)));
            }
        }
        table.updateUI();
    }

    private class Shortcuts implements KeyListener {
        private final JTable table;
        private final MessagePrompt prompt = new MessagePrompt();

        public Shortcuts(JTable table) {
            this.table = table;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            String keyId = "" + e.getKeyChar();
            if (!keys.containsKey(keyId)) {
                return;
            }
            for (Feature action : keys.get(keyId)) {
                action.action()
                        .perform(table.getSelectedRow() - 1, table.getSelectedColumn() - 1, prompt);
            }
            refreshUI(table);
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private static class MessagePrompt implements Prompt {

        @Override
        public Optional<String> ask(String prompt) {
            Optional<String[]> answer = askMany(new String[]{prompt});
            return answer.map(strings -> strings[0]);
        }

        @Override
        public Optional<String[]> askMany(String[] prompts) {
            JPanel panel = new JPanel(new GridLayout(0, 1));

            JTextField[] promptFields = new JTextField[prompts.length];
            for (int i = 0; i < prompts.length; i++) {
                promptFields[i] = new JTextField(15);
                panel.add(new JLabel(prompts[i]));
                panel.add(promptFields[i]);
                panel.add(Box.createHorizontalStrut(15));
            }

            int result = JOptionPane.showConfirmDialog(null, panel,
                    "Prompt", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            String[] answers = new String[prompts.length];
            for (int i = 0; i < prompts.length; i++) {
                answers[i] = promptFields[i].getText();
            }
            return Optional.of(answers);
        }

        @Override
        public boolean askYesNo(String prompt) {
            int result = JOptionPane.showConfirmDialog(null, prompt,
                    "Prompt", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            return result == JOptionPane.OK_OPTION;
        }

        @Override
        public void message(String prompt) {
            JOptionPane.showMessageDialog(null, prompt,
                    "Prompt", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Create an item in the menu to trigger each feature.
     */
    private void setupMenu(JFrame frame, JTable table) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Features");
        Prompt prompt = new MessagePrompt();

        for (Feature feature : features.values()) {
            JMenuItem item = new JMenuItem(feature.name());
            item.addActionListener(e -> {
                feature.action().perform(table.getSelectedRow() - 1, table.getSelectedColumn() - 1, prompt);
                refreshUI(table);
            });
            menu.add(item);
        }
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }

    /**
     * Style the table as per the {@link Configuration} class.
     */
    private void style(JTable table) {
        table.setGridColor(Configuration.LINE_COLOR);
        table.setRowHeight(Configuration.ROW_HEIGHT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(Configuration.HEADER_COLUMN_WIDTH);
        for (int column = 1; column < view.getColumns(); column++) {
            columnModel.getColumn(column).setPreferredWidth(Configuration.COLUMN_WIDTH);
        }
    }

    /**
     * Create a new component that is linked to the value of the currently highlighted cell.
     * When the highlighted cell changes, the formula is updated.
     */
    private Component formulaView(JTable table) {
        TextField formulaView = new TextField();
        formulaView.setEnabled(false);

        // Callback for whenever the highlighted cell changes.
        ListSelectionListener updateFormula = e -> {
            // Clear the view when selecting headers.
            if (table.getSelectedColumn() <= 0 || table.getSelectedRow() <= 0) {
                formulaView.setText("");
                return;
            }

            ViewElement element = view.formulaAt(
                    table.getSelectedRow() - 1,
                    table.getSelectedColumn() - 1
            );

            formulaView.setText(element.getContent());
            formulaView.setForeground(CellRenderer.getColorByName(element.getForeground()));
            formulaView.setBackground(CellRenderer.getColorByName(element.getBackground()));
        };

        // Add the callback when both the column changes and the row changes.
        table.getSelectionModel().addListSelectionListener(updateFormula);
        table.getColumnModel().getSelectionModel().addListSelectionListener(updateFormula);

        return formulaView;
    }
}
