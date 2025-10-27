package com.kartikay.sales_order_management_api.repository;

import com.kartikay.sales_order_management_api.domain.Order;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class OrderSpecification {

    public static Specification<Order> createdBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return null;
            return cb.between(root.get("creationDate"), start, end);
        };
    }

    public static Specification<Order> cancelledBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return null;
            return cb.between(root.get("cancellationDate"), start, end);
        };
    }

    public static Specification<Order> hasCustomerName(String customerName) {
        return (root, query, cb) -> {
            if (customerName == null || customerName.isBlank()) return null;
            return cb.like(cb.lower(root.get("customerName")), "%" + customerName.toLowerCase() + "%");
        };
    }
}
