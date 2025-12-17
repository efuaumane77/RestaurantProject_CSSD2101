package com.university.restaurant.repository;

import com.university.restaurant.model.menu.MenuCategory;
import com.university.restaurant.model.menu.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class InMemoryMenuRepo implements MenuRepository {
    private final Map<String, MenuItem> store = new HashMap<>();

    @Override
    public Optional<MenuItem> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<MenuItem> findByCategory(MenuCategory category) {
        return store.values().stream()
                .filter(item -> item.getCategory() == category)
                .toList();
    }

    @Override
    public List<MenuItem> search(Predicate<MenuItem> filter) {
        return store.values().stream().filter(filter).toList();
    }

    @Override
    public void save(MenuItem item) {
        store.put(item.getId(), item);
    }
}
