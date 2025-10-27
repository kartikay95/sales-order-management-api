package com.kartikay.sales_order_management_api.repository;

import com.kartikay.sales_order_management_api.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
