package sheep.features.files;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.UI;
import sheep.ui.Prompt;
import sheep.ui.graphical.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileLoading implements Feature {

    private final Sheet sheet;


    public FileLoading(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void register(UI ui) {
        ui.addFeature("load-file", "Load File", (row, col, prompt) -> loadFile(ui));
    }

    private void loadFile(UI ui) {
        MessagePrompt prompt = new MessagePrompt();
        try {
            //JFileChooser fileChooser = new JFileChooser();

            String path = String.valueOf(prompt.ask("Please enter file name."));

            //int result = fileChooser.showOpenDialog(null);

            // if (result == JFileChooser.APPROVE_OPTION) {
            //    File fileToLoad = fileChooser.getSelectedFile();

                File fileToLoad = new File(path);

                if (!fileToLoad.exists()) {
                    prompt.message("Error: File not found.");
                    //JOptionPane.showMessageDialog(null, "Error: File not found.");
                    return;
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                    // Read dimensions from the file
                    String dimensionsLine = reader.readLine();
                    String[] dimensions = dimensionsLine.split(",");
                    int loadedRows = Integer.parseInt(dimensions[0]);
                    int loadedColumns = Integer.parseInt(dimensions[1]);

                    // Load data from CSV
                    sheet.clear(); // Clear existing data
                    sheet.loadFromCSV(reader.lines().collect(Collectors.joining("\n")));

                    // Update dimensions of the sheet
                    sheet.updateDimensions(loadedRows, loadedColumns);

                    prompt.message("File loaded successfully.");
                    // JOptionPane.showMessageDialog(null, );
                } catch (IOException e) {
                    e.printStackTrace();
                    prompt.message("Error loading file.");
                    //JOptionPane.showMessageDialog(null, "Error loading file: " + e.getMessage());
                }
         //   }
        } catch (HeadlessException e) {
            // Handle headless environment
            System.err.println("Error: Headless environment detected. Unable to display file chooser dialog.");
            System.err.println("Please run the application in a graphical environment to load files.");
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
}
