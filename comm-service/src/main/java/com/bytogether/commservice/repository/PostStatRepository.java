package com.bytogether.commservice.repository;

import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.entity.PostStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostStatRepository extends JpaRepository<PostStat, Long> {

    List<PostStat> findByRegionAndDeletedFalse(String divisionCode, Pageable pageable);

    List<PostStat> findByRegionAndTagAndDeletedFalse(String divisionCode, String tag, Pageable pageable);

}