package com.bytogether.commservice.repository;

import com.bytogether.commservice.entity.PostStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostStatRepository extends JpaRepository<PostStat, Long> {

    List<PostStat> findByRegion(String divisionCode, Pageable pageable);

    List<PostStat> findByRegionAndTag(String divisionCode, String tag, Pageable pageable);

}