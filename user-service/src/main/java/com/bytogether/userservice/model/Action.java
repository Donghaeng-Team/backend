package com.bytogether.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Action {
    ISSUED, REFRESHED, REVOKED, EXPIRED ;
}
