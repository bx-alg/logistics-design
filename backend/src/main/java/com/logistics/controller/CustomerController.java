package com.logistics.controller;

import com.logistics.dto.CustomerDTO;
import com.logistics.service.CustomerService;
import com.logistics.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public Result<CustomerDTO> create(@Valid @RequestBody CustomerDTO customerDTO) {
        return Result.success(customerService.create(customerDTO));
    }

    @PutMapping("/{id}")
    public Result<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        return Result.success(customerService.update(id, customerDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CustomerDTO> findById(@PathVariable Long id) {
        return Result.success(customerService.findById(id));
    }

    @GetMapping
    public Result<Page<CustomerDTO>> findAll(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return Result.success(customerService.findAll(keyword, pageable));
    }

    @GetMapping("/check-code")
    public Result<Boolean> checkCustomerCode(@RequestParam String customerCode) {
        return Result.success(customerService.existsByCustomerCode(customerCode));
    }

    @PostMapping("/batch")
    public Result<List<CustomerDTO>> createBatch(@Valid @RequestBody List<CustomerDTO> customerDTOs) {
        return Result.success(customerService.createBatch(customerDTOs));
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        customerService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/search")
    public Result<Page<CustomerDTO>> search(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) Integer status,
            Pageable pageable) {
        return Result.success(customerService.findByConditions(
            customerName, customerCode, contactName, contactPhone, status, pageable));
    }

    @PutMapping("/{id}/status")
    public Result<CustomerDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return Result.success(customerService.updateStatus(id, status));
    }

    @GetMapping("/export")
    public Result<List<CustomerDTO>> export(@RequestParam(required = false) String keyword) {
        return Result.success(customerService.exportCustomers(keyword));
    }
} 