package com.logistics.service.impl;

import com.logistics.dto.CustomerDTO;
import com.logistics.entity.Customer;
import com.logistics.repository.CustomerRepository;
import com.logistics.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDTO create(CustomerDTO customerDTO) {
        if (customerRepository.existsByCustomerCodeAndDeletedFalse(customerDTO.getCustomerCode())) {
            throw new RuntimeException("客户编码已存在");
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        customer = customerRepository.save(customer);
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    @Override
    @Transactional
    public CustomerDTO update(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        
        // 检查客户编码是否重复（排除自身）
        if (!customer.getCustomerCode().equals(customerDTO.getCustomerCode()) &&
            customerRepository.existsByCustomerCodeAndDeletedFalse(customerDTO.getCustomerCode())) {
            throw new RuntimeException("客户编码已存在");
        }

        BeanUtils.copyProperties(customerDTO, customer, "id", "createTime", "deleted");
        customer = customerRepository.save(customer);
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    @Override
    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    @Override
    public Page<CustomerDTO> findAll(String keyword, Pageable pageable) {
        Specification<Customer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword + "%";
                predicates.add(cb.or(
                    cb.like(root.get("customerName"), likeKeyword),
                    cb.like(root.get("customerCode"), likeKeyword),
                    cb.like(root.get("contactName"), likeKeyword),
                    cb.like(root.get("contactPhone"), likeKeyword)
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return customerRepository.findAll(spec, pageable)
                .map(customer -> {
                    CustomerDTO dto = new CustomerDTO();
                    BeanUtils.copyProperties(customer, dto);
                    return dto;
                });
    }

    @Override
    public boolean existsByCustomerCode(String customerCode) {
        return customerRepository.existsByCustomerCodeAndDeletedFalse(customerCode);
    }

    @Override
    @Transactional
    public List<CustomerDTO> createBatch(List<CustomerDTO> customerDTOs) {
        List<Customer> customers = customerDTOs.stream()
                .map(dto -> {
                    if (customerRepository.existsByCustomerCodeAndDeletedFalse(dto.getCustomerCode())) {
                        throw new RuntimeException("客户编码已存在: " + dto.getCustomerCode());
                    }
                    Customer customer = new Customer();
                    BeanUtils.copyProperties(dto, customer);
                    return customer;
                })
                .collect(Collectors.toList());

        customers = customerRepository.saveAll(customers);
        
        return customers.stream()
                .map(customer -> {
                    CustomerDTO dto = new CustomerDTO();
                    BeanUtils.copyProperties(customer, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        List<Customer> customers = customerRepository.findAllById(ids);
        customers.forEach(customer -> customer.setDeleted(true));
        customerRepository.saveAll(customers);
    }

    @Override
    public Page<CustomerDTO> findByConditions(String customerName, String customerCode,
                                            String contactName, String contactPhone,
                                            Integer status, Pageable pageable) {
        Specification<Customer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (StringUtils.hasText(customerName)) {
                predicates.add(cb.like(root.get("customerName"), "%" + customerName + "%"));
            }
            if (StringUtils.hasText(customerCode)) {
                predicates.add(cb.like(root.get("customerCode"), "%" + customerCode + "%"));
            }
            if (StringUtils.hasText(contactName)) {
                predicates.add(cb.like(root.get("contactName"), "%" + contactName + "%"));
            }
            if (StringUtils.hasText(contactPhone)) {
                predicates.add(cb.like(root.get("contactPhone"), "%" + contactPhone + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return customerRepository.findAll(spec, pageable)
                .map(customer -> {
                    CustomerDTO dto = new CustomerDTO();
                    BeanUtils.copyProperties(customer, dto);
                    return dto;
                });
    }

    @Override
    @Transactional
    public CustomerDTO updateStatus(Long id, Integer status) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        customer.setStatus(status);
        customer = customerRepository.save(customer);
        
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    @Override
    public List<CustomerDTO> exportCustomers(String keyword) {
        Specification<Customer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword + "%";
                predicates.add(cb.or(
                    cb.like(root.get("customerName"), likeKeyword),
                    cb.like(root.get("customerCode"), likeKeyword),
                    cb.like(root.get("contactName"), likeKeyword),
                    cb.like(root.get("contactPhone"), likeKeyword)
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return customerRepository.findAll(spec).stream()
                .map(customer -> {
                    CustomerDTO dto = new CustomerDTO();
                    BeanUtils.copyProperties(customer, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 