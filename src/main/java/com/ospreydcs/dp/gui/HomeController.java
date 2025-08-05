package com.ospreydcs.dp.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // FXML injected components
    @FXML private Label hintsLabel;
    @FXML private Label statusLabel;
    @FXML private Label detailsLabel;

    // Dependencies
    private HomeViewModel viewModel;
    private DpApplication dpApplication;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("HomeController initializing...");
        
        // Create the view model
        viewModel = new HomeViewModel();
        
        // Bind UI components to view model properties
        bindUIToViewModel();
        
        logger.debug("HomeController initialized successfully");
    }

    private void bindUIToViewModel() {
        // Bind labels to view model properties
        hintsLabel.textProperty().bind(viewModel.hintsTextProperty());
        statusLabel.textProperty().bind(viewModel.statusTextProperty());
        detailsLabel.textProperty().bind(viewModel.detailsTextProperty());
        
        logger.debug("UI bindings established in HomeController");
    }

    // Dependency injection methods
    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        if (viewModel != null) {
            viewModel.setDpApplication(dpApplication);
        }
        logger.debug("DpApplication injected into HomeController");
    }

    // Getter for view model (to allow other controllers to access it)
    public HomeViewModel getViewModel() {
        return viewModel;
    }

    // Methods for updating home view state from other parts of the application
    public void onDataGenerationSuccess(String message) {
        if (viewModel != null) {
            viewModel.onSuccessfulDataGeneration(message);
            logger.info("Home view updated with data generation success: {}", message);
        }
    }

    public void onQuerySuccess(String message) {
        if (viewModel != null) {
            viewModel.onSuccessfulQuery(message);
            logger.info("Home view updated with query success: {}", message);
        }
    }

    public void refreshApplicationState() {
        if (viewModel != null && dpApplication != null) {
            // Check current application state and update view model accordingly
            // This could query the DpApplication for current state information
            logger.debug("Refreshing home view application state");
        }
    }

    public void resetApplicationState() {
        if (viewModel != null) {
            viewModel.resetApplicationState();
            logger.info("Home view application state reset");
        }
    }
}