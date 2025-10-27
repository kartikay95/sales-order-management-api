package com.kartikay.sales_order_management_api.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record OrderRequestDTO(
        @NotBlank String customerName,
        @Size(min = 1, message = "Order must have at least one item")
        List<ItemRequest> items
) {
    public record ItemRequest(
            @NotNull Long catalogItemId,
            @Min(1) int quantity
    ) {}
}

