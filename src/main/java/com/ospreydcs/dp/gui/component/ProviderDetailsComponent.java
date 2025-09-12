package com.ospreydcs.dp.gui.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Reusable component for Provider Details section.
 * Contains provider name, description, tags, and attributes.
 */
public class ProviderDetailsComponent extends VBox implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // FXML components
    @FXML private TextField providerNameField;
    @FXML private TextArea providerDescriptionArea;
    @FXML private TagsListComponent providerTagsComponent;
    @FXML private AttributesListComponent providerAttributesComponent;

    // Properties for external binding
    private final StringProperty providerName = new SimpleStringProperty();
    private final StringProperty providerDescription = new SimpleStringProperty();

    public ProviderDetailsComponent() {
        // Load FXML and set this as controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/provider-details-component.fxml"));
        fxmlLoader.setController(this);

        try {
            VBox root = fxmlLoader.load();
            this.getChildren().setAll(root.getChildren());
            this.setSpacing(root.getSpacing());
            this.getStyleClass().setAll(root.getStyleClass());
        } catch (IOException exception) {
            logger.error("Failed to load ProviderDetailsComponent FXML", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("ProviderDetailsComponent initialized");

        // Bind text fields to properties
        providerNameField.textProperty().bindBidirectional(providerName);
        providerDescriptionArea.textProperty().bindBidirectional(providerDescription);

        logger.debug("ProviderDetailsComponent setup complete");
    }

    // Public API methods for external access

    /**
     * Gets the provider name.
     */
    public String getProviderName() {
        return providerName.get();
    }

    /**
     * Sets the provider name.
     */
    public void setProviderName(String providerName) {
        this.providerName.set(providerName);
    }

    /**
     * Property for provider name binding.
     */
    public StringProperty providerNameProperty() {
        return providerName;
    }

    /**
     * Gets the provider description.
     */
    public String getProviderDescription() {
        return providerDescription.get();
    }

    /**
     * Sets the provider description.
     */
    public void setProviderDescription(String providerDescription) {
        this.providerDescription.set(providerDescription);
    }

    /**
     * Property for provider description binding.
     */
    public StringProperty providerDescriptionProperty() {
        return providerDescription;
    }

    /**
     * Gets the provider tags list.
     */
    public ObservableList<String> getProviderTags() {
        return providerTagsComponent.getTags();
    }

    /**
     * Sets the provider tags list.
     */
    public void setProviderTags(ObservableList<String> tags) {
        providerTagsComponent.setTags(tags);
    }

    /**
     * Gets the provider attributes list.
     */
    public ObservableList<String> getProviderAttributes() {
        return providerAttributesComponent.getAttributes();
    }

    /**
     * Sets the provider attributes list.
     */
    public void setProviderAttributes(ObservableList<String> attributes) {
        providerAttributesComponent.setAttributes(attributes);
    }

    /**
     * Clears all provider details.
     */
    public void clearProviderDetails() {
        setProviderName("");
        setProviderDescription("");
        providerTagsComponent.clearTags();
        providerAttributesComponent.clearAttributes();
        logger.debug("Provider details cleared");
    }
}