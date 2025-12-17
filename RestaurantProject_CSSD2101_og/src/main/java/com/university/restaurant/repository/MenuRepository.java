package com.university.restaurant.repository;

import com.university.restaurant.model.menu.MenuCategory;
import com.university.restaurant.model.menu.MenuItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface MenuRepository {
    Optional<MenuItem> findById(String id);
    List<MenuItem> findByCategory(MenuCategory category);
    List<MenuItem> search(Predicate<MenuItem> filter);
    void save(MenuItem item);
}
