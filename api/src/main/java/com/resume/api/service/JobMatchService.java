package com.resume.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.api.model.JobMatchResult;
import com.resume.api.model.ResumeEntity;
import com.resume.api.repository.JobMatchResultRepository;
import com.resume.api.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JobMatchService {

    @Autowired
    ResumeRepository resumeRepository;
    @Autowired
    GptService gptService;
    @Autowired
    JobMatchResultRepository jobMatchResultRepository;
    private final ObjectMapper mapper = new ObjectMapper();


    public JobMatchResult matchAndSave(Long jobId, String jobJson, ResumeEntity resume) {
        try {
            JsonNode jobReq = mapper.readTree(jobJson);
            JsonNode analysis = mapper.readTree(resume.getAiAnalysis());

            // 1. Skills overlap
            Set<String> jobSkills = new HashSet<>();
            jobReq.get("skills").forEach(s -> jobSkills.add(s.asText().toLowerCase()));

            Set<String> resumeSkills = new HashSet<>();
            analysis.get("skills").forEach(s -> resumeSkills.add(s.asText().toLowerCase()));

            Set<String> matched = new HashSet<>(resumeSkills);
            matched.retainAll(jobSkills);

            Set<String> missing = new HashSet<>(jobSkills);
            missing.removeAll(resumeSkills);

            int skillScore = (int) (((double) matched.size() / jobSkills.size()) * 70); // 70% weight

            // 2. Experience
            int requiredYears = jobReq.get("minYears").asInt(0);
            int resumeYears = analysis.get("experience").get(0).get("years").asInt(0);
            int expScore = resumeYears >= requiredYears ? 20 : 10; // 20 if meets, else partial

            // 3. Education (simple check)
            String requiredDegree = jobReq.has("degree") ? jobReq.get("degree").asText().toLowerCase() : "";
            boolean hasDegree = analysis.get("education").toString().toLowerCase().contains(requiredDegree);
            int eduScore = hasDegree ? 10 : 0;

            int totalScore = skillScore + expScore + eduScore;

            // 4. GPT summary
            String summary = gptService.analyzeText(
                    "Given resume: " + analysis.toPrettyString() +
                            "\nAnd job: " + jobReq.toPrettyString() +
                            "\nProvide a short JSON: { \"summary\": \"\", \"whyMatch\": \"\" }"
            );

            // Save
            JobMatchResult result = JobMatchResult.builder()
                    .jobId(jobId)
                    .resumeId(resume.getId())
                    .score(totalScore)
                    .missingSkills(mapper.writeValueAsString(missing))
                    .summary(summary)
                    .aiGenerated(true)
                    .build();

            return jobMatchResultRepository.save(result);

        } catch (Exception e) {
            throw new RuntimeException("Error during job matching", e);
        }
    }
}
