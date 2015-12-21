package com.bblink.dataflow;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lidl on 12/21/15.
 */
public class KafkaProducerTest extends AbstractTest{

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void sendTest(){
        TestDto testDto=new TestDto();
        testDto.setName("lidl");
        testDto.setAge(30);
        testDto.setValid(true);

        kafkaProducer.sendMsg(TOPIC.LOGINFLOW,testDto);
    }
}
