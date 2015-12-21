package com.bblink.dataflow;

import org.springframework.stereotype.Component;

/**
 * Created by lidl on 12/21/15.
 */
@Component
public interface KafkaProducer{
    void sendMsg(TOPIC topic, Object dto);
}

