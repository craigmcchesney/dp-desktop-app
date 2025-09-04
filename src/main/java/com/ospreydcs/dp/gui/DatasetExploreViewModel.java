package com.ospreydcs.dp.gui;

import com.ospreydcs.dp.client.result.QueryDataSetsApiResult;
import com.ospreydcs.dp.grpc.v1.annotation.DataSet;
import com.ospreydcs.dp.gui.model.DatasetInfoTableRow;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DatasetExploreViewModel {

    private static final Logger logger = LogManager.getLogger();

    // Query form properties
    private final StringProperty datasetId = new SimpleStringProperty("");
    private final StringProperty owner = new SimpleStringProperty("");
    private final StringProperty nameDescription = new SimpleStringProperty("");
    private final StringProperty pvName = new SimpleStringProperty("");
    
    // Results properties
    private final ObservableList<DatasetInfoTableRow> datasetResults = FXCollections.observableArrayList();
    private final IntegerProperty resultCount = new SimpleIntegerProperty(0);
    private final BooleanProperty isSearching = new SimpleBooleanProperty(false);
    
    // Status properties
    private final StringProperty statusMessage = new SimpleStringProperty("Ready to search for datasets");
    private final StringProperty searchStatusMessage = new SimpleStringProperty("");
    
    // Dependencies
    private DpApplication dpApplication;
    private MainController mainController;

    public DatasetExploreViewModel() {
        logger.debug("DatasetExploreViewModel initialized");
    }
    
    // Dependency injection methods
    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        logger.debug("DpApplication injected into DatasetExploreViewModel");
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        logger.debug("MainController injected into DatasetExploreViewModel");
    }

    // Property getters for form fields
    public StringProperty datasetIdProperty() { return datasetId; }
    public StringProperty ownerProperty() { return owner; }
    public StringProperty nameDescriptionProperty() { return nameDescription; }
    public StringProperty pvNameProperty() { return pvName; }
    
    // Property getters for results
    public ObservableList<DatasetInfoTableRow> getDatasetResults() { return datasetResults; }
    public IntegerProperty resultCountProperty() { return resultCount; }
    public BooleanProperty isSearchingProperty() { return isSearching; }
    
    // Property getters for status
    public StringProperty statusMessageProperty() { return statusMessage; }
    public StringProperty searchStatusMessageProperty() { return searchStatusMessage; }

    /**
     * Execute dataset search with current form parameters
     */
    public void executeSearch() {
        if (dpApplication == null) {
            searchStatusMessage.set("DpApplication not initialized");
            return;
        }

        isSearching.set(true);
        searchStatusMessage.set("Searching datasets...");
        datasetResults.clear();
        resultCount.set(0);

        // Create background task for search
        Task<Void> searchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                executeDatasetSearch();
                return null;
            }
        };

        searchTask.setOnSucceeded(e -> {
            isSearching.set(false);
            searchStatusMessage.set("Search completed");
            logger.info("Dataset search completed with {} results", resultCount.get());
        });

        searchTask.setOnFailed(e -> {
            logger.error("Dataset search failed", searchTask.getException());
            searchStatusMessage.set("Search failed: " + searchTask.getException().getMessage());
            isSearching.set(false);
        });

        Thread searchThread = new Thread(searchTask);
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void executeDatasetSearch() throws Exception {
        // Get form values (convert empty strings to null for API)
        String datasetIdParam = datasetId.get().trim().isEmpty() ? null : datasetId.get().trim();
        String ownerParam = owner.get().trim().isEmpty() ? null : owner.get().trim();
        String nameDescParam = nameDescription.get().trim().isEmpty() ? null : nameDescription.get().trim();
        String pvNameParam = pvName.get().trim().isEmpty() ? null : pvName.get().trim();
        
        logger.debug("Searching datasets with parameters: datasetId={}, owner={}, nameDescription={}, pvName={}", 
            datasetIdParam, ownerParam, nameDescParam, pvNameParam);

        QueryDataSetsApiResult apiResult = dpApplication.queryDataSets(
            datasetIdParam, ownerParam, nameDescParam, pvNameParam);
        
        if (apiResult == null) {
            throw new RuntimeException("Dataset search failed - null response from service");
        }
        
        if (apiResult.resultStatus.isError) {
            throw new RuntimeException("Dataset search failed: " + apiResult.resultStatus.msg);
        }
        
        if (apiResult.dataSets != null) {
            processSearchResults(apiResult.dataSets);
        }
    }

    private void processSearchResults(List<DataSet> datasets) {
        // Create table rows from dataset info on JavaFX thread
        javafx.application.Platform.runLater(() -> {
            datasetResults.clear();
            
            for (DataSet dataset : datasets) {
                DatasetInfoTableRow tableRow = new DatasetInfoTableRow(dataset);
                datasetResults.add(tableRow);
            }
            
            resultCount.set(datasetResults.size());
            statusMessage.set(String.format("Found %d dataset(s)", datasetResults.size()));
            
            logger.debug("Processed {} dataset search results", datasetResults.size());
        });
    }

    /**
     * Navigate to data-explore view and load dataset in Dataset Builder
     */
    public void navigateToDatasetBuilder(String datasetId) {
        if (mainController != null) {
            // TODO: Implement navigation to data-explore Dataset Builder with dataset loading
            logger.debug("Navigation to Dataset Builder with dataset ID: {}", datasetId);
        } else {
            logger.warn("MainController is null, cannot navigate to Dataset Builder");
        }
    }

    public void cancel() {
        logger.info("Dataset search cancelled by user");
        statusMessage.set("Operation cancelled");
    }

    public void updateStatus(String message) {
        statusMessage.set(message);
        logger.debug("Status updated: {}", message);
    }
}