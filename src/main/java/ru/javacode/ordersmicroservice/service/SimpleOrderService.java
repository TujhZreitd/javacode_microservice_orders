package ru.javacode.ordersmicroservice.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.javacode.core.OrderCreatedEvent;
import ru.javacode.ordersmicroservice.service.dto.CreateOrderDto;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class SimpleOrderService implements OrderService {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public String createOrder(CreateOrderDto createOrderDto) throws ExecutionException, InterruptedException {
        //TODO save DB
        String orderId = UUID.randomUUID().toString();
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderId,
                createOrderDto.getNameProduct(),
                createOrderDto.getPrice(),
                createOrderDto.getQuantity()
        );
        SendResult<String, OrderCreatedEvent> result = kafkaTemplate.send("new-orders-topic", orderId, orderCreatedEvent).get();

        LOGGER.info("Topic: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return: {}", orderId);
        return orderId;
    }
}
