package com.club69.commons.response;

import lombok.Data;

@Data
public class ApiResponse {
    private Boolean success;
    private String message;
    private Object data;

    public ApiResponse(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, Object data) {
        this.success = true;
        this.message = message;
        this.data = data;
    }
}
