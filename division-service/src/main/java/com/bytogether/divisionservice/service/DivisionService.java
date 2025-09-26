package com.bytogether.divisionservice.service;

import com.bytogether.divisionservice.dto.*;
import com.bytogether.divisionservice.entity.Division;
import com.bytogether.divisionservice.repository.DivisionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DivisionService {

    private final DivisionRepository divisionRepository;

    // 읍면동 검색 (좌표로) - 완성
    public Optional<Division> getDivisionByCoordinates(Coordinate coordinate) {
        return divisionRepository.findByCoordinate(coordinate.getLatitude(), coordinate.getLongitude());
    }

    // 읍면동 리스트 검색 (좌표로) - 완성 (최적화는 이후에 현재 쿼리 여러번 날림 - 250923 14:26)
    public List<Optional<Division>> getDivisionListByCoordinatesList(Coordinates coordinates) {
        List<Optional<Division>> list = new ArrayList<>();

        coordinates.getCoordinateList().forEach(coordinate -> {
            Optional<Division> byCoordinate = divisionRepository.findByCoordinate(coordinate.getLatitude(), coordinate.getLongitude());
            list.add(byCoordinate);
        });

        return list;
    }

    // 읍면동 검색 (읍면동 코드로) - 완성
    public Optional<Division> getDivisionByCode(Emd emd) {
        return divisionRepository.findById(emd.getEmyCode());
    }

    //읍면동 리스트 검색 (읍면동 코드로) - 완성 (최적화는 이후에 현재 쿼리 여러번 날림 - 250923 14:26)
    public List<Optional<Division>> getDivisionListByCodeList(@Valid Emds emds) {
        List<Optional<Division>> list = new ArrayList<>();
        emds.getEmdList().forEach(emd -> {
            Optional<Division> byCode = divisionRepository.findById(emd.getEmyCode());
            list.add(byCode);
        });

        return list;
    }

    // 인접동 검색 (읍면동 코드로) - 완성
    public List<Division> getNearDivisionByCode(EmdDepth emdDepth) {
        return divisionRepository.getNearDivisionByCode(emdDepth.getEmyCode(), emdDepth.getDepth());
    }

    // 인접동 검색 (좌표로) - 완성
    public List<Division> getNearDivisionByCoordinates(@Valid CoordinateDepth coordinateDepth) {
        return divisionRepository.getNearDivisionByCoordinates(coordinateDepth.getLatitude(), coordinateDepth.getLongitude(), coordinateDepth.getDepth());
    }
}
