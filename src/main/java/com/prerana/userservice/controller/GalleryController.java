package com.prerana.userservice.controller;

import com.prerana.userservice.dto.GalleryImageDto;
import com.prerana.userservice.service.GalleryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    /* NGO upload */
    @PreAuthorize("hasAuthority('TYPE_NGO')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption,
            HttpServletRequest request
    ) throws IOException {
        Long ngoId = (Long) request.getAttribute("userId");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required");
        }
        galleryService.uploadImage(ngoId, file, caption);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasAuthority('TYPE_NGO')")
    @GetMapping("/ngo/my")
    public ResponseEntity<List<GalleryImageDto>> myImages(HttpServletRequest request) {

        Long ngoId = (Long) request.getAttribute("userId");

        List<GalleryImageDto> images = galleryService.getImagesByOwnerId(ngoId);

        return ResponseEntity.ok(images);
    }

    @GetMapping("/ngo/{ngoId}/public")
    public ResponseEntity<List<GalleryImageDto>> publicImages(@PathVariable Long ngoId,HttpServletRequest request){

        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(
                galleryService.getApprovedImagesForNgo(userId,ngoId)
        );
    }
}

    /* Platform gallery */
//    @GetMapping
//    public ResponseEntity<List<GalleryImageDto>> getAll() {
//        return ResponseEntity.ok(galleryService.getApprovedImages());
//    }
//
//    /* NGO gallery */
//    @GetMapping("/ngo/{ngoId}")
//    public ResponseEntity<List<GalleryImageDto>> getNgoGallery(@PathVariable Long ngoId) {
//        return ResponseEntity.ok(galleryService.getNgoGallery(ngoId));
//    }
//
//    /* Moderator approve */
//    @PostMapping("/{id}/approve")
//    public ResponseEntity<?> approve(@PathVariable Long id) {
//        galleryService.approveImage(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/ngo/me")
//    public ResponseEntity<List<GalleryImageDto>> myGallery(HttpServletRequest request) {
//        Long ngoId = (Long) request.getAttribute("userId");
//        return ResponseEntity.ok(galleryService.getNgoGallery(ngoId));
//    }
//}
