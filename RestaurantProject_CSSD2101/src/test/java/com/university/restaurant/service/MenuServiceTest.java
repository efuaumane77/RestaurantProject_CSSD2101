package com.university.restaurant.service;

import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepo;

    @Mock
    private RestaurantAuditLogRepository auditRepo;

    private MenuService service;
    private Manager manager;
    private Waiter waiter;

    @BeforeEach
    void setUp() {
        service = new MenuService(menuRepo, auditRepo);
        manager = new Manager("m1", "Alice");
        waiter = new Waiter("w1", "Bob");
        when(auditRepo.tailHash()).thenReturn("GENESIS");
    }

    @Test
    void addMenuItem_withManagerRole_shouldSucceed() {
        MenuItem item = new Drink("d1", "Coke", "Soft drink", 2.99, false);

        service.addMenuItem(manager, item);

        verify(menuRepo).save(item);
        verify(auditRepo).append(any());
    }

    @Test
    void addMenuItem_withWaiterRole_shouldThrowSecurityException() {
        MenuItem item = new Drink("d1", "Coke", "Soft drink", 2.99, false);

        assertThrows(SecurityException.class, () -> {
            service.addMenuItem(waiter, item);
        });
        verify(menuRepo, never()).save(any());
    }

    @Test
    void updatePrice_withManagerRole_shouldSucceed() {
        MenuItem oldItem = new Drink("d1", "Coke", "desc", 2.99, false);
        when(menuRepo.findById("d1")).thenReturn(Optional.of(oldItem));

        service.updatePrice(manager, "d1", 3.49);

        verify(menuRepo).save(any(MenuItem.class));
        verify(auditRepo).append(any());
    }

    @Test
    void updatePrice_nonExistentItem_shouldThrowException() {
        when(menuRepo.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.updatePrice(manager, "invalid", 5.99);
        });
    }

    @Test
    void updatePrice_withWaiterRole_shouldThrowSecurityException() {
        assertThrows(SecurityException.class, () -> {
            service.updatePrice(waiter, "d1", 3.49);
        });
        verify(menuRepo, never()).save(any());
    }

    @Test
    void listMenuAvailableItems_shouldReturnAvailableItems() {
        MenuItem item1 = new Drink("d1", "Coke", "desc", 2.99, false);
        MenuItem item2 = new Drink("d2", "Sprite", "desc", 2.99, false);
        item2.setAvailable(false);

        when(menuRepo.search(any())).thenReturn(List.of(item1));

        List<MenuItem> available = service.listMenuAvailableItems();

        assertEquals(1, available.size());
        assertTrue(available.get(0).isAvailable());
    }
}
