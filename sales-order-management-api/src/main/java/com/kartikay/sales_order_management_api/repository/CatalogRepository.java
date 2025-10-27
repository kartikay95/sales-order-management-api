package com.kartikay.sales_order_management_api.repository;


import com.kartikay.sales_order_management_api.domain.CatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CatalogRepository extends JpaRepository<CatalogItem, Long> {
    Optional<CatalogItem> findByNameIgnoreCase(String name);
}

