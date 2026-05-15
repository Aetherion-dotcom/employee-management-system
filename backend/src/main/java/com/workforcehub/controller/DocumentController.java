package com.workforcehub.controller;

import com.workforcehub.dto.response.ApiResponse;
import com.workforcehub.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "Upload and download employee documents")
public class DocumentController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a document")
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.save(file);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", filename));
    }

    @GetMapping("/download/{filename}")
    @Operation(summary = "Download a document")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource resource = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    @Operation(summary = "Delete a document")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String filename) {
        fileStorageService.delete(filename);
        return ResponseEntity.ok(ApiResponse.success("File deleted", null));
    }
}
