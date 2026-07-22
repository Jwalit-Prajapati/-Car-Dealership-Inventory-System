package com.jwalit.inventory_system.dto;

import java.time.Instant;

public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private final Instant timestamp = Instant.now();

    protected ApiResponse() {
        // Enforce instantiation via static factories only
    }

    public static <T> ApiResponse<T> success(T payload) {
        ApiResponse<T> responseInstance = new ApiResponse<>();
        responseInstance.success = true;
        responseInstance.status = 200;
        responseInstance.message = "Success";
        responseInstance.data = payload;
        return responseInstance;
    }

    public static <T> ApiResponse<T> success(String customMessage, T payload) {
        ApiResponse<T> responseInstance = new ApiResponse<>();
        responseInstance.success = true;
        responseInstance.status = 200;
        responseInstance.message = customMessage;
        responseInstance.data = payload;
        return responseInstance;
    }

    public static <T> ApiResponse<T> error(int httpStatus, String errorMessage) {
        ApiResponse<T> responseInstance = new ApiResponse<>();
        responseInstance.success = false;
        responseInstance.status = httpStatus;
        responseInstance.message = errorMessage;
        responseInstance.data = null;
        return responseInstance;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
