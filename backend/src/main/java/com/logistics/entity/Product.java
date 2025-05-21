package com.logistics.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    private String unit;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    private Integer status;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(nullable = false)
    private Integer deleted;
    
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.deleted = 0;
        if (this.status == null) {
            this.status = 1;
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 