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
public class PipelineWorkingMemory {
    private ConversionQueue job;
    private String temporaryDirectoryPath;
    private Boolean status;
    private String errorMessage;
    private List<List<String>> commandsGenerated;
    private List<String> shakaCommand;
    private Map<String, Object> context; // For additional data between steps
    private Map<PipelineStepName, PipelineStepExecutionTime> executionTiming; // For additional data between steps
    private List<String> generatedFiles;
    private PipelineStepName currentStep;

    public PipelineWorkingMemory(ConversionQueue job) {
        this.job = job;
        this.commandsGenerated = new ArrayList<>();
        this.executionTiming = new HashMap<>();
        this.context = new HashMap<>();
        this.status = true;
    }

    public void updateStartTimeForStep() {executionTiming.put(currentStep, new PipelineStepExecutionTime()); }
    public void updateEndTimeForStep() {executionTiming.get(currentStep).setEndTime(LocalDateTime.now()); }
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
