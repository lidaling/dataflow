package com.bblink.dataflow;

import com.bblink.spring.ArchaiusPropertyPlaceholderConfigurer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by lidl on 12/21/15.
 */
public class KafkaProducerImpl implements KafkaProducer{

    private static Producer<Integer, String> producer;
    private static final Properties properties = new Properties();

    static {
        ArchaiusPropertyPlaceholderConfigurer config = new ArchaiusPropertyPlaceholderConfigurer();
//        config.setPrefix("snappy-rest");
        String brokers="";
        try {
            config.afterPropertiesSet();
            brokers = config.resolvePlaceholder("/brokers/ids", null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        properties.put("bootstrap.servers", brokers);
        properties.put("bootstrap.servers", "cdh-slave1:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new org.apache.kafka.clients.producer.KafkaProducer(properties);
    }

    public void sendMsg(TOPIC topic, Object dto) {
        Gson gson = new GsonBuilder().create();
        String msg=gson.toJson(dto);
        ProducerRecord<Integer, String> data = new ProducerRecord<Integer, String>(topic.getTopicName(), msg);
        producer.send(data);
        producer.close();
    }
}
