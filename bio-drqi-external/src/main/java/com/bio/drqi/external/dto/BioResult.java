package com.bio.drqi.external.dto;

import lombok.Data;

@Data
public class BioResult<T> {

    private String code;

    private T data;

    private String message;

    public boolean isSuccess() {
        if ("200".equals(code)) {
            return true;
        } else {
            return false;
        }
    }
}
