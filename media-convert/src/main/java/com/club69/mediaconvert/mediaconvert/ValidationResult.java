package com.club69.mediaconvert.mediaconvert;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Validation Result Class
@Data
public class ValidationResult {
    private boolean valid = true;
    private Map<String, List<String>> errors = new HashMap<>();
    private Map<String, List<String>> warnings = new HashMap<>();

    public void addError(String field, String message) {
        valid = false;
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public void addWarning(String field, String message) {
        warnings.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
