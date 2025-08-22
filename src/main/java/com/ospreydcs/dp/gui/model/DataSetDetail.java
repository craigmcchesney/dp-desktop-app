package com.ospreydcs.dp.gui.model;

import java.util.List;
import java.util.Objects;

/**
 * Model class representing a dataset in the Annotation Builder.
 * Each dataset contains an ID, name, description, and list of data blocks.
 */
public class DataSetDetail {
    
    private String id;
    private String name;
    private String description;
    private List<DataBlockDetail> dataBlocks;
    
    public DataSetDetail() {
    }
    
    public DataSetDetail(String id, String name, String description, List<DataBlockDetail> dataBlocks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dataBlocks = dataBlocks;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<DataBlockDetail> getDataBlocks() {
        return dataBlocks;
    }
    
    public void setDataBlocks(List<DataBlockDetail> dataBlocks) {
        this.dataBlocks = dataBlocks;
    }
    
    /**
     * Returns a formatted display string for this dataset.
     * Format: "Dataset name - Description snippet - First data block display string"
     * As specified in SPECIFICATIONS.md section 9.1.4
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Dataset name
        if (name != null && !name.trim().isEmpty()) {
            sb.append(name);
        } else {
            sb.append("Unnamed Dataset");
        }
        
        sb.append(" - ");
        
        // Description snippet (first 30 characters)
        if (description != null && !description.trim().isEmpty()) {
            String descSnippet = description.trim();
            if (descSnippet.length() > 30) {
                descSnippet = descSnippet.substring(0, 30) + "...";
            }
            sb.append(descSnippet);
        } else {
            sb.append("No description");
        }
        
        sb.append(" - ");
        
        // First data block display string
        if (dataBlocks != null && !dataBlocks.isEmpty()) {
            sb.append(dataBlocks.get(0).toString());
        } else {
            sb.append("No data blocks");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSetDetail that = (DataSetDetail) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(dataBlocks, that.dataBlocks);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, dataBlocks);
    }
}