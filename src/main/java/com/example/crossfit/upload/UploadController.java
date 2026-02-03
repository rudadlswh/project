package com.example.crossfit.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
    private final FileStorageService fileStorageService;

    public UploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/images")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("image") MultipartFile image) {
        String url = fileStorageService.store(image);
        return ResponseEntity.ok(new UploadResponse(url));
    }
}
