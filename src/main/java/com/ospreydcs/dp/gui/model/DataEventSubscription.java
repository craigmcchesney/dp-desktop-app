package com.ospreydcs.dp.gui.model;

import com.ospreydcs.dp.client.IngestionStreamClient;
import com.ospreydcs.dp.grpc.v1.ingestionstream.PvConditionTrigger;

public class DataEventSubscription {

    public final PvConditionTrigger trigger;
    public final IngestionStreamClient.SubscribeDataEventCall subscribeDataEventCall;

    public DataEventSubscription(
            PvConditionTrigger trigger,
            IngestionStreamClient.SubscribeDataEventCall subscribeDataEventCall
    ) {
        this.trigger = trigger;
        this.subscribeDataEventCall = subscribeDataEventCall;
    }

}
