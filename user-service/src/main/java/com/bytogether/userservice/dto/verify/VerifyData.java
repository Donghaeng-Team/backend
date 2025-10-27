package com.bytogether.userservice.dto.verify;

import com.bytogether.userservice.model.InitialProvider;
import com.bytogether.userservice.model.VerifyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyData {
    private String email;
    private VerifyType verifyType;
    private InitialProvider initialProvider;
}
