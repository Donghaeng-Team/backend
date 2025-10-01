package com.bytogether.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenType {
    REFRESH, ACCESS;  ;
}
