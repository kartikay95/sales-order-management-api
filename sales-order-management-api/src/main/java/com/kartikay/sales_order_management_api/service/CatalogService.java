package com.kartikay.sales_order_management_api.service;

import com.kartikay.sales_order_management_api.domain.CatalogItem;
import com.kartikay.sales_order_management_api.exception.ResourceNotFoundException;
import com.kartikay.sales_order_management_api.repository.CatalogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<CatalogItem> getAllItems() {
        return catalogRepository.findAll();
    }

    @Transactional
    public CatalogItem updatePrice(Long id, BigDecimal newPrice) {
        CatalogItem item = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog item not found with id: " + id));

        item.setPrice(newPrice);
        return catalogRepository.save(item);
    }

    public CatalogItem getItemById(Long id) {
        return catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog item not found: " + id));
    }

    public CatalogItem createItem(String name, BigDecimal price) {
        CatalogItem item = new CatalogItem();
        item.setName(name);
        item.setPrice(price);
        return catalogRepository.save(item);
    }

    public void deleteItem(Long id) {
        catalogRepository.deleteById(id);
    }

}
