package tko.rabbitmqpitfalls.pitfall1autoack;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Start the application and via RabbitMQ Web Interface send two messages to "autoack" exchange:
 * 1. properties: content_type text/plain
 * body: Tomasz
 * 2. properties: content_type text/plain
 * body Tomasz Kowalski
 */
@SpringBootApplication
public class AutoAckApplication {
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        //default prefetch count since 2.0.1 is 250, with default prefetch consumer won't be blocked
        //but messages might be processed out of order
        factory.setPrefetchCount(1);

        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    Queue autoackQueue() {
        return QueueBuilder.nonDurable("autoack").build();
    }

    @Bean
    Exchange autoackExchange() {
        return ExchangeBuilder.directExchange("autoack").build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(autoackQueue()).to(autoackExchange()).with("").noargs();
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(AutoAckApplication.class, args);
    }
}
