package com.pgmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    
    private Boolean success;
    
    private String message;
    
    private Object data;
    
    public static ResponseDto success(String message, Object data) {
        return new ResponseDto(true, message, data);
    }
    
    public static ResponseDto success(String message) {
        return new ResponseDto(true, message, null);
    }
    
    public static ResponseDto error(String message) {
        return new ResponseDto(false, message, null);
    }
}
