package com.ospreydcs.dp.gui.component;

import com.ospreydcs.dp.gui.DpApplication;
import com.ospreydcs.dp.gui.MainController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Reusable component for Query PVs section.
 * Manages a list of PV names with remove functionality and Edit Query navigation.
 */
public class QueryPvsComponent extends VBox implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    // FXML components
    @FXML private ListView<String> pvNamesListView;
    @FXML private Button editQueryButton;

    // Dependencies for navigation and data management
    private DpApplication dpApplication;
    private MainController mainController;

    // Observable list for PV names
    private final ObservableList<String> pvNames = FXCollections.observableArrayList();

    public QueryPvsComponent() {
        // Load FXML and set this as controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/components/query-pvs-component.fxml"));
        fxmlLoader.setController(this);

        try {
            VBox root = fxmlLoader.load();
            this.getChildren().setAll(root.getChildren());
            this.setSpacing(root.getSpacing());
            this.getStyleClass().setAll(root.getStyleClass());
        } catch (IOException exception) {
            logger.error("Failed to load QueryPvsComponent FXML", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("QueryPvsComponent initialized");

        // Set up ListView with custom cell factory for remove buttons
        pvNamesListView.setItems(pvNames);
        pvNamesListView.setCellFactory(listView -> new PvNameListCell());

        logger.debug("QueryPvsComponent setup complete");
    }

    // Dependency injection methods
    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        refreshFromDpApplication();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Public API methods
    public void addPvName(String pvName) {
        if (dpApplication != null) {
            dpApplication.addPvName(pvName);
            refreshFromDpApplication();
        }
    }

    public void removePvName(String pvName) {
        if (dpApplication != null) {
            dpApplication.removePvName(pvName);
            refreshFromDpApplication();
        }
    }

    public ObservableList<String> getPvNames() {
        return pvNames;
    }

    public void refreshFromDpApplication() {
        if (dpApplication != null) {
            List<String> dpPvNames = dpApplication.getPvNames();
            pvNames.clear();
            if (dpPvNames != null) {
                pvNames.addAll(dpPvNames);
            }
            logger.debug("QueryPvsComponent refreshed with {} PV names", pvNames.size());
        }
    }

    @FXML
    private void onEditQuery() {
        if (mainController != null) {
            logger.debug("Navigating to Data Explorer Query Editor");
            mainController.switchToView("/fxml/data-explore.fxml");
        }
    }

    // Custom ListCell for PV names with remove buttons
    private class PvNameListCell extends ListCell<String> {
        private HBox content;
        private Label pvNameLabel;
        private Button removeButton;

        public PvNameListCell() {
            super();
            content = new HBox();
            content.setSpacing(5);
            content.setPadding(new Insets(2, 5, 2, 5));
            
            pvNameLabel = new Label();
            pvNameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(pvNameLabel, Priority.ALWAYS);
            
            removeButton = new Button("🗑️");
            removeButton.getStyleClass().addAll("btn", "btn-danger", "btn-xs");
            removeButton.setOnAction(e -> {
                String pvName = getItem();
                if (pvName != null) {
                    removePvName(pvName);
                }
            });
            
            content.getChildren().addAll(pvNameLabel, removeButton);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || item == null) {
                setGraphic(null);
            } else {
                pvNameLabel.setText(item);
                setGraphic(content);
            }
        }
    }
}