package com.ospreydcs.dp.gui.model;

import com.ospreydcs.dp.grpc.v1.annotation.DataSet;
import com.ospreydcs.dp.grpc.v1.annotation.DataBlock;
import com.ospreydcs.dp.grpc.v1.common.Timestamp;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Table row wrapper for DataSet protobuf objects.
 * Provides JavaFX property binding support for TableView display in dataset-explore view.
 */
public class DatasetInfoTableRow {
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final DataSet dataSet;
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty owner = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty dataBlocks = new SimpleStringProperty();
    
    public DatasetInfoTableRow(DataSet dataSet) {
        this.dataSet = dataSet;
        
        // Set property values from DataSet
        this.id.set(dataSet.getId());
        this.name.set(dataSet.getName());
        this.owner.set(dataSet.getOwnerId());
        this.description.set(dataSet.getDescription());
        
        // Format data blocks as comma-separated human-readable strings
        if (!dataSet.getDataBlocksList().isEmpty()) {
            String dataBlocksStr = dataSet.getDataBlocksList().stream()
                .map(this::formatDataBlock)
                .collect(Collectors.joining(", "));
            this.dataBlocks.set(dataBlocksStr);
        } else {
            this.dataBlocks.set("");
        }
    }
    
    /**
     * Format a single data block as: "[pv-1, pv-2, pv-3: 2025-09-03 00:00:00 -> 2025-09-03 00:10:00]"
     */
    private String formatDataBlock(DataBlock dataBlock) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        // Format PV names as comma-separated list
        if (!dataBlock.getPvNamesList().isEmpty()) {
            sb.append(String.join(", ", dataBlock.getPvNamesList()));
        } else {
            sb.append("No PVs");
        }
        sb.append(": ");
        
        // Format time range
        if (dataBlock.hasBeginTime() && dataBlock.hasEndTime()) {
            LocalDateTime beginDateTime = convertTimestampToLocalDateTime(dataBlock.getBeginTime());
            LocalDateTime endDateTime = convertTimestampToLocalDateTime(dataBlock.getEndTime());
            
            sb.append(beginDateTime.format(TIMESTAMP_FORMATTER));
            sb.append(" -> ");
            sb.append(endDateTime.format(TIMESTAMP_FORMATTER));
        } else {
            sb.append("No time range");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Convert protobuf Timestamp to LocalDateTime using system default timezone.
     */
    private LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(
            timestamp.getEpochSeconds(),
            timestamp.getNanoseconds()
        );
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    
    // Getters for the original DataSet
    public DataSet getDataSet() {
        return dataSet;
    }
    
    // JavaFX Property getters for TableView binding
    public StringProperty idProperty() { return id; }
    public String getId() { return id.get(); }
    
    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    
    public StringProperty ownerProperty() { return owner; }
    public String getOwner() { return owner.get(); }
    
    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    
    public StringProperty dataBlocksProperty() { return dataBlocks; }
    public String getDataBlocks() { return dataBlocks.get(); }
    
    @Override
    public String toString() {
        return String.format("Dataset[id=%s, name=%s, blocks=%d]", 
            getId(), getName(), dataSet.getDataBlocksList().size());
    }
}