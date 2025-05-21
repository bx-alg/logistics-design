package com.logistics.service;

import com.logistics.dto.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerDTO create(CustomerDTO customerDTO);
    CustomerDTO update(Long id, CustomerDTO customerDTO);
    void delete(Long id);
    CustomerDTO findById(Long id);
    Page<CustomerDTO> findAll(String keyword, Pageable pageable);
    boolean existsByCustomerCode(String customerCode);
    
    // 新增批量操作方法
    List<CustomerDTO> createBatch(List<CustomerDTO> customerDTOs);
    void deleteBatch(List<Long> ids);
    
    // 新增高级查询方法
    Page<CustomerDTO> findByConditions(String customerName, String customerCode, 
                                     String contactName, String contactPhone, 
                                     Integer status, Pageable pageable);
    
    // 新增状态更新方法
    CustomerDTO updateStatus(Long id, Integer status);
    
    // 新增导出方法
    List<CustomerDTO> exportCustomers(String keyword);
} 