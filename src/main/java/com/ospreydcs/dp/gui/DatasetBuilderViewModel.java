package com.ospreydcs.dp.gui;

import com.ospreydcs.dp.gui.model.DataBlockDetail;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ViewModel for the Dataset Builder tab functionality.
 * Manages dataset properties, data blocks, and UI state.
 */
public class DatasetBuilderViewModel {

    private static final Logger logger = LogManager.getLogger();

    // Dataset properties
    private final StringProperty datasetName = new SimpleStringProperty("");
    private final StringProperty datasetDescription = new SimpleStringProperty("");
    
    // Data blocks
    private final ObservableList<DataBlockDetail> dataBlocks = FXCollections.observableArrayList();
    
    // UI state properties
    private final BooleanProperty hasDataBlocks = new SimpleBooleanProperty(false);
    private final BooleanProperty isDatasetValid = new SimpleBooleanProperty(false);
    private final StringProperty statusMessage = new SimpleStringProperty("Ready to build dataset");
    
    // Button state properties
    private final BooleanProperty resetButtonEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty saveButtonEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty datasetActionsEnabled = new SimpleBooleanProperty(false);

    public DatasetBuilderViewModel() {
        logger.debug("DatasetBuilderViewModel initialized");
        
        // Set up listeners to update button states and validation
        setupPropertyListeners();
    }
    
    private void setupPropertyListeners() {
        // Update hasDataBlocks property when dataBlocks list changes
        dataBlocks.addListener((javafx.collections.ListChangeListener<DataBlockDetail>) change -> {
            boolean hasBlocks = !dataBlocks.isEmpty();
            hasDataBlocks.set(hasBlocks);
            updateDatasetValidation();
            updateButtonStates();
            logger.debug("Data blocks list changed. Count: {}", dataBlocks.size());
        });
        
        // Update validation when dataset name changes
        datasetName.addListener((obs, oldVal, newVal) -> {
            updateDatasetValidation();
            updateButtonStates();
        });
        
        // Update button states when description changes
        datasetDescription.addListener((obs, oldVal, newVal) -> {
            updateButtonStates();
        });
    }
    
    private void updateDatasetValidation() {
        boolean nameValid = datasetName.get() != null && !datasetName.get().trim().isEmpty();
        boolean blocksValid = !dataBlocks.isEmpty();
        boolean isValid = nameValid && blocksValid;
        
        isDatasetValid.set(isValid);
        
        if (!nameValid && !blocksValid) {
            statusMessage.set("Dataset name and data blocks are required");
        } else if (!nameValid) {
            statusMessage.set("Dataset name is required");
        } else if (!blocksValid) {
            statusMessage.set("Data blocks are required");
        } else {
            statusMessage.set("Ready to build dataset");
        }
    }
    
    private void updateButtonStates() {
        boolean hasName = datasetName.get() != null && !datasetName.get().trim().isEmpty();
        boolean hasBlocks = !dataBlocks.isEmpty();
        boolean hasContent = hasName || !datasetDescription.get().trim().isEmpty() || hasBlocks;
        
        // Enable buttons based on content and validation state
        resetButtonEnabled.set(hasContent);
        saveButtonEnabled.set(hasName && hasBlocks); // Both name and blocks required for save
        // Dataset actions combo will be enabled in future implementation
        datasetActionsEnabled.set(false);
        
        logger.debug("Button states updated - Reset: {}, Save: {}, Actions: {}", 
                    resetButtonEnabled.get(), saveButtonEnabled.get(), datasetActionsEnabled.get());
    }
    
    // Public methods for business logic
    
    public void addDataBlock(DataBlockDetail dataBlock) {
        if (dataBlock != null && !dataBlocks.contains(dataBlock)) {
            dataBlocks.add(dataBlock);
            logger.debug("Added data block: {}", dataBlock);
        }
    }
    
    public void removeDataBlock(DataBlockDetail dataBlock) {
        if (dataBlocks.remove(dataBlock)) {
            logger.debug("Removed data block: {}", dataBlock);
        }
    }
    
    public void clearDataBlocks() {
        int count = dataBlocks.size();
        dataBlocks.clear();
        logger.debug("Cleared {} data blocks", count);
    }
    
    public void resetDataset() {
        datasetName.set("");
        datasetDescription.set("");
        clearDataBlocks();
        statusMessage.set("Dataset reset");
        logger.debug("Dataset reset completed");
    }
    
    // Property getters for data binding
    
    public StringProperty datasetNameProperty() { return datasetName; }
    public String getDatasetName() { return datasetName.get(); }
    public void setDatasetName(String name) { datasetName.set(name != null ? name : ""); }
    
    public StringProperty datasetDescriptionProperty() { return datasetDescription; }
    public String getDatasetDescription() { return datasetDescription.get(); }
    public void setDatasetDescription(String description) { datasetDescription.set(description != null ? description : ""); }
    
    public ObservableList<DataBlockDetail> getDataBlocks() { return dataBlocks; }
    
    public BooleanProperty hasDataBlocksProperty() { return hasDataBlocks; }
    public boolean hasDataBlocks() { return hasDataBlocks.get(); }
    
    public BooleanProperty isDatasetValidProperty() { return isDatasetValid; }
    public boolean isDatasetValid() { return isDatasetValid.get(); }
    
    public StringProperty statusMessageProperty() { return statusMessage; }
    public String getStatusMessage() { return statusMessage.get(); }
    
    public BooleanProperty resetButtonEnabledProperty() { return resetButtonEnabled; }
    public BooleanProperty saveButtonEnabledProperty() { return saveButtonEnabled; }
    public BooleanProperty datasetActionsEnabledProperty() { return datasetActionsEnabled; }
}