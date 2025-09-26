package com.bytogether.divisionservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 행정구역 정보 엔티티
 * 시도, 시군구, 읍면동 정보와 중심점 좌표를 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "divisions", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sidocd", "sggcd", "emdcd"}))
public class Division {
    @Id
    @Column(name = "id", length = 8, nullable = false, updatable = false, insertable = false)
    private String id;

    @Column(name = "sidocd", length = 2, nullable = false, updatable = false, insertable = false)
    private String sidoCode;

    @Column(name = "sidonm", length = 30, nullable = false, updatable = false, insertable = false)
    private String sidoName;

    @Column(name = "sggcd", length = 3, nullable = false, updatable = false, insertable = false)
    private String sggCode;

    @Column(name = "sggnm", length = 30, nullable = false, updatable = false, insertable = false)
    private String sggName;

    @Column(name = "emdcd", length = 3, nullable = false, updatable = false, insertable = false)
    private String emdCode;

    @Column(name = "emdnm", length = 30, nullable = false, updatable = false, insertable = false)
    private String emdName;

    @Column(name = "centroid_lat", precision = 11, scale = 8, nullable = false, updatable = false, insertable = false)
    private BigDecimal centroidLat;

    @Column(name = "centroid_lng", precision = 12, scale = 8, nullable = false, updatable = false, insertable = false)
    private BigDecimal centroidLng;
}
