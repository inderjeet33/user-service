package com.prerana.userservice.repository;

import com.prerana.userservice.entity.VolunteerAssignmentEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerAssignmentRepository
        extends JpaRepository<VolunteerAssignmentEntity, Long> {

    boolean existsByVolunteerRequest_IdAndStatusIn(
            Long requestId, List<AssignmentStatus> statuses
    );

    Optional<VolunteerAssignmentEntity> findTopByVolunteerRequest_IdAndStatusInOrderByCreatedAtDesc(Long id, List<AssignmentStatus> status);

    Optional<VolunteerAssignmentEntity>
    findFirstByVolunteerRequest_IdAndStatusIn(
            Long requestId,
            List<AssignmentStatus> statuses
    );

    @Query("SELECT  v FROM VolunteerAssignmentEntity v " +
            "WHERE v.receiver.id = :ngoUserId " +
            "ORDER BY v.createdAt DESC")
    List<VolunteerAssignmentEntity> findAllAssignmentsForNgo(@Param("ngoUserId") Long ngoUserId);


    Optional<VolunteerAssignmentEntity>
    findFirstByVolunteerRequest_IdAndStatus(
            Long requestId,
            AssignmentStatus status
    );
}
