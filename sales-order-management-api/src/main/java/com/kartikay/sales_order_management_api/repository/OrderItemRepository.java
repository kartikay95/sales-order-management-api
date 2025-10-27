package com.kartikay.sales_order_management_api.repository;

import com.kartikay.sales_order_management_api.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // mostly used internally, can extend with custom queries later
}
