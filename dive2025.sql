use dive2025;
select *
from shelter;

select * from shelter_capacity;

# 가까운 shelter 가져오기
SELECT
    s.id,
    s.username,
    s.description,
    (6371 * ACOS(
            COS(RADIANS(35.151443)) * COS(RADIANS(s.latitude)) *
            COS(RADIANS(s.longitude) - RADIANS(128.93962)) +
            SIN(RADIANS(35.151443)) * SIN(RADIANS(s.latitude))
            )) AS distance,
    CASE WHEN s.shelter_feature = 'INJURED' THEN 0 ELSE 1 END AS match_priority,
    (sc.total_capacity - sc.cur_capacity) AS remain_capacity
FROM shelter s
         JOIN (
    SELECT sc1.shelter_id, sc1.total_capacity, sc1.cur_capacity
    FROM shelter_capacity sc1
    WHERE sc1.updated_at = (
        SELECT MAX(sc2.updated_at)
        FROM shelter_capacity sc2
        WHERE sc2.shelter_id = sc1.shelter_id
    )
) sc
              ON s.id = sc.shelter_id
WHERE s.latitude != 35.151443
  AND (sc.total_capacity - sc.cur_capacity) > 0
ORDER BY match_priority ASC, distance ASC;