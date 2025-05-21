package com.logistics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.logistics.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    
    List<Customer> findByDeletedEquals(Boolean deleted);
    
    Customer findByIdAndDeletedEquals(Long id, Boolean deleted);

    Optional<Customer> findByCustomerCodeAndDeletedFalse(String customerCode);

    boolean existsByCustomerCodeAndDeletedFalse(String customerCode);
} 