package com.prerana.userservice.repository;

import java.util.*;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.Assign;

public interface ModeratorAssignmentRepository extends JpaRepository<ModeratorAssignmentEntity, Long> {

    boolean existsByDonationRequest_IdAndStatusIn(
            Long donationRequestId,
            List<AssignmentStatus> statuses
    );

    List<ModeratorAssignmentEntity> findByDonationRequest_IdOrderByCreatedAtDesc(Long donationRequestId);

    Optional<ModeratorAssignmentEntity> findTopByDonationRequest_IdOrderByCreatedAtDesc(Long donationRequestId);
    List<ModeratorAssignmentEntity> findByReceiver_Id(Long receiverId);

    Long countByReceiver_Id(Long receiverId);

    Long countByReceiver_IdAndStatus(Long receiverId, AssignmentStatus status);

    boolean existsByDonationRequest_IdAndReceiver_Id(
            Long donationRequestId,
            Long receiverId
    );

    List<ModeratorAssignmentEntity> findByDonationRequest_IdIn(List<Long> donationRequestIds);


    List<ModeratorAssignmentEntity> findByDonationRequest_IdInAndStatusIn(
            List<Long> donationRequestId,
            List<AssignmentStatus> statuses
    );

    List<ModeratorAssignmentEntity> findByDonor_Id(Long donorId);

    List<ModeratorAssignmentEntity> findByModerator_IdOrderByCreatedAtDesc(Long moderatorId);

    Optional<ModeratorAssignmentEntity>
    findTopByDonationRequest_IdAndStatusInOrderByCreatedAtDesc(
            Long donationId,
            List<AssignmentStatus> status
    );
    ModeratorAssignmentEntity findByDonationRequest_IdAndStatusIn(Long donationRequestId, List<AssignmentStatus> status);
    public Page<ModeratorAssignmentEntity> findByStatus(AssignmentStatus status, Pageable pageable);

    @Query("SELECT m FROM ModeratorAssignmentEntity m " +
            "WHERE m.receiver.id = :ngoUserId " +
            "ORDER BY m.createdAt DESC")
    List<ModeratorAssignmentEntity> findAllAssignmentsForNgo(@Param("ngoUserId") Long ngoUserId);

    Optional<ModeratorAssignmentEntity> findFirstByDonationRequest_IdAndStatus(Long donatinoRequestId, AssignmentStatus status);

    ModeratorAssignmentEntity findByDonationRequest_IdAndStatus(Long donationRequestId, AssignmentStatus status);

        List<ModeratorAssignmentEntity> findAllByOrderByCreatedAtDesc();

    Optional<ModeratorAssignmentEntity> findFirstByDonationRequest_IdAndStatusIn(Long id,List<AssignmentStatus> assignmentStatus);

    @Query("""
    select count(distinct a.receiver.id)
    from ModeratorAssignmentEntity a
    where a.donor.id = :donorId
""")
    Long countDistinctReceiverByDonor_Id(Long donorId);

}
