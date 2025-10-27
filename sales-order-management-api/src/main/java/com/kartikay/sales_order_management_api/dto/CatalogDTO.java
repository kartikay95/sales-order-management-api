package com.kartikay.sales_order_management_api.dto;

import java.math.BigDecimal;

public record CatalogDTO(
        Long id,
        String name,
        BigDecimal price
) {}
