package com.ospreydcs.dp.gui;

import com.ospreydcs.dp.gui.model.PvDetail;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DataGenerationController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // Provider Details FXML components
    @FXML private TextField providerNameField;
    @FXML private TextArea providerDescriptionArea;
    @FXML private ComboBox<String> providerTagsCombo;
    @FXML private Button addProviderTagButton;
    @FXML private ListView<String> providerTagsList;
    @FXML private ComboBox<String> providerAttributeKeyCombo;
    @FXML private ComboBox<String> providerAttributeValueCombo;
    @FXML private Button addProviderAttributeButton;
    @FXML private ListView<String> providerAttributesList;

    // Request Details FXML components
    @FXML private DatePicker dataBeginDatePicker;
    @FXML private Spinner<Integer> beginHourSpinner;
    @FXML private Spinner<Integer> beginMinuteSpinner;
    @FXML private Spinner<Integer> beginSecondSpinner;
    @FXML private DatePicker dataEndDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;
    @FXML private Spinner<Integer> endMinuteSpinner;
    @FXML private Spinner<Integer> endSecondSpinner;
    @FXML private ComboBox<String> requestTagsCombo;
    @FXML private Button addRequestTagButton;
    @FXML private ListView<String> requestTagsList;
    @FXML private ComboBox<String> requestAttributeKeyCombo;
    @FXML private ComboBox<String> requestAttributeValueCombo;
    @FXML private Button addRequestAttributeButton;
    @FXML private ListView<String> requestAttributesList;
    @FXML private ComboBox<String> eventNameCombo;

    // PV Details FXML components
    @FXML private ListView<PvDetail> pvDetailsList;
    @FXML private VBox pvDetailEntryPanel;
    @FXML private TextField pvNameField;
    @FXML private ComboBox<String> pvDataTypeCombo;
    @FXML private ComboBox<Integer> pvValuesPerSecondCombo;
    @FXML private TextField pvInitialValueField;
    @FXML private TextField pvMaxStepField;

    // Action buttons
    @FXML private Button generateButton;
    @FXML private Button cancelButton;

    // Dependencies
    private DataGenerationViewModel viewModel;
    private DpApplication dpApplication;
    private Stage primaryStage;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("DataGenerationController initializing...");
        
        // Create the view model
        viewModel = new DataGenerationViewModel();
        
        // Bind UI components to view model properties
        bindUIToViewModel();
        
        // Set up event handlers
        setupEventHandlers();
        
        // Populate ComboBox items
        populateComboBoxes();
        
        logger.debug("DataGenerationController initialized successfully");
    }

    private void bindUIToViewModel() {
        // Provider Details bindings
        providerNameField.textProperty().bindBidirectional(viewModel.providerNameProperty());
        providerDescriptionArea.textProperty().bindBidirectional(viewModel.providerDescriptionProperty());
        providerTagsList.setItems(viewModel.getProviderTags());
        providerAttributesList.setItems(viewModel.getProviderAttributes());

        // Request Details bindings
        dataBeginDatePicker.valueProperty().bindBidirectional(viewModel.dataBeginDateProperty());
        
        
        // Use manual synchronization instead of bidirectional binding for better control
        setupSpinnerBinding(beginHourSpinner, viewModel.beginHourProperty(), "beginHour");
        setupSpinnerBinding(beginMinuteSpinner, viewModel.beginMinuteProperty(), "beginMinute");
        setupSpinnerBinding(beginSecondSpinner, viewModel.beginSecondProperty(), "beginSecond");
        
        dataEndDatePicker.valueProperty().bindBidirectional(viewModel.dataEndDateProperty());
        
        setupSpinnerBinding(endHourSpinner, viewModel.endHourProperty(), "endHour");
        setupSpinnerBinding(endMinuteSpinner, viewModel.endMinuteProperty(), "endMinute");
        setupSpinnerBinding(endSecondSpinner, viewModel.endSecondProperty(), "endSecond");
        
        logger.debug("Time spinner bindings completed");
        
        requestTagsList.setItems(viewModel.getRequestTags());
        requestAttributesList.setItems(viewModel.getRequestAttributes());
        eventNameCombo.valueProperty().bindBidirectional(viewModel.eventNameProperty());

        // PV Details bindings
        pvDetailsList.setItems(viewModel.getPvDetails());
        
        pvNameField.textProperty().bindBidirectional(viewModel.currentPvNameProperty());
        pvDataTypeCombo.valueProperty().bindBidirectional(viewModel.currentPvDataTypeProperty());
        
        // Use custom binding for Integer ComboBox instead of asObject() which might not work correctly
        setupIntegerComboBinding(pvValuesPerSecondCombo, viewModel.currentPvValuesPerSecondProperty());
        pvInitialValueField.textProperty().bindBidirectional(viewModel.currentPvInitialValueProperty());
        pvMaxStepField.textProperty().bindBidirectional(viewModel.currentPvMaxStepProperty());

        // Button state bindings
        generateButton.disableProperty().bind(viewModel.isGeneratingProperty());
    }

    private void setupEventHandlers() {
        // Set up custom cell factory for PV details list to show delete option
        pvDetailsList.setCellFactory(listView -> new ListCell<PvDetail>() {
            @Override
            protected void updateItem(PvDetail item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setText(item.toString());
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            viewModel.removePvDetail(item);
                        }
                    });
                }
            }
        });

        // Set up automatic PV form submission
        setupPvFormAutoSubmission();

        // Set up context menu for list items
        setupContextMenus();
    }

    private void setupContextMenus() {
        // Provider tags context menu
        providerTagsList.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Remove");
            deleteItem.setOnAction(e -> viewModel.removeProviderTag(cell.getItem()));
            contextMenu.getItems().add(deleteItem);
            
            cell.setContextMenu(contextMenu);
            return cell;
        });

        // Provider attributes context menu
        providerAttributesList.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Remove");
            deleteItem.setOnAction(e -> viewModel.removeProviderAttribute(cell.getItem()));
            contextMenu.getItems().add(deleteItem);
            
            cell.setContextMenu(contextMenu);
            return cell;
        });

        // Request tags context menu
        requestTagsList.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Remove");
            deleteItem.setOnAction(e -> viewModel.removeRequestTag(cell.getItem()));
            contextMenu.getItems().add(deleteItem);
            
            cell.setContextMenu(contextMenu);
            return cell;
        });

        // Request attributes context menu
        requestAttributesList.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Remove");
            deleteItem.setOnAction(e -> viewModel.removeRequestAttribute(cell.getItem()));
            contextMenu.getItems().add(deleteItem);
            
            cell.setContextMenu(contextMenu);
            return cell;
        });

        // PV details context menu
        pvDetailsList.setCellFactory(listView -> {
            ListCell<PvDetail> cell = new ListCell<PvDetail>() {
                @Override
                protected void updateItem(PvDetail item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
            
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Remove");
            deleteItem.setOnAction(e -> viewModel.removePvDetail(cell.getItem()));
            contextMenu.getItems().add(deleteItem);
            
            cell.setContextMenu(contextMenu);
            return cell;
        });
    }

    private void populateComboBoxes() {
        // Provider Tags ComboBox
        providerTagsCombo.getItems().addAll("IOC", "application", "batch");
        
        // Provider Attribute Key ComboBox
        providerAttributeKeyCombo.getItems().addAll("sector", "subsystem");
        
        // Request Tags ComboBox
        requestTagsCombo.getItems().addAll("commissioning", "outage", "experiment");
        
        // Request Attribute Key ComboBox
        requestAttributeKeyCombo.getItems().addAll("status", "mode");
        
        // Event Name ComboBox
        eventNameCombo.getItems().addAll("Commission-1", "Commission-2", "Experiment-1", "Experiment-2");
        
        // PV Data Type ComboBox
        pvDataTypeCombo.getItems().addAll("integer", "float");
        
        // PV Values per Second ComboBox
        pvValuesPerSecondCombo.getItems().addAll(1, 10, 100);
        
        // Set initial value to match ViewModel default
        pvValuesPerSecondCombo.setValue(viewModel.currentPvValuesPerSecondProperty().get());
        
        logger.debug("ComboBox items populated");
    }
    
    private void setupSpinnerBinding(Spinner<Integer> spinner, javafx.beans.property.IntegerProperty viewModelProperty, String name) {
        if (spinner.getValueFactory() == null) {
            logger.error("{} spinner value factory is null!", name);
            return;
        }
        
        logger.debug("Setting up binding for {} spinner", name);
        
        // Initialize ViewModel property from spinner value
        viewModelProperty.set(spinner.getValue());
        logger.debug("Initialized {} ViewModel property to: {}", name, spinner.getValue());
        
        // Listen for changes in spinner and update ViewModel
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                logger.debug("{} spinner changed from {} to {}", name, oldVal, newVal);
                viewModelProperty.set(newVal);
            }
        });
        
        // Listen for changes in ViewModel and update spinner
        viewModelProperty.addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(spinner.getValue())) {
                logger.debug("{} ViewModel property changed from {} to {}", name, oldVal, newVal);
                spinner.getValueFactory().setValue(newVal.intValue());
            }
        });
        
        logger.debug("{} spinner binding completed", name);
    }
    
    private void setupIntegerComboBinding(ComboBox<Integer> combo, javafx.beans.property.IntegerProperty viewModelProperty) {
        logger.debug("Setting up custom Integer ComboBox binding");
        
        // Initialize ViewModel property from ComboBox value
        if (combo.getValue() != null) {
            viewModelProperty.set(combo.getValue());
        }
        logger.debug("Initialized ViewModel property to: {}", viewModelProperty.get());
        
        // Listen for changes in ComboBox and update ViewModel
        combo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                logger.debug("pvValuesPerSecondCombo changed from {} to {}", oldVal, newVal);
                viewModelProperty.set(newVal);
                logger.debug("Updated ViewModel property to: {}", viewModelProperty.get());
            }
        });
        
        // Listen for changes in ViewModel and update ComboBox
        viewModelProperty.addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(combo.getValue())) {
                logger.debug("ViewModel property changed from {} to {}, updating ComboBox", oldVal, newVal);
                combo.setValue(newVal.intValue());
            }
        });
        
        logger.debug("Integer ComboBox binding completed");
    }
    
    private void setupPvFormAutoSubmission() {
        // Auto-submit when user presses Enter in any of the PV form fields
        pvNameField.setOnAction(e -> attemptPvFormSubmission());
        pvInitialValueField.setOnAction(e -> attemptPvFormSubmission());
        pvMaxStepField.setOnAction(e -> attemptPvFormSubmission());
        
        // Auto-submit when user moves focus away from the last required field
        pvMaxStepField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // Lost focus
                attemptPvFormSubmission();
            }
        });
        
        logger.debug("PV form auto-submission handlers set up");
    }
    
    private void attemptPvFormSubmission() {
        // Only auto-submit if all required fields are filled
        if (pvNameField.getText() != null && !pvNameField.getText().trim().isEmpty() &&
            pvDataTypeCombo.getValue() != null &&
            pvValuesPerSecondCombo.getValue() != null &&
            pvInitialValueField.getText() != null && !pvInitialValueField.getText().trim().isEmpty() &&
            pvMaxStepField.getText() != null && !pvMaxStepField.getText().trim().isEmpty()) {
            
            logger.debug("Auto-submitting PV form");
            
            // Store the current PV name to check if addition was successful
            String currentPvName = pvNameField.getText().trim();
            
            // Attempt to add the PV
            viewModel.addCurrentPvDetail();
            
            // If the form was cleared (indicating successful addition), move focus back to PV Name field
            if (pvNameField.getText() == null || pvNameField.getText().trim().isEmpty()) {
                // Use Platform.runLater to ensure the focus change happens after the form is cleared
                javafx.application.Platform.runLater(() -> {
                    pvNameField.requestFocus();
                    logger.debug("Moved focus back to PV Name field for next entry");
                });
            }
        }
    }
    
    private void setupStatusListener() {
        // Connect ViewModel status messages to MainController status display
        if (viewModel != null && mainController != null) {
            viewModel.statusMessageProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus != null && !newStatus.trim().isEmpty()) {
                    mainController.getViewModel().updateStatus(newStatus);
                }
            });
            logger.debug("Status listener established between DataGenerationViewModel and MainController");
        }
    }

    // Dependency injection methods
    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        if (viewModel != null) {
            viewModel.setDpApplication(dpApplication);
        }
        logger.debug("DpApplication injected into DataGenerationController");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        logger.debug("Primary stage injected into DataGenerationController");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        
        // Inject MainController into ViewModel for home view updates
        if (viewModel != null) {
            viewModel.setMainController(mainController);
        }
        
        // Set up status message forwarding
        setupStatusListener();
        
        logger.debug("MainController injected into DataGenerationController");
    }

    // Provider Details action handlers
    @FXML
    private void onAddProviderTag() {
        String selectedTag = providerTagsCombo.getValue();
        if (selectedTag != null) {
            viewModel.addProviderTag(selectedTag);
            providerTagsCombo.setValue(null);
        }
    }

    @FXML
    private void onProviderAttributeKeyChanged() {
        String selectedKey = providerAttributeKeyCombo.getValue();
        if (selectedKey != null) {
            providerAttributeValueCombo.setItems(viewModel.getProviderAttributeValues(selectedKey));
            providerAttributeValueCombo.setValue(null);
        }
    }

    @FXML
    private void onAddProviderAttribute() {
        String key = providerAttributeKeyCombo.getValue();
        String value = providerAttributeValueCombo.getValue();
        if (key != null && value != null) {
            viewModel.addProviderAttribute(key, value);
            providerAttributeKeyCombo.setValue(null);
            providerAttributeValueCombo.setValue(null);
        }
    }

    // Request Details action handlers
    @FXML
    private void onAddRequestTag() {
        String selectedTag = requestTagsCombo.getValue();
        if (selectedTag != null) {
            viewModel.addRequestTag(selectedTag);
            requestTagsCombo.setValue(null);
        }
    }

    @FXML
    private void onRequestAttributeKeyChanged() {
        String selectedKey = requestAttributeKeyCombo.getValue();
        if (selectedKey != null) {
            requestAttributeValueCombo.setItems(viewModel.getRequestAttributeValues(selectedKey));
            requestAttributeValueCombo.setValue(null);
        }
    }

    @FXML
    private void onAddRequestAttribute() {
        String key = requestAttributeKeyCombo.getValue();
        String value = requestAttributeValueCombo.getValue();
        if (key != null && value != null) {
            viewModel.addRequestAttribute(key, value);
            requestAttributeKeyCombo.setValue(null);
            requestAttributeValueCombo.setValue(null);
        }
    }

    // PV Details action handlers - form is always visible now
    // User adds PV by pressing Enter or clicking away from fields when valid

    // Main action handlers
    @FXML
    private void onGenerate() {
        logger.info("Generate button clicked");
        viewModel.generateData();
    }

    @FXML
    private void onCancel() {
        logger.info("Cancel button clicked");
        viewModel.cancel();
        // Navigate back to main window
        if (mainController != null) {
            mainController.switchToMainView();
        } else {
            logger.warn("MainController reference is null, cannot navigate back");
        }
    }
}