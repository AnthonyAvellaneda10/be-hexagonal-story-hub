package com.uni.pe.storyhub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    public static final String TYPE_SUCCESS = "success";
    public static final String TYPE_ERROR = "error";

    private Integer idToast;
    private String message;
    @Builder.Default
    private Integer duration = 5000;
    private String type; // success, error, warning, info
    private Integer statusCode;
    private T data; // Optional data field for specific responses
}
