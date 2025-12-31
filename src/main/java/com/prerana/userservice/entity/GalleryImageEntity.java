package com.prerana.userservice.entity;

import com.prerana.userservice.enums.GalleryStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery")
@Data
public class GalleryImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ngo_id", nullable = false)
    private UserEntity ngo;

    @Column(nullable = false)
    private String imagePath;

    private String caption;

    @Enumerated(EnumType.STRING)
    private GalleryStatus status; // OPTIONAL for now

    private LocalDateTime uploadedAt;


}
