package com.bytogether.userservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VerifyType {
    EMAIL, PASSWORD, PASSWORD_VERIFY;

    @JsonCreator
    public static VerifyType from (String value){
        return VerifyType.valueOf(value.toUpperCase());
    }
}
