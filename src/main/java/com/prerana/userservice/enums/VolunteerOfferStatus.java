package com.prerana.userservice.enums;

public enum VolunteerOfferStatus {
    OPEN,           // not assigned to anyone
    ASSIGNED,       // assigned to a receiver (active)
    IN_PROGRESS,    // contact ongoing
    COMPLETED,      // help delivered
    CANCELLED,      // volunteer cancelled
    EXPIRED         // old / inactive
}
