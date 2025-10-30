package com.kartikay.sales_order_management_api.service;

import com.kartikay.sales_order_management_api.domain.CatalogItem;
import com.kartikay.sales_order_management_api.domain.Order;
import com.kartikay.sales_order_management_api.domain.OrderItem;
import com.kartikay.sales_order_management_api.exception.InvalidOperationException;
import com.kartikay.sales_order_management_api.exception.ResourceNotFoundException;
import com.kartikay.sales_order_management_api.repository.CatalogRepository;
import com.kartikay.sales_order_management_api.repository.OrderRepository;
import com.kartikay.sales_order_management_api.repository.OrderSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogRepository catalogRepository;

    // Maintain single point for tax logic
    private static final BigDecimal VAT_RATE = new BigDecimal("0.12"); // 12% VAT

    public OrderService(OrderRepository orderRepository, CatalogRepository catalogRepository) {
        this.orderRepository = orderRepository;
        this.catalogRepository = catalogRepository;
    }

    /**
     * Create a new order for a customer
     */
    @Transactional
    public Order createOrder(String customerName, List<OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOperationException("Order must have at least one item");
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCreationDate(LocalDate.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest request : items) {
            CatalogItem catalogItem = catalogRepository.findById(request.catalogItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catalog item not found: " + request.catalogItemId()));

            BigDecimal price = catalogItem.getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(request.quantity()));
            subtotal = subtotal.add(lineTotal);

            // Build order item
            OrderItem orderItem = new OrderItem();
            orderItem.setCatalogItemId(catalogItem.getId());
            orderItem.setItemName(catalogItem.getName());
            orderItem.setUnitPrice(price);
            orderItem.setQuantity(request.quantity());
            order.addItem(orderItem);
        }

        // Calculate tax + total
        BigDecimal vat = subtotal.multiply(VAT_RATE);
        BigDecimal total = subtotal.add(vat);

        order.setSubtotal(subtotal);
        order.setVat(vat);
        order.setTotal(total);

        return orderRepository.save(order);
    }

    /**
     * Cancel an existing order
     */
    @Transactional
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getCancellationDate() != null) {
            throw new InvalidOperationException("Order is already cancelled");
        }

        order.setCancellationDate(LocalDate.now());
        return orderRepository.save(order);
    }

    /**
     * List all orders with filters (customerName, date range, pagination)
     */
    @Transactional(readOnly = true)
    public Page<Order> listOrders(String customerName, LocalDate start, LocalDate end, Pageable pageable) {
        Specification<Order> spec = Specification
                .where(OrderSpecification.hasCustomerName(customerName))
                .and(OrderSpecification.createdBetween(start, end));

        Page<Order> orders = orderRepository.findAll(spec, pageable);

        orders.forEach(o -> o.getItems().size());
        return orders;
    }

    /**
     * Fetch an order by ID (with items)
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    /**
     * Delete an order (Admin only)
     */
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    // DTO for request items
    public record OrderItemRequest(Long catalogItemId, int quantity) {}
}
