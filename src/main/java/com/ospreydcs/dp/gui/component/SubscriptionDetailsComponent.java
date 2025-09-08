package com.ospreydcs.dp.gui.component;

import com.ospreydcs.dp.gui.DpApplication;
import com.ospreydcs.dp.gui.model.SubscribeDataEventDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Reusable component for capturing data event subscription details.
 * Follows the pattern established by other reusable components like ProviderDetailsComponent.
 * Contains a list of SubscribeDataEventDetail objects and a form for adding new subscriptions.
 */
public class SubscriptionDetailsComponent extends VBox implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // FXML components
    @FXML private ListView<SubscribeDataEventDetail> subscriptionsList;
    @FXML private TextField pvNameField;
    @FXML private ComboBox<DpApplication.TriggerCondition> triggerConditionCombo;
    @FXML private TextField triggerValueField;

    // Data storage
    private final ObservableList<SubscribeDataEventDetail> subscriptions = FXCollections.observableArrayList();

    public SubscriptionDetailsComponent() {
        // Load FXML and set this as controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/subscription-details-component.fxml"));
        fxmlLoader.setController(this);

        try {
            VBox root = fxmlLoader.load();
            this.getChildren().setAll(root.getChildren());
            this.setSpacing(root.getSpacing());
            this.setPadding(root.getPadding());
            this.getStyleClass().setAll(root.getStyleClass());
            
            logger.debug("SubscriptionDetailsComponent FXML loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load subscription-details-component.fxml", e);
            throw new RuntimeException("Failed to load subscription details component", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("SubscriptionDetailsComponent initializing...");
        
        // Set up ListView with subscriptions data
        subscriptionsList.setItems(subscriptions);
        
        // Set up ComboBox with TriggerCondition enum values
        setupTriggerConditionCombo();
        
        // Set up context menu for removing subscriptions
        setupContextMenu();
        
        // Set up auto-submission form pattern
        setupAutoSubmissionHandlers();
        
        logger.debug("SubscriptionDetailsComponent initialized successfully");
    }

    private void setupTriggerConditionCombo() {
        triggerConditionCombo.setItems(FXCollections.observableArrayList(DpApplication.TriggerCondition.values()));
        
        // Custom string converter for user-friendly display
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
                // Not used for ComboBox selection
                return null;
            }
        });
        
        triggerConditionCombo.setMaxWidth(Double.MAX_VALUE);
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(e -> {
            SubscribeDataEventDetail selected = subscriptionsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                subscriptions.remove(selected);
                logger.debug("Removed subscription: {}", selected.getDisplayString());
            }
        });
        contextMenu.getItems().add(removeItem);
        subscriptionsList.setContextMenu(contextMenu);
    }

    private void setupAutoSubmissionHandlers() {
        // Auto-submit when user presses Enter in any text field
        pvNameField.setOnAction(e -> attemptSubscriptionSubmission());
        triggerValueField.setOnAction(e -> attemptSubscriptionSubmission());
        
        // Auto-submit when user moves focus away from the last required field
        triggerValueField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // Lost focus
                attemptSubscriptionSubmission();
            }
        });
        
        // Also handle ComboBox selection change
        triggerConditionCombo.setOnAction(e -> {
            // If this was the last missing field, try auto-submission
            if (isFormValid()) {
                javafx.application.Platform.runLater(this::attemptSubscriptionSubmission);
            }
        });
    }

    private void attemptSubscriptionSubmission() {
        if (isFormValid()) {
            addCurrentSubscription();
            
            // Return focus to first field for next entry
            javafx.application.Platform.runLater(() -> {
                pvNameField.requestFocus();
            });
        }
    }

    private boolean isFormValid() {
        return pvNameField.getText() != null && !pvNameField.getText().trim().isEmpty() &&
               triggerConditionCombo.getValue() != null &&
               triggerValueField.getText() != null && !triggerValueField.getText().trim().isEmpty();
    }

    private void addCurrentSubscription() {
        String pvName = pvNameField.getText().trim();
        DpApplication.TriggerCondition condition = triggerConditionCombo.getValue();
        String triggerValue = triggerValueField.getText().trim();
        
        SubscribeDataEventDetail subscription = new SubscribeDataEventDetail(pvName, condition, triggerValue);
        subscriptions.add(subscription);
        
        // Clear form fields
        pvNameField.clear();
        triggerConditionCombo.setValue(null);
        triggerValueField.clear();
        
        logger.debug("Added subscription: {}", subscription.getDisplayString());
    }

    // Public API methods for external access

    /**
     * Returns the list of subscriptions for API calls.
     * This follows the Critical Integration Pattern - always access component data directly.
     */
    public List<SubscribeDataEventDetail> getSubscriptions() {
        return List.copyOf(subscriptions);
    }

    /**
     * Clears all subscriptions from the component.
     */
    public void clearSubscriptions() {
        subscriptions.clear();
        // Also clear form fields
        pvNameField.clear();
        triggerConditionCombo.setValue(null);
        triggerValueField.clear();
        logger.debug("Cleared all subscriptions and form fields");
    }

    /**
     * Returns the observable list for external binding if needed.
     */
    public ObservableList<SubscribeDataEventDetail> getSubscriptionsObservableList() {
        return subscriptions;
    }
}