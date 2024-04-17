package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alert {
    private Integer id_toast;
    private String message;
    private Integer duration;
    private String type;
    private Integer status_code;
    private UserResponse userResponse;

    public Alert(Integer id_toast, String message, Integer duration, String type, Integer status_code) {
        this.id_toast = id_toast;
        this.message = message;
        this.duration = duration;
        this.type = type;
        this.status_code = status_code;
    }

    public Alert(Integer id_toast, String message, Integer duration, String type, Integer status_code, UserResponse userResponse) {
        this.id_toast = id_toast;
        this.message = message;
        this.duration = duration;
        this.type = type;
        this.status_code = status_code;
        this.userResponse = userResponse;
    }
}
