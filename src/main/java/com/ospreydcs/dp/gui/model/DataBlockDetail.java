package com.ospreydcs.dp.gui.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Model class representing a data block in the Dataset Builder.
 * Each data block contains a list of PV names and a time range.
 */
public class DataBlockDetail {
    
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private List<String> pvNames;
    private Instant beginTime;
    private Instant endTime;
    
    public DataBlockDetail() {
    }
    
    public DataBlockDetail(List<String> pvNames, Instant beginTime, Instant endTime) {
        this.pvNames = pvNames;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public List<String> getPvNames() {
        return pvNames;
    }
    
    public void setPvNames(List<String> pvNames) {
        this.pvNames = pvNames;
    }
    
    public Instant getBeginTime() {
        return beginTime;
    }
    
    public void setBeginTime(Instant beginTime) {
        this.beginTime = beginTime;
    }
    
    public Instant getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
    
    /**
     * Returns a formatted display string for this data block.
     * Format: "pv-1, pv-2, pv-3: 2025-08-15 11:03:00 -> 2025-08-15 11:05:00"
     */
    @Override
    public String toString() {
        if (pvNames == null || pvNames.isEmpty()) {
            return "No PVs selected";
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Format PV names as comma-separated list
        sb.append(String.join(", ", pvNames));
        sb.append(": ");
        
        // Format time range
        if (beginTime != null && endTime != null) {
            LocalDateTime beginDateTime = LocalDateTime.ofInstant(beginTime, ZoneId.systemDefault());
            LocalDateTime endDateTime = LocalDateTime.ofInstant(endTime, ZoneId.systemDefault());
            
            sb.append(beginDateTime.format(DISPLAY_FORMATTER));
            sb.append(" -> ");
            sb.append(endDateTime.format(DISPLAY_FORMATTER));
        } else {
            sb.append("No time range specified");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataBlockDetail that = (DataBlockDetail) o;
        return Objects.equals(pvNames, that.pvNames) &&
               Objects.equals(beginTime, that.beginTime) &&
               Objects.equals(endTime, that.endTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pvNames, beginTime, endTime);
    }
}