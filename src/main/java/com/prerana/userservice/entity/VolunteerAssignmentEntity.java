//package com.prerana.userservice.entity;
//
//import com.prerana.userservice.enums.AssignmentStatus;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "volunteer_assignments")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class VolunteerAssignmentEntity extends BaseEntity {
//
//    @ManyToOne
//    @JoinColumn(name = "moderator_id", nullable = false)
//    private UserEntity moderator;
//
//    @ManyToOne
//    @JoinColumn(name = "volunteer_request_id", nullable = false)
//    private VolunteerRequestEntity volunteerRequest;
//
//    @ManyToOne
//    @JoinColumn(name = "ngo_id", nullable = false)
//    private UserEntity ngo;
//
//    @Enumerated(EnumType.STRING)
//    private AssignmentStatus status;   // ASSIGNED, IN_PROGRESS, COMPLETED
//}

package com.prerana.userservice.entity;

import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "volunteer_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerAssignmentEntity extends BaseEntity {

    // Who assigned (Moderator)
    @ManyToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    private UserEntity moderator;

    // Volunteer Request
    @ManyToOne
    @JoinColumn(name = "volunteer_request_id", nullable = false)
    private VolunteerRequestEntity volunteerRequest;

    // Assigned NGO / Receiver
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType receiver_type;

    // Volunteer (Individual user)
    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private UserEntity volunteer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    public boolean isActive() {
        return status == AssignmentStatus.ASSIGNED ||
                status == AssignmentStatus.IN_PROGRESS;
    }
}
