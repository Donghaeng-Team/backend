package com.bytogether.marketservice.entity;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.dto.request.ExtendMarketRequest;
import com.bytogether.marketservice.dto.request.PutMarketRequest;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.util.GeometryUtils;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 공동구매 게시글 엔티티 클래스 - Market
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Entity
@Table(name = "markets")
@Getter
@Setter
@NoArgsConstructor
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", length = 8, nullable = false)
    private String categoryId;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "price", nullable = false, updatable = false)
    private Long price;

    @Column(name = "recruit_min", nullable = false, updatable = false)
    private Integer recruitMin;

    @Column(name = "recruit_max", nullable = false, updatable = false)
    private Integer recruitMax;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private MarketStatus status;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_id", nullable = false, updatable = false)
    private Long authorId;

    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point, 4326)", updatable = false)
    private Point location;

    @Column(name = "location_text", nullable = false)
    private String locationText;

    @Column(name = "division_id", length = 8, nullable = false, updatable = false)
    private String divisionId;

    @Column(name = "emd_name", length = 30, nullable = false, updatable = false)
    private String emdName;

    @Column(name = "latitude", precision = 11, scale = 8, nullable = false, updatable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 12, scale = 8, nullable = false, updatable = false)
    private BigDecimal longitude;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    // Category와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // Images와의 관계
    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Image> images = new ArrayList<>();

    // Views와의 관계
    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL)
    private List<View> marketViews = new ArrayList<>();

    public static Market fromCreateRequest(CreateMarketRequest request, Long authorId, String emdName, String divisionId) {
        Market market = new Market();
        market.setCategoryId(request.getCategoryId());
        market.setEndTime(request.getEndTime());
        market.setPrice(request.getPrice());
        market.setRecruitMin(request.getRecruitMin());
        market.setRecruitMax(request.getRecruitMax());
        market.setStatus(MarketStatus.RECRUITING); // 기본 상태
        market.setTitle(request.getTitle());
        market.setContent(request.getContent());
        market.setAuthorId(authorId);
        Point point = GeometryUtils.createPoint(request.getLongitude().doubleValue(), request.getLatitude().doubleValue());
        market.setLocation(point);
        market.setLocationText(request.getLocationText());
        market.setDivisionId(divisionId);
        market.setEmdName(emdName);
        market.setLatitude(request.getLatitude());
        market.setLongitude(request.getLongitude());
        return market;

    }

    // 제약조건 검증 메서드
    @PrePersist
    @PreUpdate
    private void validateConstraints() {
        if (recruitMin != null && recruitMin <= 1) {
            throw new IllegalArgumentException("모집 인원은 2명 이상이어야 합니다.");
        }
        if (recruitMin != null && recruitMax != null && recruitMin > recruitMax) {
            throw new IllegalArgumentException("최소 모집 인원은 최대 모집 인원보다 클 수 없습니다.");
        }
        if (latitude != null && (latitude.compareTo(BigDecimal.valueOf(33)) < 0 ||
                latitude.compareTo(BigDecimal.valueOf(39)) > 0)) {
            throw new IllegalArgumentException("위도는 33~39 범위여야 합니다.");
        }
        if (longitude != null && (longitude.compareTo(BigDecimal.valueOf(124)) < 0 ||
                longitude.compareTo(BigDecimal.valueOf(132)) > 0)) {
            throw new IllegalArgumentException("경도는 124~132 범위여야 합니다.");
        }
    }

    public void updateFromPutRequest(@Valid PutMarketRequest putMarketRequest) {
        // 수정 가능한 필드만 업데이트
        // title, content, categoryId, endTime(이후 시간으로만 변경 가능)

        // 이후 시간으로만 변경 가능
        if (putMarketRequest.getEndTime().isBefore(this.getEndTime())) {
            throw new MarketException("End time must be later than the current end time", HttpStatus.BAD_REQUEST);
        }
        this.setTitle(putMarketRequest.getTitle());
        this.setContent(putMarketRequest.getContent());
        this.setCategoryId(putMarketRequest.getCategoryId());

        this.setEndTime(putMarketRequest.getEndTime());
    }

    public void extendEndTimeFromExtendRequest(ExtendMarketRequest extendMarketRequest) {
        // 이후 시간으로만 변경 가능
        if (extendMarketRequest.getEndTime().isBefore(this.getEndTime())) {
            throw new MarketException("End time must be later than the current end time", HttpStatus.BAD_REQUEST);
        }
        this.setEndTime(extendMarketRequest.getEndTime());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "categoryId = " + categoryId + ", " +
                "endTime = " + endTime + ", " +
                "price = " + price + ", " +
                "recruitMin = " + recruitMin + ", " +
                "recruitMax = " + recruitMax + ", " +
                "status = " + status + ", " +
                "title = " + title + ", " +
                "content = " + content + ", " +
                "authorId = " + authorId + ", " +
                "location = " + location + ", " +
                "locationText = " + locationText + ", " +
                "divisionId = " + divisionId + ", " +
                "emdName = " + emdName + ", " +
                "latitude = " + latitude + ", " +
                "longitude = " + longitude + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ", " +
                "views = " + views + ")";
    }
}