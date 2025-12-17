package com.university.restaurant.infrastructure.adapter;

import com.university.restaurant.infrastructure.entity.AuditLogEntity;
import com.university.restaurant.infrastructure.jpa.AuditLogJpaRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JPA Adapter implementing RestaurantAuditLogRepository port.
 * Bridges Alice's audit log repository with Spring Data JPA.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class AuditLogJpaAdapter implements RestaurantAuditLogRepository {

    private final AuditLogJpaRepository jpaRepo;

    public AuditLogJpaAdapter(AuditLogJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public void append(RestaurantAuditEntry entry) {
        // Use getters instead of direct field access
        AuditLogEntity entity = new AuditLogEntity(
                entry.getUserId(),
                entry.getRole(),
                entry.getAction(),
                entry.getEntityType(),
                entry.getEntityId(),
                entry.getDetails(),
                entry.getPrevHash(),
                entry.getHash()
        );
        jpaRepo.save(entity);
    }

    @Override
    public List<RestaurantAuditEntry> all() {
        // Note: Cannot fully reconstruct RestaurantAuditEntry from entity
        // because RestaurantAuditEntry fields are final
        throw new UnsupportedOperationException(
                "Cannot retrieve audit entries due to RestaurantAuditEntry design. " +
                        "Consider using entity directly or creating a DTO."
        );
    }

    @Override
    public boolean verifyChain() {
        List<AuditLogEntity> logs = jpaRepo.findAllByOrderByTimestampDesc();

        for (int i = 1; i < logs.size(); i++) {
            AuditLogEntity current = logs.get(i);
            AuditLogEntity previous = logs.get(i - 1);

            if (!current.getPrevHash().equals(previous.getHash())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String tailHash() {
        AuditLogEntity mostRecent = jpaRepo.findMostRecent();
        return mostRecent != null ? mostRecent.getHash() : "GENESIS";
    }
}



























