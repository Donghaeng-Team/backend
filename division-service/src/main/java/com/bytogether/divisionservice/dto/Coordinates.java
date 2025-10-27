package com.bytogether.divisionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coordinates {
    @NotNull
    List<Coordinate> coordinateList;
}
