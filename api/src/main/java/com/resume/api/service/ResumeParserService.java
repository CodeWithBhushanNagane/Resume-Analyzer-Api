package com.resume.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.api.dto.ResumeAnalysis;
import com.resume.api.model.EducationEntity;
import com.resume.api.repository.ResumeRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.resume.api.model.ResumeEntity;
import com.resume.api.model.ExperienceEntity;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ResumeParserService {

    private final Tika tika = new Tika();

    @Autowired
    private GptService gptService;

    @Autowired
    private ResumeRepository resumeRepository;

    public String extractTextFromFile(File file) throws Exception {
        return tika.parseToString(file);
    }

    public ResumeEntity saveParsedResume(String fileName, String filePath, String resumeText, ResumeAnalysis aiData) throws JsonProcessingException {
        ResumeEntity resume = new ResumeEntity();
        resume.setFileName(fileName);
        resume.setFilePath(filePath);
        resume.setName(aiData.name);
        resume.setEmail(aiData.email);
        resume.setPhone(aiData.phone);
        resume.setSkills(aiData.skills);
        resume.setResumeTextExtracted(resumeText);
        resume.setAiAnalysis(new ObjectMapper().writeValueAsString(aiData));
        resume.setUploadedAt(LocalDateTime.now());

        // Map experience
        List<ExperienceEntity> experiences = aiData.experience.stream().map(exp -> {
            ExperienceEntity e = new ExperienceEntity();
            e.setCompany(exp.company);
            e.setRole(exp.role);
            e.setYears(exp.years);
            e.setResume(resume);
            return e;
        }).toList();

        // Map education
        List<EducationEntity> educations = aiData.education.stream().map(edu -> {
            EducationEntity e = new EducationEntity();
            e.setDegree(edu.degree);
            e.setUniversity(edu.university);
            e.setYear(edu.year);
            e.setResume(resume);
            return e;
        }).toList();

        resume.setExperience(experiences);
        resume.setEducation(educations);

        return resumeRepository.save(resume);
    }

    // Implement getAllResumes method
    public List<ResumeEntity> getAllResumes() {
        return resumeRepository.findAll();
    }
}
