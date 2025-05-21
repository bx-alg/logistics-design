package com.logistics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistics.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByDeletedEquals(Integer deleted);
    
    Product findByIdAndDeletedEquals(Long id, Integer deleted);
} 