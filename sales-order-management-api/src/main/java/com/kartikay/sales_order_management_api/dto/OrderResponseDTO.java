package com.kartikay.sales_order_management_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String customerName,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate creationDate,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate cancellationDate,
        BigDecimal subtotal,
        BigDecimal vat,
        BigDecimal total,
        List<OrderItemDTO> items
) {}

