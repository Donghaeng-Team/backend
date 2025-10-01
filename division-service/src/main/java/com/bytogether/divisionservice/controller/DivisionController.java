package com.bytogether.divisionservice.controller;

import com.bytogether.divisionservice.dto.*;
import com.bytogether.divisionservice.entity.Division;
import com.bytogether.divisionservice.service.DivisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/division")
public class DivisionController {

    private final DivisionService divisionService;

    // 읍면동 검색 (좌표로) - 완성
    @GetMapping("/public/by-coord")
    public Optional<Division> getDivisionByCoordinates(@Valid Coordinate coordinate) {
        return divisionService.getDivisionByCoordinates(coordinate);
    }

}
