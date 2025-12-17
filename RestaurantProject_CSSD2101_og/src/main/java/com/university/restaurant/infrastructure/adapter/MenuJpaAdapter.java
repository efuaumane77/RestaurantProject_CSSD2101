package com.university.restaurant.infrastructure.adapter;

import com.university.restaurant.infrastructure.adapter.mapper.MenuItemMapper;
import com.university.restaurant.infrastructure.entity.MenuItemEntity;
import com.university.restaurant.infrastructure.jpa.MenuJpaRepository;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.model.menu.MenuCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * JPA Adapter implementing MenuRepository port.
 * Bridges Alice's MenuRepository interface with Spring Data JPA.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class MenuJpaAdapter implements MenuRepository {

    private final MenuJpaRepository jpaRepo;
    private final MenuItemMapper mapper;

    public MenuJpaAdapter(MenuJpaRepository jpaRepo, MenuItemMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        return jpaRepo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<MenuItem> findByCategory(MenuCategory category) {
        return jpaRepo.findByCategory(category).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> search(Predicate<MenuItem> filter) {
        // Load all items and apply predicate filter
        // Note: This is not optimal for large datasets
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public void save(MenuItem item) {
        MenuItemEntity entity = mapper.toEntity(item);
        entity.setAvailable(item.isAvailable());
        jpaRepo.save(entity);
    }
}
