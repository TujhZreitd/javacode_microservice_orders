package ru.javacode.ordersmicroservice.service;

import ru.javacode.ordersmicroservice.service.dto.CreateOrderDto;

import java.util.concurrent.ExecutionException;

public interface OrderService {
    String createOrder(CreateOrderDto createOrderDto) throws ExecutionException, InterruptedException;
}
