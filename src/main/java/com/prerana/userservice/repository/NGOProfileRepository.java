package com.prerana.userservice.repository;

import com.prerana.userservice.enums.ActivationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

import com.prerana.userservice.entity.NGOProfileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NGOProfileRepository extends JpaRepository<NGOProfileEntity, Long> {
    Optional<NGOProfileEntity> findByUserId(Long userId);

    List<NGOProfileEntity> findByCityAndCategoriesContaining(String city, String categoryFragment); // simple filter

    List<NGOProfileEntity> findByCityContaining(String city); // simple filter

    List<NGOProfileEntity> findByCategoriesContaining(String categoryFragment); // simple filter

    // Optional filters with Spring Data JPA method query
    Page<NGOProfileEntity> findByCityContainingIgnoreCaseAndCategoriesContainingIgnoreCase(
            String city, String category, Pageable pageable);

    @Query("""
SELECT n
FROM NGOProfileEntity n
WHERE (:city IS NULL OR n.city ILIKE CONCAT('%', CAST(:city AS text), '%'))
AND (:state IS NULL OR n.state ILIKE CONCAT('%', CAST(:state AS text), '%'))
AND (:category IS NULL OR n.categories ILIKE CONCAT('%', CAST(:category AS text), '%'))
AND (:verified IS NULL OR n.activationStatus ILIKE CONCAT('%',CAST(:verified AS text),'%'))
ORDER BY n.createdAt DESC
""")
    Page<NGOProfileEntity> search(
            @Param("city") String city,
            @Param("state") String state,
            @Param("category") String category,
            @Param("verified") String verified,
            Pageable pageable
    );



    Optional<NGOProfileEntity> findByUser_id(Long userId);

    Long countByActivationStatus(ActivationStatus status);

    // for advanced dynamic filters, implement Specification-based queries
}