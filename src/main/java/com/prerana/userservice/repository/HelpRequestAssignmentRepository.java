package com.prerana.userservice.repository;

import com.prerana.userservice.entity.HelpRequestAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HelpRequestAssignmentRepository extends JpaRepository<HelpRequestAssignmentEntity,Long> {
    Optional<HelpRequestAssignmentEntity> findTopByHelpRequest_IdOrderByCreatedAtDesc(Long id);

        @Query("""
        SELECT a FROM HelpRequestAssignmentEntity a
        WHERE a.helper.id = :helperId
          AND a.status IN ('ASSIGNED', 'IN_PROGRESS')
    """)
        List<HelpRequestAssignmentEntity> findActiveAssignmentsForHelper(
                @Param("helperId") Long helperId
        );


}
