package com.prerana.userservice.repository;

import com.prerana.userservice.entity.HelpRequestAssignmentEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    List<HelpRequestAssignmentEntity> findByHelper_Id(Long helperId);

    Long countByHelper_IdAndStatus(Long helperId, AssignmentStatus status);

    long countByHelper_IdAndHelperType(Long helperId, UserType helperType);

    long countByHelper_IdAndHelperTypeAndStatus(
            Long helperId,
            UserType helperType,
            AssignmentStatus status
    );

    @Query("""
        select count(a)
        from HelpRequestAssignmentEntity a
        where a.helper.id = :helperId
          and a.createdAt >= :start
          and a.createdAt <= :end
    """)
    long countAssignmentsForHelperBetween(
            @Param("helperId") Long helperId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
