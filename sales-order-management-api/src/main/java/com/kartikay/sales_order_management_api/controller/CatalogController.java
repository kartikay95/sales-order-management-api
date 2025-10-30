package com.kartikay.sales_order_management_api.controller;

import com.kartikay.sales_order_management_api.domain.CatalogItem;
import com.kartikay.sales_order_management_api.dto.CatalogDTO;
import com.kartikay.sales_order_management_api.service.CatalogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // GET all items
    @GetMapping
    public List<CatalogDTO> getAll() {
        return catalogService.getAllItems()
                .stream()
                .map(item -> new CatalogDTO(item.getId(), item.getName(), item.getPrice()))
                .toList();
    }

    //GET single item by ID
    @GetMapping("/{id}")
    public CatalogDTO getById(@PathVariable Long id) {
        CatalogItem item = catalogService.getItemById(id);
        return new CatalogDTO(item.getId(), item.getName(), item.getPrice());
    }

    // CREATE new catalog item (ADMIN role)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CatalogDTO createItem(@Valid @RequestBody CatalogDTO dto) {
        CatalogItem saved = catalogService.createItem(dto.name(), dto.price());
        return new CatalogDTO(saved.getId(), saved.getName(), saved.getPrice());
    }

    // UPDATE price (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/price")
    public CatalogDTO updatePrice(
            @PathVariable Long id,
            @RequestParam @DecimalMin(value = "0.0", inclusive = false) BigDecimal newPrice) {

        CatalogItem updated = catalogService.updatePrice(id, newPrice);
        return new CatalogDTO(updated.getId(), updated.getName(), updated.getPrice());
    }

    // DELETE item (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        catalogService.deleteItem(id);
    }
}
