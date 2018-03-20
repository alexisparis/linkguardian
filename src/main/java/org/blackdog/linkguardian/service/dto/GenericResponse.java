package org.blackdog.linkguardian.service.dto;

public class GenericResponse {

    private String message;

    private GenericResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static GenericResponse withMessage(String msg) {
        GenericResponse response = new GenericResponse(msg);
        return response;
    }
}
