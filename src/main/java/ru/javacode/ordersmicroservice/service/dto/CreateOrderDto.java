package ru.javacode.ordersmicroservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    private String nameProduct;
    private BigDecimal price;
    private Integer quantity;
}
