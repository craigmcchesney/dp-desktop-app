package com.ospreydcs.dp.gui.model;

import java.util.List;
import java.util.Objects;

/**
 * Model class representing calculations details for an annotation.
 * Contains an ID and a list of data frames with calculation results.
 */
public class CalculationsDetails {
    
    private String id;
    private List<DataFrameDetails> dataFrames;
    
    public CalculationsDetails() {
    }
    
    public CalculationsDetails(String id, List<DataFrameDetails> dataFrames) {
        this.id = id;
        this.dataFrames = dataFrames;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<DataFrameDetails> getDataFrames() {
        return dataFrames;
    }
    
    public void setDataFrames(List<DataFrameDetails> dataFrames) {
        this.dataFrames = dataFrames;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationsDetails that = (CalculationsDetails) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(dataFrames, that.dataFrames);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, dataFrames);
    }
    
    @Override
    public String toString() {
        return "CalculationsDetails{" +
                "id='" + id + '\'' +
                ", dataFrames=" + (dataFrames != null ? dataFrames.size() + " frames" : "null") +
                '}';
    }
}