package com.ospreydcs.dp.gui.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Reusable component for Request Details section.
 * Contains request tags, attributes, and event name.
 */
public class RequestDetailsComponent extends VBox implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // FXML components
    @FXML private TagsListComponent requestTagsComponent;
    @FXML private AttributesListComponent requestAttributesComponent;
    @FXML private TextField eventNameField;

    // Properties for external binding
    private final StringProperty eventName = new SimpleStringProperty();

    public RequestDetailsComponent() {
        // Load FXML and set this as controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/request-details-component.fxml"));
        fxmlLoader.setController(this);

        try {
            VBox root = fxmlLoader.load();
            this.getChildren().setAll(root.getChildren());
            this.setSpacing(root.getSpacing());
            this.getStyleClass().setAll(root.getStyleClass());
        } catch (IOException exception) {
            logger.error("Failed to load RequestDetailsComponent FXML", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("RequestDetailsComponent initialized");

        // Bind event name field to property
        eventNameField.textProperty().bindBidirectional(eventName);

        logger.debug("RequestDetailsComponent setup complete");
    }

    // Public API methods for external access

    /**
     * Gets the event name.
     */
    public String getEventName() {
        return eventName.get();
    }

    /**
     * Sets the event name.
     */
    public void setEventName(String eventName) {
        this.eventName.set(eventName);
    }

    /**
     * Property for event name binding.
     */
    public StringProperty eventNameProperty() {
        return eventName;
    }

    /**
     * Gets the request tags list.
     */
    public ObservableList<String> getRequestTags() {
        return requestTagsComponent.getTags();
    }

    /**
     * Sets the request tags list.
     */
    public void setRequestTags(ObservableList<String> tags) {
        requestTagsComponent.setTags(tags);
    }

    /**
     * Gets the request attributes list.
     */
    public ObservableList<String> getRequestAttributes() {
        return requestAttributesComponent.getAttributes();
    }

    /**
     * Sets the request attributes list.
     */
    public void setRequestAttributes(ObservableList<String> attributes) {
        requestAttributesComponent.setAttributes(attributes);
    }

    /**
     * Clears all request details.
     */
    public void clearRequestDetails() {
        setEventName("");
        requestTagsComponent.clearTags();
        requestAttributesComponent.clearAttributes();
        logger.debug("Request details cleared");
    }
}