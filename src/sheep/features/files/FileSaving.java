package sheep.features.files;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.UI;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.HeadlessException; // Import HeadlessException

public class FileSaving implements Feature {

    private final Sheet sheet;
    private String errorMessage; // Field to store error message

    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void register(UI ui) {
        ui.addFeature("save-file", "Save File", (row, col, prompt) -> saveFile(ui));
    }

    private void saveFile(UI ui) {
        try {
            JFileChooser fileChooser = new JFileChooser();

            int result = fileChooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Append ".csv" extension to the chosen file name
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    fileToSave = new File(filePath + ".csv");
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                    // Write the sheet dimensions to the file
                    writer.write(sheet.getRows() + "," + sheet.getColumns() + "\n");

                    // Write the spreadsheet data to the file in CSV format
                    writer.write(sheet.toCSV());

                    JOptionPane.showMessageDialog(null, "File saved successfully to: " + fileToSave.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error saving file: " + e.getMessage());
                }
            }
        } catch (HeadlessException e) {
            // Handle headless environment
            System.err.println("Error: Headless environment detected. Unable to display file save dialog.");
            System.err.println("Please run the application in a graphical environment to save files.");
        }
    }
}
