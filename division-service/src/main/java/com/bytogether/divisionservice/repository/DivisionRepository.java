package com.bytogether.divisionservice.repository;

import com.bytogether.divisionservice.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, String> {

    // 읍면동 검색 (좌표로) - 완성
    @Query(nativeQuery = true, value = """
            SELECT * FROM divisions d
            WHERE d.id = COALESCE(
                -- 1. 먼저 포함되는 것 찾기
                (
                    SELECT division_id
                    FROM division_geoms dg
                    WHERE ST_Contains(
                        dg.geom,
                        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                    )
                    LIMIT 1
                ),
                -- 2. 없으면 가장 가까운 것 찾기 (거리 제한 추가)
                (
                    SELECT division_id
                    FROM division_geoms dg
                    WHERE ST_DWithin(
                        dg.geom::geography,
                        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                        1000  -- 1km 반경 내에서만 검색
                    )
                    ORDER BY ST_Distance(
                        dg.geom::geography,
                        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
                    )
                    LIMIT 1
                )
            )
            """)
    Optional<Division> findByCoordinate(@Param("latitude") Double latitude, @Param("longitude") Double longitude);


    // 인접동 검색 (읍면동 코드로) - 완성
    @Query(nativeQuery = true, value = """
            SELECT * FROM divisions WHERE id in (
                    WITH RECURSIVE division_hops AS (
                SELECT id, division_id, 1 AS level
                FROM division_nears
                WHERE id = :emyCode
            
                UNION ALL
            
                SELECT dn.id, dn.division_id, dh.level + 1
                FROM division_nears dn
                JOIN division_hops dh ON dn.id = dh.division_id
                WHERE dh.level < :depth
                    )
                    SELECT DISTINCT division_id
                    FROM division_hops
                )
            """)
    List<Division> getNearDivisionByCode(@Param("emyCode") String emyCode, @Param("depth") Integer depth);

    // 인접동 검색 (좌표로) - 완성
    @Query(nativeQuery = true, value = """
            
                    SELECT
            	*
            FROM
            	DIVISIONS
            WHERE
            	ID IN (
            		WITH RECURSIVE
            			DIVISION_HOPS AS (
            				SELECT
            					ID,
            					DIVISION_ID,
            					1 AS LEVEL
            				FROM
            					DIVISION_NEARS
            				WHERE
            					ID = (
            						SELECT
            							D.ID
            						FROM
            							DIVISIONS D
            						WHERE
            							D.ID = (
            								SELECT
            									DIVISION_ID
            								FROM
            									DIVISION_GEOMS DG
            								WHERE
            									ST_CONTAINS (
            										DG.GEOM,
            										ST_SETSRID (ST_MAKEPOINT (:longitude,:latitude), 4326)
            									)
            							)
            					)
            				UNION ALL
            				SELECT
            					DN.ID,
            					DN.DIVISION_ID,
            					DH.LEVEL + 1
            				FROM
            					DIVISION_NEARS DN
            					JOIN DIVISION_HOPS DH ON DN.ID = DH.DIVISION_ID
            				WHERE
            					DH.LEVEL < :depth
            			)
            		SELECT DISTINCT
            			DIVISION_ID
            		FROM
            			DIVISION_HOPS
            	)
            """)
    List<Division> getNearDivisionByCoordinates(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("depth") Integer depth);
}
