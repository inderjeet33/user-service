package com.prerana.userservice.enums;

public enum DonationOfferStatus {
    OPEN,           // not assigned to anyone
    ASSIGNED,       // assigned to a receiver (active)
    IN_PROGRESS,    // contact ongoing
    COMPLETED,      // help delivered
    CANCELLED,      // donor cancelled
    EXPIRED         // old / inactive
}