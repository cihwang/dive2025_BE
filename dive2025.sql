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
        COS(RADIANS(35.17721265))
      * COS(RADIANS(s.latitude))
      * COS(RADIANS(s.longitude) - RADIANS(128.9167344))
      + SIN(RADIANS(35.17721265)) * SIN(RADIANS(s.latitude))
    )) AS distance,
    (sc.total_capacity - sc.cur_capacity) AS remain_capacity,
    (FIELD(s.shelter_feature, 'GENERAL','VET','HOSPITAL') - 1) AS shelter_rank,
    (FIELD('SEVERE', 'NORMAL','MILD','SEVERE') - 1) AS animal_rank
FROM shelter s
JOIN (
    SELECT sc1.shelter_id, sc1.total_capacity, sc1.cur_capacity
    FROM shelter_capacity sc1
    WHERE sc1.updated_at = (
        SELECT MAX(sc2.updated_at)
        FROM shelter_capacity sc2
        WHERE sc2.shelter_id = sc1.shelter_id
    )
) sc ON sc.shelter_id = s.id
WHERE
    s.longitude !=  128.9167344
    AND (sc.total_capacity - sc.cur_capacity) > 0
    /* MILD(1) > shelter_rank 조건 */
    AND (FIELD(s.shelter_feature, 'GENERAL','VET','HOSPITAL') - 1) >= (FIELD('SEVERE', 'NORMAL','MILD','SEVERE') - 1)
ORDER BY
    distance ASC,
    shelter_rank DESC;