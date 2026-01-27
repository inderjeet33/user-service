package com.prerana.userservice.service;

import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.entity.VolunteerAssignmentEntity;
import com.prerana.userservice.entity.VolunteerRequestEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.VolunteerOfferStatus;
import com.prerana.userservice.repository.UserRepository;
import com.prerana.userservice.repository.VolunteerAssignmentRepository;
import com.prerana.userservice.repository.VolunteerRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerAssignmentService {

    private final VolunteerRequestRepository volunteerRepo;
    private final VolunteerAssignmentRepository assignmentRepo;
    private final UserRepository userRepo;

    @Transactional
    public VolunteerAssignmentEntity assignNgo(
            Long moderatorId,
            Long volunteerRequestId,
            Long ngoId
    ) {

        VolunteerRequestEntity request =
                volunteerRepo.findById(volunteerRequestId)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != VolunteerOfferStatus.OPEN) {
            throw new RuntimeException("Request not open");
        }

        if (assignmentRepo.existsByVolunteerRequest_IdAndStatusIn(
                volunteerRequestId,
                List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.IN_PROGRESS)
        )) {
            throw new RuntimeException("Already assigned");
        }

        UserEntity moderator = userRepo.findById(moderatorId).orElseThrow();
        UserEntity ngo = userRepo.findById(ngoId).orElseThrow();

        VolunteerAssignmentEntity assignment =
                VolunteerAssignmentEntity.builder()
                        .moderator(moderator)
                        .volunteerRequest(request)
                        .receiver(ngo)
                        .receiver_type(ngo.getUserType())
                        .volunteer(request.getUser())
                        .status(AssignmentStatus.ASSIGNED)
                        .build();


        request.setStatus(VolunteerOfferStatus.ASSIGNED);

        volunteerRepo.save(request);
        return assignmentRepo.save(assignment);
    }
}
