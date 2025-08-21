package com.resume.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.resume.api.dto.ResumeAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GptService {

    private final OpenAIClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GptService(@Value("${openai.api.key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public ResumeAnalysis analyzeResume(String resumeText) {
        String prompt = """
            You are a resume parser. Return only valid JSON with this structure:
            {
              "name": "",
              "email": "",
              "phone": "",
              "skills": [],
              "experience": [
                { "company": "", "role": "", "years": 0 }
              ],
              "education": [
                { "degree": "", "university": "", "year": 0 }
              ]
            }
            Resume text:
            """ + resumeText;

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .addSystemMessage("You are a JSON API. Always return valid JSON without commentary.")
                .addUserMessage(prompt)
                .maxCompletionTokens(2000)
                .temperature(0.0)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        String rawOutput = completion.choices().get(0).message().content().orElse("{}");

        if (rawOutput.startsWith("```")) {
            rawOutput = rawOutput.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
        }

        try {
            return objectMapper.readValue(rawOutput, ResumeAnalysis.class);
        } catch (Exception e) {
            System.err.println("Failed to parse AI JSON: " + e.getMessage());
            return new ResumeAnalysis();
        }
    }

    /**
     * Extract structured job requirements
     */
    public String extractJobRequirements(String jobDescription) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI) // or GPT_5
                .addUserMessage(
                        "Extract the following from this job description and return strictly as JSON:\n" +
                                "{ skills: [list of required skills], min_experience: number (years), education: string }\n\n" +
                                "Job Description:\n" + jobDescription
                )
                .temperature(0.0) // deterministic
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        return completion.choices().get(0).message().content().get();
    }
}
