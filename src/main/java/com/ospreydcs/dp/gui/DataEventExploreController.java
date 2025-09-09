package com.ospreydcs.dp.gui;

import com.ospreydcs.dp.client.IngestionClient;
import com.ospreydcs.dp.grpc.v1.ingestionstream.SubscribeDataEventResponse;
import com.ospreydcs.dp.gui.model.DataEventSubscription;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class DataEventExploreController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // Data Event Subscriptions section (left panel)
    @FXML private ListView<DataEventSubscription> subscriptionsList;

    // Subscription Builder section (top right)
    @FXML private TextField pvNameField;
    @FXML private ComboBox<DpApplication.TriggerCondition> triggerConditionCombo;
    @FXML private TextField triggerValueField;
    @FXML private ComboBox<IngestionClient.IngestionDataType> pvDataTypeCombo;
    @FXML private Button addButton;

    // Data Events section (bottom right)
    @FXML private TableView<SubscribeDataEventResponse.Event> eventsTable;
    @FXML private TableColumn<SubscribeDataEventResponse.Event, String> eventTimeColumn;
    @FXML private TableColumn<SubscribeDataEventResponse.Event, String> triggerValueColumn;

    // Dependencies
    private DataEventExploreViewModel viewModel;
    private DpApplication dpApplication;
    private Stage primaryStage;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("DataEventExploreController initializing...");
        
        // Create the view model
        viewModel = new DataEventExploreViewModel();
        
        // Bind UI components to view model properties
        bindUIToViewModel();
        
        // Set up event handlers
        setupEventHandlers();
        
        // Populate ComboBox items
        populateComboBoxes();
        
        logger.debug("DataEventExploreController initialized successfully");
    }

    private void bindUIToViewModel() {
        // Data Event Subscriptions list binding
        subscriptionsList.setItems(viewModel.getSubscriptions());
        
        // Set up custom cell factory for subscriptions list
        setupSubscriptionsListCellFactory();
        
        // Subscription Builder form bindings
        pvNameField.textProperty().bindBidirectional(viewModel.pvNameProperty());
        triggerConditionCombo.valueProperty().bindBidirectional(viewModel.triggerConditionProperty());
        triggerValueField.textProperty().bindBidirectional(viewModel.triggerValueProperty());
        pvDataTypeCombo.valueProperty().bindBidirectional(viewModel.pvDataTypeProperty());
        
        // Data Events table binding
        eventsTable.setItems(viewModel.getEvents());
        
        // Set up table columns
        setupEventsTableColumns();
        
        // Button state bindings
        addButton.disableProperty().bind(viewModel.isAddingProperty().or(viewModel.isFormValidProperty().not()));
    }

    private void setupEventHandlers() {
        // Set up subscription list selection handling
        subscriptionsList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    viewModel.loadEventsForSubscription(newSelection);
                }
            }
        );
        
        // Set up status listener for MainController communication
        setupStatusListener();
    }
    
    private void setupStatusListener() {
        // Connect ViewModel status messages to MainController status display
        if (viewModel != null && mainController != null) {
            viewModel.statusMessageProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus != null && !newStatus.trim().isEmpty()) {
                    mainController.getViewModel().updateStatus(newStatus);
                }
            });
            logger.debug("Status listener established between DataEventExploreViewModel and MainController");
        }
    }

    private void populateComboBoxes() {
        // Trigger Condition ComboBox (same as SubscriptionDetailsComponent)
        triggerConditionCombo.getItems().addAll(DpApplication.TriggerCondition.values());
        
        // Set up trigger condition display converter
        triggerConditionCombo.setConverter(new StringConverter<DpApplication.TriggerCondition>() {
            @Override
            public String toString(DpApplication.TriggerCondition condition) {
                if (condition == null) return "";
                return switch (condition) {
                    case EQUAL_TO -> "Equal to (=)";
                    case GREATER -> "Greater than (>)";
                    case GREATER_OR_EQUAL -> "Greater or equal (>=)";
                    case LESS -> "Less than (<)";
                    case LESS_OR_EQUAL -> "Less or equal (<=)";
                };
            }

            @Override
            public DpApplication.TriggerCondition fromString(String string) {
                return null; // Not used for ComboBox selection
            }
        });
        
        // PV Data Type ComboBox (only the types mentioned in section 19.4)
        pvDataTypeCombo.getItems().addAll(
            IngestionClient.IngestionDataType.UINT,
            IngestionClient.IngestionDataType.ULONG,
            IngestionClient.IngestionDataType.INT,
            IngestionClient.IngestionDataType.LONG,
            IngestionClient.IngestionDataType.FLOAT,
            IngestionClient.IngestionDataType.DOUBLE
        );
        
        logger.debug("ComboBox items populated");
    }
    
    private void setupSubscriptionsListCellFactory() {
        subscriptionsList.setCellFactory(listView -> new SubscriptionListCell());
    }
    
    private void setupEventsTableColumns() {
        // Event Time column with hyperlink
        eventTimeColumn.setCellValueFactory(cellData -> {
            SubscribeDataEventResponse.Event event = cellData.getValue();
            java.time.Instant instant = java.time.Instant.ofEpochSecond(
                event.getEventTime().getEpochSeconds(),
                event.getEventTime().getNanoseconds()
            );
            String formattedTime = instant.atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new javafx.beans.property.SimpleStringProperty(formattedTime);
        });
        
        eventTimeColumn.setCellFactory(column -> new EventTimeTableCell());
        
        // Trigger Value column
        triggerValueColumn.setCellValueFactory(cellData -> {
            SubscribeDataEventResponse.Event event = cellData.getValue();
            String value = event.hasDataValue() ? event.getDataValue().toString() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(value);
        });
    }

    // Custom ListCell for subscriptions with hyperlink and trash button
    private class SubscriptionListCell extends ListCell<DataEventSubscription> {
        private HBox content;
        private Hyperlink nameLink;
        private Button removeButton;

        public SubscriptionListCell() {
            super();
            content = new HBox();
            content.setSpacing(5);
            content.setPadding(new Insets(2, 5, 2, 5));

            nameLink = new Hyperlink();
            nameLink.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLink, Priority.ALWAYS);

            removeButton = new Button("🗑️");
            removeButton.getStyleClass().addAll("btn", "btn-danger", "btn-xs");

            content.getChildren().addAll(nameLink, removeButton);
        }

        @Override
        protected void updateItem(DataEventSubscription item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                nameLink.setText(item.getDisplayString());
                nameLink.setOnAction(e -> {
                    // Select this item and load events
                    subscriptionsList.getSelectionModel().select(item);
                    viewModel.loadEventsForSubscription(item);
                });

                removeButton.setOnAction(e -> {
                    viewModel.cancelSubscription(item);
                });

                setGraphic(content);
                setText(null);
            }
        }
    }
    
    // Custom TableCell for event time hyperlinks
    private class EventTimeTableCell extends TableCell<SubscribeDataEventResponse.Event, String> {
        private Hyperlink timeLink;

        public EventTimeTableCell() {
            super();
            timeLink = new Hyperlink();
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                timeLink.setText(item);
                timeLink.setOnAction(e -> {
                    SubscribeDataEventResponse.Event event = getTableRow().getItem();
                    if (event != null) {
                        viewModel.navigateToQueryEditor(event);
                    }
                });

                setGraphic(timeLink);
                setText(null);
            }
        }
    }

    // Dependency injection methods
    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        if (viewModel != null) {
            viewModel.setDpApplication(dpApplication);
        }
        logger.debug("DpApplication injected into DataEventExploreController");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        logger.debug("Primary stage injected into DataEventExploreController");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        
        // Inject MainController into ViewModel for navigation
        if (viewModel != null) {
            viewModel.setMainController(mainController);
        }
        
        // Set up status message forwarding
        setupStatusListener();
        
        logger.debug("MainController injected into DataEventExploreController");
    }

    // Action handlers
    @FXML
    private void onAdd() {
        logger.info("Add button clicked");
        viewModel.addSubscription();
    }
}