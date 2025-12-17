package com.university.restaurant.infrastructure.adapter;

import com.university.restaurant.infrastructure.adapter.mapper.InventoryMapper;
import com.university.restaurant.infrastructure.jpa.InventoryJpaRepository;
import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.inventory.StockStatus;
import com.university.restaurant.repository.InventoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA Adapter implementing InventoryRepository port.
 * Bridges Alice's InventoryRepository interface with Spring Data JPA.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class InventoryJpaAdapter implements InventoryRepository {

    private final InventoryJpaRepository jpaRepo;
    private final InventoryMapper mapper;

    public InventoryJpaAdapter(InventoryJpaRepository jpaRepo, InventoryMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<InventoryItem> findById(String id) {
        return jpaRepo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<InventoryItem> findByName(String name) {
        return jpaRepo.findByNameIgnoreCase(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<InventoryItem> findByStatus(StockStatus status) {
        // Filter based on computed status
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .filter(item -> item.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void save(InventoryItem item) {
        jpaRepo.save(mapper.toEntity(item));
    }
}