package com.bytogether.divisionservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Emd {
    @Pattern(regexp = "^\\d{8}$")
    private String emdCode;
}
