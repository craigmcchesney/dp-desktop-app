package com.ospreydcs.dp.gui.component;

import com.ospreydcs.dp.grpc.v1.common.DataColumn;
import com.ospreydcs.dp.grpc.v1.common.DataValue;
import com.ospreydcs.dp.grpc.v1.common.Timestamp;
import com.ospreydcs.dp.gui.model.DataFrameDetails;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the reusable Calculation Frame Details Dialog component.
 * This dialog displays detailed information about a DataFrameDetails object including
 * timestamps and data columns with sample values.
 */
public class CalculationFrameDetailsDialogController implements Initializable {
    
    private static final Logger logger = LogManager.getLogger();
    
    @FXML private Label frameNameLabel;
    @FXML private TextArea contentArea;
    
    private DataFrameDetails currentFrame;
    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("CalculationFrameDetailsDialogController initialized");
        
        // Set up content area properties
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
    }
    
    /**
     * Sets the DataFrameDetails to display in this dialog.
     * This should be called before showing the dialog.
     */
    public void setDataFrame(DataFrameDetails frame) {
        this.currentFrame = frame;
        if (frame != null) {
            updateContent();
        }
    }
    
    /**
     * Sets the dialog stage reference for closing operations.
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    
    /**
     * Updates the dialog content based on the current DataFrameDetails.
     */
    private void updateContent() {
        if (currentFrame == null) {
            frameNameLabel.setText("No frame selected");
            contentArea.setText("No calculation frame data available.");
            return;
        }
        
        // Set frame name in header
        frameNameLabel.setText("Calculation Frame: " + currentFrame.getName());
        
        // Build detailed content
        StringBuilder content = new StringBuilder();
        content.append("Frame Name: ").append(currentFrame.getName()).append("\n\n");
        
        // Add timestamp information
        if (currentFrame.getTimestamps() != null && !currentFrame.getTimestamps().isEmpty()) {
            content.append("Timestamps: ").append(currentFrame.getTimestamps().size()).append(" entries\n");
            content.append("First timestamp: ").append(formatTimestamp(currentFrame.getTimestamps().get(0))).append("\n");
            if (currentFrame.getTimestamps().size() > 1) {
                content.append("Last timestamp: ").append(formatTimestamp(currentFrame.getTimestamps().get(currentFrame.getTimestamps().size() - 1))).append("\n");
            }
        } else {
            content.append("Timestamps: None\n");
        }
        content.append("\n");
        
        // Add column information
        if (currentFrame.getDataColumns() != null && !currentFrame.getDataColumns().isEmpty()) {
            content.append("Data Columns (").append(currentFrame.getDataColumns().size()).append("):\n");
            for (int i = 0; i < currentFrame.getDataColumns().size(); i++) {
                DataColumn column = currentFrame.getDataColumns().get(i);
                content.append(String.format("  %d. %s (%d values)\n", 
                    i + 1, 
                    column.getName(), 
                    column.getDataValuesCount()));
                
                // Show first few values as sample
                if (column.getDataValuesCount() > 0) {
                    content.append("     Sample values: ");
                    int sampleCount = Math.min(3, column.getDataValuesCount());
                    for (int j = 0; j < sampleCount; j++) {
                        if (j > 0) content.append(", ");
                        content.append(formatDataValue(column.getDataValues(j)));
                    }
                    if (column.getDataValuesCount() > 3) {
                        content.append("...");
                    }
                    content.append("\n");
                }
            }
        } else {
            content.append("Data Columns: None\n");
        }
        
        contentArea.setText(content.toString());
        logger.debug("Updated dialog content for frame: {}", currentFrame.getName());
    }
    
    /**
     * Formats a timestamp for display.
     */
    private String formatTimestamp(Timestamp timestamp) {
        try {
            java.time.Instant instant = java.time.Instant.ofEpochSecond(
                timestamp.getEpochSeconds(), 
                timestamp.getNanoseconds()
            );
            return instant.toString();
        } catch (Exception e) {
            logger.warn("Error formatting timestamp: {}", e.getMessage());
            return "Invalid timestamp";
        }
    }
    
    /**
     * Formats a DataValue for display.
     */
    private String formatDataValue(DataValue dataValue) {
        try {
            switch (dataValue.getValueCase()) {
                case STRINGVALUE:
                    return "\"" + dataValue.getStringValue() + "\"";
                case DOUBLEVALUE:
                    return String.format("%.3f", dataValue.getDoubleValue());
                case BOOLEANVALUE:
                    return String.valueOf(dataValue.getBooleanValue());
                case INTVALUE:
                    return String.valueOf(dataValue.getIntValue());
                case ULONGVALUE:
                    return String.valueOf(dataValue.getUlongValue());
                case UINTVALUE:
                    return String.valueOf(dataValue.getUintValue());
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            logger.warn("Error formatting data value: {}", e.getMessage());
            return "Error";
        }
    }
    
    @FXML
    private void onClose() {
        logger.debug("Close button clicked in calculation frame details dialog");
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    
    /**
     * Static factory method to create and show a calculation frame details dialog.
     * This provides a convenient way to show the dialog without manually creating the stage.
     * 
     * @param frame The DataFrameDetails to display
     * @param ownerStage The parent stage (can be null)
     */
    public static void showDialog(DataFrameDetails frame, Stage ownerStage) {
        try {
            logger.info("Showing calculation frame details dialog for: {}", 
                       frame != null ? frame.getName() : "null");
            
            // Create dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Calculation Frame Details");
            dialog.setResizable(true);
            
            // Load FXML content
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                CalculationFrameDetailsDialogController.class.getResource("/fxml/components/calculation-frame-details-dialog.fxml")
            );
            
            javafx.scene.layout.VBox content = loader.load();
            CalculationFrameDetailsDialogController controller = loader.getController();
            
            // Configure controller
            controller.setDataFrame(frame);
            
            // Set up dialog
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            // Set owner if provided
            if (ownerStage != null) {
                dialog.initOwner(ownerStage);
            }
            
            // Show dialog
            dialog.showAndWait();
            
            logger.debug("Calculation frame details dialog closed");
            
        } catch (Exception e) {
            logger.error("Failed to show calculation frame details dialog", e);
            
            // Fallback: show simple alert
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to show calculation details");
            alert.setContentText("An error occurred while displaying the calculation frame details: " + e.getMessage());
            alert.showAndWait();
        }
    }
}