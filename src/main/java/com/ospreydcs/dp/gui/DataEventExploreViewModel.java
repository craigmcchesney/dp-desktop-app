package com.ospreydcs.dp.gui;

import com.ospreydcs.dp.client.IngestionClient;
import com.ospreydcs.dp.grpc.v1.ingestionstream.SubscribeDataEventResponse;
import com.ospreydcs.dp.gui.model.DataEventSubscription;
import com.ospreydcs.dp.gui.model.SubscribeDataEventDetail;
import com.ospreydcs.dp.service.common.model.ResultStatus;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataEventExploreViewModel {

    private static final Logger logger = LogManager.getLogger();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    // Subscription Builder form properties
    private final StringProperty pvName = new SimpleStringProperty("");
    private final ObjectProperty<DpApplication.TriggerCondition> triggerCondition = new SimpleObjectProperty<>();
    private final StringProperty triggerValue = new SimpleStringProperty("");
    private final ObjectProperty<IngestionClient.IngestionDataType> pvDataType = new SimpleObjectProperty<>();

    // Status properties
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty isAdding = new SimpleBooleanProperty(false);

    // Data collections
    private final ObservableList<DataEventSubscription> subscriptions = FXCollections.observableArrayList();
    private final ObservableList<SubscribeDataEventResponse.Event> events = FXCollections.observableArrayList();

    // Form validation
    private final BooleanBinding isFormValid = pvName.isNotEmpty()
            .and(triggerCondition.isNotNull())
            .and(triggerValue.isNotEmpty())
            .and(pvDataType.isNotNull());

    // Dependencies
    private DpApplication dpApplication;
    private MainController mainController;

    public DataEventExploreViewModel() {
        logger.debug("DataEventExploreViewModel created");
    }

    // Property accessors
    public StringProperty pvNameProperty() { return pvName; }
    public ObjectProperty<DpApplication.TriggerCondition> triggerConditionProperty() { return triggerCondition; }
    public StringProperty triggerValueProperty() { return triggerValue; }
    public ObjectProperty<IngestionClient.IngestionDataType> pvDataTypeProperty() { return pvDataType; }
    public StringProperty statusMessageProperty() { return statusMessage; }
    public BooleanProperty isAddingProperty() { return isAdding; }
    public BooleanBinding isFormValidProperty() { return isFormValid; }

    // Data collection accessors
    public ObservableList<DataEventSubscription> getSubscriptions() { return subscriptions; }
    public ObservableList<SubscribeDataEventResponse.Event> getEvents() { return events; }

    // Dependency injection methods
    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        
        // Sync subscriptions with DpApplication state
        syncSubscriptionsFromApplication();
        
        logger.debug("DpApplication injected into DataEventExploreViewModel");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        logger.debug("MainController injected into DataEventExploreViewModel");
    }

    // Business logic methods
    public void syncSubscriptionsFromApplication() {
        if (dpApplication != null) {
            subscriptions.clear();
            subscriptions.addAll(dpApplication.getDataEventSubscriptions());
            logger.debug("Synced {} subscriptions from DpApplication", subscriptions.size());
        }
    }

    public void addSubscription() {
        if (dpApplication == null || !isFormValid.get()) {
            updateStatus("Cannot add subscription: form is invalid or application not initialized");
            return;
        }

        isAdding.set(true);
        updateStatus("Adding data event subscription...");

        // Create background task for subscription
        Task<ResultStatus> addTask = new Task<ResultStatus>() {
            @Override
            protected ResultStatus call() throws Exception {
                SubscribeDataEventDetail subscriptionDetail = new SubscribeDataEventDetail(
                    pvName.get().trim(),
                    triggerCondition.get(),
                    triggerValue.get().trim()
                );

                return dpApplication.subscribeDataEvent(subscriptionDetail, pvDataType.get());
            }
        };

        addTask.setOnSucceeded(e -> {
            javafx.application.Platform.runLater(() -> {
                ResultStatus result = addTask.getValue();
                if (result.isError) {
                    updateStatus("Failed to add subscription: " + result.msg);
                } else {
                    updateStatus("Subscription added successfully");
                    
                    // Clear form
                    clearForm();
                    
                    // Refresh subscriptions from DpApplication
                    syncSubscriptionsFromApplication();
                }
                isAdding.set(false);
            });
        });

        addTask.setOnFailed(e -> {
            javafx.application.Platform.runLater(() -> {
                updateStatus("Error adding subscription: " + addTask.getException().getMessage());
                isAdding.set(false);
            });
        });

        Thread addThread = new Thread(addTask);
        addThread.setDaemon(true);
        addThread.start();
    }

    public void cancelSubscription(DataEventSubscription subscription) {
        if (dpApplication == null) {
            updateStatus("Cannot cancel subscription: application not initialized");
            return;
        }

        updateStatus("Canceling subscription...");

        // Create background task for cancellation
        Task<ResultStatus> cancelTask = new Task<ResultStatus>() {
            @Override
            protected ResultStatus call() throws Exception {
                return dpApplication.cancelDataEventSubscription(subscription);
            }
        };

        cancelTask.setOnSucceeded(e -> {
            javafx.application.Platform.runLater(() -> {
                ResultStatus result = cancelTask.getValue();
                if (result.isError) {
                    updateStatus("Failed to cancel subscription: " + result.msg);
                } else {
                    updateStatus("Subscription canceled successfully");
                    
                    // Refresh subscriptions from DpApplication
                    syncSubscriptionsFromApplication();
                    
                    // Clear events if this was the selected subscription
                    events.clear();
                }
            });
        });

        cancelTask.setOnFailed(e -> {
            javafx.application.Platform.runLater(() -> {
                updateStatus("Error canceling subscription: " + cancelTask.getException().getMessage());
            });
        });

        Thread cancelThread = new Thread(cancelTask);
        cancelThread.setDaemon(true);
        cancelThread.start();
    }

    public void loadEventsForSubscription(DataEventSubscription subscription) {
        if (dpApplication == null) {
            updateStatus("Cannot load events: application not initialized");
            return;
        }

        // Track the selected subscription for navigation
        setCurrentlySelectedSubscription(subscription);
        
        updateStatus("Loading events for subscription...");

        // Create background task for loading events
        Task<List<SubscribeDataEventResponse.Event>> loadTask = new Task<List<SubscribeDataEventResponse.Event>>() {
            @Override
            protected List<SubscribeDataEventResponse.Event> call() throws Exception {
                return dpApplication.dataEventsForSubscription(subscription);
            }
        };

        loadTask.setOnSucceeded(e -> {
            javafx.application.Platform.runLater(() -> {
                List<SubscribeDataEventResponse.Event> eventList = loadTask.getValue();
                events.clear();
                events.addAll(eventList);
                
                updateStatus("Loaded " + eventList.size() + " events for subscription: " + subscription.getDisplayString());
            });
        });

        loadTask.setOnFailed(e -> {
            javafx.application.Platform.runLater(() -> {
                updateStatus("Error loading events: " + loadTask.getException().getMessage());
                events.clear();
            });
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // Track the currently selected subscription for navigation
    private DataEventSubscription currentlySelectedSubscription = null;
    
    public void setCurrentlySelectedSubscription(DataEventSubscription subscription) {
        this.currentlySelectedSubscription = subscription;
    }

    public void navigateToQueryEditor(SubscribeDataEventResponse.Event event) {
        if (dpApplication == null || mainController == null) {
            updateStatus("Cannot navigate: application or controller not initialized");
            return;
        }

        if (currentlySelectedSubscription == null) {
            updateStatus("No subscription selected for navigation");
            return;
        }

        String pvName = currentlySelectedSubscription.subscriptionDetail.pvName;

        // Convert event timestamp to Instant
        Instant eventTime = Instant.ofEpochSecond(
            event.getEventTime().getEpochSeconds(),
            event.getEventTime().getNanoseconds()
        );

        // Update DpApplication state for Query Editor
        dpApplication.setPvNames(List.of(pvName));
        dpApplication.setDataBeginTime(eventTime.minusSeconds(30)); // 30 seconds before event
        dpApplication.setDataEndTime(eventTime.plusSeconds(30)); // 30 seconds after event

        // Navigate to data-explore Query Editor
        if (mainController != null) {
            mainController.switchToDataExploreView();
            updateStatus("Navigated to Query Editor with event data for " + pvName + " at " + TIME_FORMATTER.format(eventTime));
        }
    }

    private void clearForm() {
        pvName.set("");
        triggerCondition.set(null);
        triggerValue.set("");
        pvDataType.set(null);
    }

    private void updateStatus(String message) {
        statusMessage.set(message);
        logger.debug("Status updated: {}", message);
    }
}