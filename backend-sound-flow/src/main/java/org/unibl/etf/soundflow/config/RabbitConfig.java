package org.unibl.etf.soundflow.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "separationQueue";

    @Bean
    public Queue separationQueue() {
        return new Queue(QUEUE_NAME, true); // durable queue
    }
}
