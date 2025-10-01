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
@RequestMapping("/internal/v1/division")
public class DivisionInternalController {

    private final DivisionService divisionService;

    // 읍면동 리스트 검색 (좌표로) - 완성 (최적화는 이후에 현재 쿼리 여러번 날림 - 250923 14:26)
    @PostMapping("/list/by-coord")
    public List<Optional<Division>> getDivisionListByCoordinatesList(@RequestBody @Valid Coordinates coordinates) {
        return divisionService.getDivisionListByCoordinatesList(coordinates);
    }


    // 읍면동 검색 (읍면동 코드로) - 완성
    @GetMapping("/by-code")
    public Optional<Division> getDivisionByCode(@Valid Emd emd) {
        return divisionService.getDivisionByCode(emd);
    }

    //읍면동 리스트 검색 (읍면동 코드로) - 완성 (최적화는 이후에 현재 쿼리 여러번 날림 - 250923 14:26)
    @PostMapping("/list/by-code")
    public List<Optional<Division>> getDivisionListByCodeList(@RequestBody @Valid Emds emds) {
        return divisionService.getDivisionListByCodeList(emds);
    }


    // 인접동 검색 (읍면동 코드로) - 완성
    @GetMapping("/near/by-code")
    public List<Division> getNearDivisionListByCode(@Valid EmdDepth emdDepth) {
        return divisionService.getNearDivisionByCode(emdDepth);
    }

    // 인접동 검색 (좌표로) - 완성
    @GetMapping("/near/by-coord")
    public List<Division> getNearDivisionListByCoordinates(@Valid CoordinateDepth coordinateDepth) {
        return divisionService.getNearDivisionByCoordinates(coordinateDepth);
    }

}
