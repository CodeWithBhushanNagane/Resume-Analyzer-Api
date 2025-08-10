package com.resume.api.controller;

import com.resume.api.dto.ResumeAnalysis;
import com.resume.api.model.ResumeEntity;
import com.resume.api.service.GptService;
import com.resume.api.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    ResumeParserService parserService;

    @Autowired
    GptService gptService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            // Create upload dir if missing
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdir();

            // Save file
            Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            // Extract text
            String resumeText = parserService.extractTextFromFile(filePath.toFile());

            // AI Analysis
            ResumeAnalysis aiData = gptService.analyzeResume(resumeText);

            ResumeEntity savedResume = parserService.saveParsedResume(
                    file.getOriginalFilename(),
                    filePath.toAbsolutePath().toString(),
                    resumeText,
                    aiData
            );

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "fileName", file.getOriginalFilename(),
                    "filePath", filePath.toAbsolutePath().toString(),
                    "resumeTextExtracted", resumeText,
                    "aiAnalysis", aiData
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
