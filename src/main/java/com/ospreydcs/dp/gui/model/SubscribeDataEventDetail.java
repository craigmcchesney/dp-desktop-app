package com.ospreydcs.dp.gui.model;

import com.ospreydcs.dp.gui.DpApplication;

public class SubscribeDataEventDetail {

    public final String pvName;
    public final DpApplication.TriggerCondition triggerCondition;
    public final String triggerValue;

    public SubscribeDataEventDetail(
            String pvName,
            DpApplication.TriggerCondition triggerCondition,
            String triggerValue
    ) {
        this.pvName = pvName;
        this.triggerCondition = triggerCondition;
        this.triggerValue = triggerValue;
    }
    
    /**
     * Returns a display string for ListView showing PV name, operator, and value.
     */
    public String getDisplayString() {
        String operatorText = switch (triggerCondition) {
            case EQUAL_TO -> "=";
            case GREATER -> ">";
            case GREATER_OR_EQUAL -> ">=";
            case LESS -> "<";
            case LESS_OR_EQUAL -> "<=";
        };
        return String.format("%s %s %s", pvName, operatorText, triggerValue);
    }
    
    @Override
    public String toString() {
        return getDisplayString();
    }
}
