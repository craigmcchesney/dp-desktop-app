package com.ospreydcs.dp.gui;

import com.ospreydcs.dp.gui.model.PvDetail;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class DataGenerationViewModel {

    private static final Logger logger = LogManager.getLogger();

    // Provider Details properties
    private final StringProperty providerName = new SimpleStringProperty();
    private final StringProperty providerDescription = new SimpleStringProperty();
    private final ObservableList<String> providerTags = FXCollections.observableArrayList();
    private final ObservableList<String> providerAttributes = FXCollections.observableArrayList();

    // Request Details properties
    private final ObjectProperty<LocalDate> dataBeginDate = new SimpleObjectProperty<>(LocalDate.now());
    private final IntegerProperty beginHour = new SimpleIntegerProperty(0);
    private final IntegerProperty beginMinute = new SimpleIntegerProperty(0);
    private final IntegerProperty beginSecond = new SimpleIntegerProperty(0);
    
    private final ObjectProperty<LocalDate> dataEndDate = new SimpleObjectProperty<>(LocalDate.now().plusDays(1));
    private final IntegerProperty endHour = new SimpleIntegerProperty(0);
    private final IntegerProperty endMinute = new SimpleIntegerProperty(0);
    private final IntegerProperty endSecond = new SimpleIntegerProperty(0);
    
    private final ObservableList<String> requestTags = FXCollections.observableArrayList();
    private final ObservableList<String> requestAttributes = FXCollections.observableArrayList();
    private final StringProperty eventName = new SimpleStringProperty();

    // PV Details properties
    private final ObservableList<PvDetail> pvDetails = FXCollections.observableArrayList();
    
    // Current PV entry properties
    private final StringProperty currentPvName = new SimpleStringProperty();
    private final StringProperty currentPvDataType = new SimpleStringProperty("integer");
    private final IntegerProperty currentPvSamplePeriod = new SimpleIntegerProperty(1000);
    private final StringProperty currentPvInitialValue = new SimpleStringProperty();
    private final StringProperty currentPvMaxStep = new SimpleStringProperty();

    // UI state properties
    private final BooleanProperty showPvEntryPanel = new SimpleBooleanProperty(false);
    
    // Status properties
    private final StringProperty statusMessage = new SimpleStringProperty("Ready to generate data");
    private final BooleanProperty isGenerating = new SimpleBooleanProperty(false);

    // Attribute key/value mappings
    private final Map<String, ObservableList<String>> providerAttributeOptions = new HashMap<>();
    private final Map<String, ObservableList<String>> requestAttributeOptions = new HashMap<>();

    private DpApplication dpApplication;

    public DataGenerationViewModel() {
        initializeAttributeOptions();
        logger.debug("DataGenerationViewModel initialized");
    }

    private void initializeAttributeOptions() {
        // Provider attribute options
        providerAttributeOptions.put("sector", FXCollections.observableArrayList("1", "2", "3", "4"));
        providerAttributeOptions.put("subsystem", FXCollections.observableArrayList("vacuum", "power", "RF", "mechanical"));
        
        // Request attribute options
        requestAttributeOptions.put("status", FXCollections.observableArrayList("normal", "abnormal"));
        requestAttributeOptions.put("mode", FXCollections.observableArrayList("live", "batch"));
    }

    public void setDpApplication(DpApplication dpApplication) {
        this.dpApplication = dpApplication;
        logger.debug("DpApplication injected into DataGenerationViewModel");
    }

    // Provider Details property getters
    public StringProperty providerNameProperty() { return providerName; }
    public StringProperty providerDescriptionProperty() { return providerDescription; }
    public ObservableList<String> getProviderTags() { return providerTags; }
    public ObservableList<String> getProviderAttributes() { return providerAttributes; }

    // Request Details property getters
    public ObjectProperty<LocalDate> dataBeginDateProperty() { return dataBeginDate; }
    public IntegerProperty beginHourProperty() { return beginHour; }
    public IntegerProperty beginMinuteProperty() { return beginMinute; }
    public IntegerProperty beginSecondProperty() { return beginSecond; }
    
    public ObjectProperty<LocalDate> dataEndDateProperty() { return dataEndDate; }
    public IntegerProperty endHourProperty() { return endHour; }
    public IntegerProperty endMinuteProperty() { return endMinute; }
    public IntegerProperty endSecondProperty() { return endSecond; }
    
    public ObservableList<String> getRequestTags() { return requestTags; }
    public ObservableList<String> getRequestAttributes() { return requestAttributes; }
    public StringProperty eventNameProperty() { return eventName; }

    // PV Details property getters
    public ObservableList<PvDetail> getPvDetails() { return pvDetails; }
    public StringProperty currentPvNameProperty() { return currentPvName; }
    public StringProperty currentPvDataTypeProperty() { return currentPvDataType; }
    public IntegerProperty currentPvSamplePeriodProperty() { return currentPvSamplePeriod; }
    public StringProperty currentPvInitialValueProperty() { return currentPvInitialValue; }
    public StringProperty currentPvMaxStepProperty() { return currentPvMaxStep; }

    // UI state property getters
    public BooleanProperty showPvEntryPanelProperty() { return showPvEntryPanel; }

    // Status property getters
    public StringProperty statusMessageProperty() { return statusMessage; }
    public BooleanProperty isGeneratingProperty() { return isGenerating; }

    // Attribute options getters
    public ObservableList<String> getProviderAttributeValues(String key) {
        return providerAttributeOptions.getOrDefault(key, FXCollections.observableArrayList());
    }

    public ObservableList<String> getRequestAttributeValues(String key) {
        return requestAttributeOptions.getOrDefault(key, FXCollections.observableArrayList());
    }

    // Business logic methods
    public void addProviderTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !providerTags.contains(tag)) {
            providerTags.add(tag);
            logger.debug("Added provider tag: {}", tag);
        }
    }

    public void removeProviderTag(String tag) {
        providerTags.remove(tag);
        logger.debug("Removed provider tag: {}", tag);
    }

    public void addProviderAttribute(String key, String value) {
        if (key != null && value != null && !key.trim().isEmpty() && !value.trim().isEmpty()) {
            String attribute = key + ": " + value;
            if (!providerAttributes.contains(attribute)) {
                providerAttributes.add(attribute);
                logger.debug("Added provider attribute: {}", attribute);
            }
        }
    }

    public void removeProviderAttribute(String attribute) {
        providerAttributes.remove(attribute);
        logger.debug("Removed provider attribute: {}", attribute);
    }

    public void addRequestTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !requestTags.contains(tag)) {
            requestTags.add(tag);
            logger.debug("Added request tag: {}", tag);
        }
    }

    public void removeRequestTag(String tag) {
        requestTags.remove(tag);
        logger.debug("Removed request tag: {}", tag);
    }

    public void addRequestAttribute(String key, String value) {
        if (key != null && value != null && !key.trim().isEmpty() && !value.trim().isEmpty()) {
            String attribute = key + ": " + value;
            if (!requestAttributes.contains(attribute)) {
                requestAttributes.add(attribute);
                logger.debug("Added request attribute: {}", attribute);
            }
        }
    }

    public void removeRequestAttribute(String attribute) {
        requestAttributes.remove(attribute);
        logger.debug("Removed request attribute: {}", attribute);
    }

    public void showPvEntryPanel() {
        clearCurrentPvEntry();
        showPvEntryPanel.set(true);
        logger.debug("Showing PV entry panel");
    }

    public void hidePvEntryPanel() {
        showPvEntryPanel.set(false);
        logger.debug("Hiding PV entry panel");
    }

    public void addCurrentPvDetail() {
        if (isCurrentPvDetailValid()) {
            PvDetail pvDetail = new PvDetail(
                currentPvName.get(),
                currentPvDataType.get(),
                currentPvSamplePeriod.get(),
                currentPvInitialValue.get(),
                currentPvMaxStep.get()
            );
            
            if (!pvDetails.contains(pvDetail)) {
                pvDetails.add(pvDetail);
                logger.info("Added PV detail: {}", pvDetail.getPvName());
                hidePvEntryPanel();
            } else {
                logger.warn("PV with name {} already exists", pvDetail.getPvName());
                statusMessage.set("PV with this name already exists");
            }
        } else {
            logger.warn("Current PV detail is not valid");
            statusMessage.set("Please fill in all required PV fields");
        }
    }

    public void removePvDetail(PvDetail pvDetail) {
        pvDetails.remove(pvDetail);
        logger.info("Removed PV detail: {}", pvDetail.getPvName());
    }

    private boolean isCurrentPvDetailValid() {
        return currentPvName.get() != null && !currentPvName.get().trim().isEmpty() &&
               currentPvDataType.get() != null && !currentPvDataType.get().trim().isEmpty() &&
               currentPvInitialValue.get() != null && !currentPvInitialValue.get().trim().isEmpty() &&
               currentPvMaxStep.get() != null && !currentPvMaxStep.get().trim().isEmpty();
    }

    private void clearCurrentPvEntry() {
        currentPvName.set("");
        currentPvDataType.set("integer");
        currentPvSamplePeriod.set(1000);
        currentPvInitialValue.set("");
        currentPvMaxStep.set("");
    }

    public LocalDateTime getBeginDateTime() {
        LocalTime beginTime = LocalTime.of(beginHour.get(), beginMinute.get(), beginSecond.get());
        return LocalDateTime.of(dataBeginDate.get(), beginTime);
    }

    public LocalDateTime getEndDateTime() {
        LocalTime endTime = LocalTime.of(endHour.get(), endMinute.get(), endSecond.get());
        return LocalDateTime.of(dataEndDate.get(), endTime);
    }

    public void generateData() {
        if (!isFormValid()) {
            statusMessage.set("Please fill in all required fields");
            return;
        }

        isGenerating.set(true);
        statusMessage.set("Generating data...");
        
        try {
            // TODO: Implement data generation logic using dpApplication
            logger.info("Starting data generation for {} PVs", pvDetails.size());
            logger.info("Provider: {}", providerName.get());
            logger.info("Time range: {} to {}", getBeginDateTime(), getEndDateTime());
            
            // Placeholder for actual implementation
            statusMessage.set("Data generation will be implemented in next phase");
            
        } catch (Exception e) {
            logger.error("Error during data generation", e);
            statusMessage.set("Error during data generation: " + e.getMessage());
        } finally {
            isGenerating.set(false);
        }
    }

    private boolean isFormValid() {
        return providerName.get() != null && !providerName.get().trim().isEmpty() &&
               !pvDetails.isEmpty() &&
               dataBeginDate.get() != null &&
               dataEndDate.get() != null &&
               getBeginDateTime().isBefore(getEndDateTime());
    }

    public void cancel() {
        logger.info("Data generation cancelled by user");
        statusMessage.set("Operation cancelled");
    }

    public void updateStatus(String message) {
        statusMessage.set(message);
        logger.debug("Status updated: {}", message);
    }
}