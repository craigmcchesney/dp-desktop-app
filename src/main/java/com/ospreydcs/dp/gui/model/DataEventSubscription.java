package com.ospreydcs.dp.gui.model;

import com.ospreydcs.dp.client.IngestionStreamClient;

public class DataEventSubscription {

    public final SubscribeDataEventDetail subscriptionDetail;
    public final IngestionStreamClient.SubscribeDataEventCall subscribeDataEventCall;

    public DataEventSubscription(
            SubscribeDataEventDetail subscriptionDetail,
            IngestionStreamClient.SubscribeDataEventCall subscribeDataEventCall
    ) {
        this.subscriptionDetail = subscriptionDetail;
        this.subscribeDataEventCall = subscribeDataEventCall;
    }

    /**
     * Returns display string for ListView presentation using the same format as SubscriptionDetailsComponent.
     */
    public String getDisplayString() {
        return subscriptionDetail.getDisplayString();
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

}
