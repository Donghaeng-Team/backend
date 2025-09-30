package com.bytogether.userservice.dto.request;

import com.bytogether.userservice.model.VerifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class VerifyRequest {
    String email;
    String token;
    VerifyType type;
}
