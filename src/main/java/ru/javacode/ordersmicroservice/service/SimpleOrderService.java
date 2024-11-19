package ru.javacode.ordersmicroservice.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.javacode.core.OrderCreatedEvent;
import ru.javacode.ordersmicroservice.service.dto.CreateOrderDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@KafkaListener(topics = "result-orders-topic")
public class SimpleOrderService implements OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentHashMap<String, CompletableFuture<OrderCreatedEvent>> responseMap = new ConcurrentHashMap<>();

    @Override
    public String createOrder(CreateOrderDto createOrderDto) throws ExecutionException, InterruptedException {
        //TODO save DB
        String orderId = UUID.randomUUID().toString();
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderId,
                createOrderDto.getNameProduct(),
                createOrderDto.getPrice(),
                createOrderDto.getQuantity(),
                false,
                false
        );
        CompletableFuture<OrderCreatedEvent> future = new CompletableFuture<>();
        responseMap.put(orderId, future);
        SendResult<String, Object> result = kafkaTemplate.send("new-orders-topic", orderId, orderCreatedEvent).get();

        LOGGER.info("Topic: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset: {}", result.getRecordMetadata().offset());

        try {
            orderCreatedEvent = future.get();
        } finally {
            responseMap.remove(orderId);
        }
        LOGGER.info("Return: {}", orderId);
        return "Order isPayment = " + orderCreatedEvent.isPayment() + " , order is shipping = " + orderCreatedEvent.isShipping();
    }

    @KafkaHandler
    public void handle(OrderCreatedEvent orderCreatedEvent) throws ExecutionException, InterruptedException {
        CompletableFuture<OrderCreatedEvent> future = responseMap.get(orderCreatedEvent.getProductId());
        if (future != null) {
            future.complete(orderCreatedEvent);
        }
    }
}
