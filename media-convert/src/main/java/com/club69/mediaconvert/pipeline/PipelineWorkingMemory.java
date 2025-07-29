package com.club69.mediaconvert.pipeline;

import com.club69.mediaconvert.model.ConversionQueue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class PipelineWorkingMemory {
    ConversionQueue job;
    String temporaryDirectoryPath;
    Boolean status;
    String errorMessage;
    private List<List<String>> commandsGenerated;
    private Map<String, Object> context = new HashMap<>(); // For additional data between steps
    private Map<PipelineStepName, PipelineStepExecutionTime> executionTiming = new HashMap<>(); // For additional data between steps
    private List<String> generatedFiles = new ArrayList<>();
    private PipelineStepName currentStep;

    public void updateStartTimeForStep() { executionTiming.put(currentStep, new PipelineStepExecutionTime()); }
    public void updateEndTimeForStep() { executionTiming.get(currentStep).setEndTime(LocalDateTime.now()); }
    public void addGeneratedFile(String fileName) { generatedFiles.add(fileName); }
    public void addToContext(String key, Object value) { this.context.put(key, value); }
    public <T> T getFromContext(String key, Class<T> type) { return type.cast(this.context.get(key)); }

    public void markAsFailed(String errorMessage) {
        this.status = false;
        this.errorMessage = errorMessage;
    }

    @Getter
    public static class PipelineStepExecutionTime {
        private final LocalDateTime startTime = LocalDateTime.now();
        @Setter private LocalDateTime endTime;
    }
}
