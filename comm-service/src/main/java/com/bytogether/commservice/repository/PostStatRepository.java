package com.bytogether.commservice.repository;

import com.bytogether.commservice.entity.PostStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostStatRepository extends JpaRepository<PostStat, Long> {

    @Query("""
    SELECT ps FROM PostStat ps
    WHERE ps.region = :region
    ORDER BY ps.createdAt DESC
    """)
    List<PostStat> findByRegion(@Param("region") String divisionCode, Pageable pageable);

    @Query("""
    SELECT ps FROM PostStat ps
    WHERE ps.region = :region AND ps.tag = :tag
    ORDER BY ps.createdAt DESC
    """)
    List<PostStat> findByRegionAndTag(@Param("region") String divisionCode,
                                      @Param("tag") String tag,
                                      Pageable pageable);


    @Query("""
    SELECT ps FROM PostStat ps
    WHERE ps.region = :region
      AND (:tag = 'all' OR ps.tag = :tag)
      AND (
          :keyword IS NULL
          OR :keyword = ''
          OR LOWER(ps.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(ps.previewContent) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    ORDER BY ps.createdAt DESC
    """)
    List<PostStat> searchPosts(@Param("region") String region,
                               @Param("tag") String tag,
                               @Param("keyword") String keyword,
                               Pageable pageable);

}