package com.jwalit.inventory_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> success(T payload) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Success")
                .data(payload)
                .build();
    }

    public static <T> ApiResponse<T> success(String customMessage, T payload) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(customMessage)
                .data(payload)
                .build();
    }

    public static <T> ApiResponse<T> error(int httpStatus, String errorMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(httpStatus)
                .message(errorMessage)
                .build();
    }
}
