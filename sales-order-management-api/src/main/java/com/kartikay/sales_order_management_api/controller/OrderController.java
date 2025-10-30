package com.kartikay.sales_order_management_api.controller;

import com.kartikay.sales_order_management_api.domain.Order;
import com.kartikay.sales_order_management_api.dto.OrderItemDTO;
import com.kartikay.sales_order_management_api.dto.OrderRequestDTO;
import com.kartikay.sales_order_management_api.dto.OrderResponseDTO;
import com.kartikay.sales_order_management_api.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order
     * Accessible by USER or ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO request) {
        List<OrderService.OrderItemRequest> items = request.items().stream()
                .map(i -> new OrderService.OrderItemRequest(i.catalogItemId(), i.quantity()))
                .toList();

        Order order = orderService.createOrder(request.customerName(), items);
        return mapToResponse(order);
    }

    /**
     * List all orders (with optional filters, pagination, and sorting)
     * Accessible by USER or ADMIN
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<OrderResponseDTO> listOrders(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Order> orders = orderService.listOrders(customerName, start, end, pageable);
        return orders.map(this::mapToResponse);
    }

    /**
     * Get a single order by ID
     * Returns customer info, items, subtotal, VAT, and total
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {
        return mapToResponse(orderService.getOrderById(id));
    }

    /**
     * Cancel an order
     * Only ADMIN can cancel
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) {
        return mapToResponse(orderService.cancelOrder(id));
    }

    /**
     * Permanently delete an order (optional)
     * Only ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    // Utility mapper to convert domain → DTO
    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
                .map(i -> new OrderItemDTO(
                        i.getCatalogItemId(),
                        i.getItemName(),
                        i.getUnitPrice(),
                        i.getQuantity()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerName(),
                order.getCreationDate(),
                order.getCancellationDate(),
                order.getSubtotal(),
                order.getVat(),
                order.getTotal(),
                items
        );
    }
}
