use dive2025;
select *
from shelter;

SELECT id,
       username,
       description,
       (6371 * acos(
               cos(radians(35.151443)) * cos(radians(latitude)) *
               cos(radians(longitude) - radians(128.93962)) +
               sin(radians(35.151443)) * sin(radians(latitude))
               )) AS distance,
       CASE WHEN shelter_feature = 'INJURED' THEN 0 ELSE 1 END AS match_priority
FROM shelter
WHERE latitude != 35.151443
ORDER BY match_priority ASC, distance ASC;