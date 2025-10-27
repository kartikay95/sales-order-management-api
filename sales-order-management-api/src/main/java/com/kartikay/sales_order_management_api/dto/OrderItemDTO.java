package com.kartikay.sales_order_management_api.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long catalogItemId,
        String itemName,
        BigDecimal unitPrice,
        int quantity
) {}


