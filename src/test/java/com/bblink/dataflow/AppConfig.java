package com.bblink.dataflow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lidl on 12/21/15.
 */
@Configuration
public class AppConfig {
    @Bean
    public KafkaProducer getSampleService() {
        return new KafkaProducerImpl();
    }
}
