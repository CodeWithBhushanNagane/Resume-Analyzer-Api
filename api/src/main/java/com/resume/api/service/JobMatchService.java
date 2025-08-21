package com.resume.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.api.model.ResumeEntity;
import com.resume.api.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobMatchService {

    @Autowired
    ResumeRepository resumeRepository;
    @Autowired
    GptService gptService;
    private final ObjectMapper mapper = new ObjectMapper();


    public List<Map<String, Object>> matchJob(String jobDescription) {
        try {
            // 1. Extract structured job requirements from GPT
            String jobJson = gptService.extractJobRequirements(jobDescription);

            jobJson = jobJson.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            JsonNode jobReq = mapper.readTree(jobJson);
            Set<String> requiredSkills = new HashSet<>();
            jobReq.withArray("skills").forEach(s -> requiredSkills.add(s.asText().toLowerCase()));
            int minExperience = jobReq.has("min_experience") ? jobReq.get("min_experience").asInt() : 0;
            String requiredDegree = jobReq.has("education") ? jobReq.get("education").asText().toLowerCase() : "";

            // 2. Fetch all resumes from DB
            List<ResumeEntity> resumes = resumeRepository.findAll();

            List<Map<String, Object>> results = new ArrayList<>();

            for (ResumeEntity resume : resumes) {
                if(resume.getAiAnalysis() == null || resume.getAiAnalysis().isEmpty()) {
                    continue; // Skip resumes without AI analysis
                }
                JsonNode analysis = mapper.readTree(resume.getAiAnalysis());

                // Candidate skills
                Set<String> candidateSkills = new HashSet<>();
                analysis.withArray("skills").forEach(s -> candidateSkills.add(s.asText().toLowerCase()));

                // Skill matching
                Set<String> matched = new HashSet<>(candidateSkills);
                matched.retainAll(requiredSkills);
                Set<String> missing = new HashSet<>(requiredSkills);
                missing.removeAll(candidateSkills);

                double skillMatchPercent = requiredSkills.isEmpty() ? 100 :
                        ((double) matched.size() / requiredSkills.size()) * 100;

                // Experience
                int years = 0;
                if (analysis.has("experience") && analysis.withArray("experience").size() > 0) {
                    years = analysis.withArray("experience").get(0).has("years")
                            ? analysis.withArray("experience").get(0).get("years").asInt()
                            : 0;
                }
                boolean experienceOk = years >= minExperience;

                // Education
                boolean educationOk = analysis.withArray("education")
                        .toString().toLowerCase().contains(requiredDegree);

                // Build result
                Map<String, Object> candidateResult = new HashMap<>();
                candidateResult.put("candidate", analysis.has("name") ? analysis.get("name").asText() : "Unknown");
                candidateResult.put("matchPercent", Math.round(skillMatchPercent));
                candidateResult.put("matchedSkills", matched);
                candidateResult.put("missingSkills", missing);
                candidateResult.put("experienceOk", experienceOk);
                candidateResult.put("educationOk", educationOk);

                results.add(candidateResult);
            }

            // 3. Sort by match percent
            results.sort((a, b) -> {
                Number n1 = (Number) b.get("matchPercent");
                Number n2 = (Number) a.get("matchPercent");
                return Integer.compare(n1.intValue(), n2.intValue());
            });
//            results.sort((a, b) -> ((Integer)b.get("matchPercent")).compareTo((Integer)a.get("matchPercent")));

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Error during job matching: " + e.getMessage(), e);
        }
    }
}
