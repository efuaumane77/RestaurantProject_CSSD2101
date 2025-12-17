package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for StaffEntity.
 * Provides CRUD operations and custom queries for staff members.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface StaffJpaRepository extends JpaRepository<StaffEntity, String> {

    /**
     * Find staff by role (MANAGER, WAITER, CHEF).
     */
    List<StaffEntity> findByRole(String role);

    /**
     * Find active staff members.
     */
    List<StaffEntity> findByActiveTrue();

    /**
     * Find active staff by role.
     */
    List<StaffEntity> findByRoleAndActiveTrue(String role);

    /**
     * Find staff by email.
     */
    Optional<StaffEntity> findByEmail(String email);

    /**
     * Find staff by name (case-insensitive, partial match).
     */
    @Query("SELECT s FROM StaffEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<StaffEntity> searchByName(String name);

    /**
     * Count active staff by role.
     */
    long countByRoleAndActiveTrue(String role);
}