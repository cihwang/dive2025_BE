package com.example.DIVE2025.domain.rescued.controller;

import com.example.DIVE2025.domain.rescued.mapper.RescuedMapper;
import com.example.DIVE2025.domain.rescued.service.RescuedImportService;
import com.example.DIVE2025.domain.rescued.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin/rescued")
@RequiredArgsConstructor
public class RescuedImportController {

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");
    // 운영에서는 @Value("${rescued.admin-key}") 로 외부화 권장
    private static final String ADMIN_KEY = "run-once";

    private final RescuedImportService service;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final RescuedMapper rescuedMapper;
    private final FileUploadUtil fileUploadUtil;

    /**
     * 초기 적재: 최근 3년치 + 보호중(protect)만 DB 구성
     *   POST /admin/rescued/initial-build?key=run-once
     */
    @PostMapping("/initial-build")
    public ResponseEntity<String> initialBuild(@RequestParam("key") String key) {
        if (!ADMIN_KEY.equals(key)) return ResponseEntity.status(403).body("forbidden");

        long t0 = System.currentTimeMillis();
        int changed = service.initialBuild3YearsProtectOnly();
        long t1 = System.currentTimeMillis();

        String msg = String.format("initial build done, changedRows=%d, elapsed=%.2fs",
                changed, (t1 - t0) / 1000.0);
        log.info("[RescueAdminController] {}", msg);
        return ResponseEntity.ok(msg);
    }


//      수동 동기화(최근 N일 버퍼). 기본 3일.
//        POST /admin/rescued/sync?days=3&key=run-once

    @PostMapping("/sync")
    public ResponseEntity<String> syncRecent(@RequestParam(name = "days", defaultValue = "3") int days,
                                             @RequestParam("key") String key) {
        if (!ADMIN_KEY.equals(key)) return ResponseEntity.status(403).body("forbidden");

        long t0 = System.currentTimeMillis();
        int changed = service.syncRecentDays(days);
        long t1 = System.currentTimeMillis();

        String msg = String.format("sync(%dd) done, changedRows=%d, elapsed=%.2fs",
                days, changed, (t1 - t0) / 1000.0);
        log.info("[RescueAdminController] {}", msg);
        return ResponseEntity.ok(msg);
    }

    /**
     * DB/커넥션/샘플 rows 점검
     *   GET /admin/rescued/debug-db
     */
    @GetMapping("/debug-db")
    public ResponseEntity<?> debugDb() {
        Map<String, Object> out = new LinkedHashMap<>();
        try (Connection c = dataSource.getConnection()) {
            var md = c.getMetaData();
            out.put("dbProduct", md.getDatabaseProductName());
            out.put("dbVersion", md.getDatabaseProductVersion());
            out.put("jdbcUrl", md.getURL());
            out.put("dbUser", md.getUserName());
            out.put("catalog", c.getCatalog());
            out.put("schema", c.getSchema());
            out.put("autoCommit(conn)", c.getAutoCommit());

            try { out.put("DATABASE()", jdbcTemplate.queryForObject("SELECT DATABASE()", String.class)); } catch (Exception ignore) {}
            try { out.put("@@autocommit", jdbcTemplate.queryForObject("SELECT @@autocommit", Integer.class)); } catch (Exception ignore) {}
            try { out.put("@@tx_read_only", jdbcTemplate.queryForObject("SELECT @@tx_read_only", Integer.class)); } catch (Exception ignore) {}

            try {
                Long cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM rescued", Long.class);
                out.put("has_rescued_table", true);
                out.put("rescued_count", cnt);

                String sql =
                        "SELECT desertion_no, care_reg_no, up_kind_nm, kind_nm, protection_status, " +
                                "       rescue_date, move_date, updated_at " +
                                "  FROM rescued " +
                                " ORDER BY (updated_at IS NULL), updated_at DESC, id DESC " +
                                " LIMIT 10";
                List<Map<String, Object>> recent = jdbcTemplate.query(sql, (rs, i) -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("desertion_no", rs.getString("desertion_no"));
                    row.put("care_reg_no", rs.getString("care_reg_no"));
                    row.put("up_kind_nm", rs.getString("up_kind_nm"));
                    row.put("kind_nm", rs.getString("kind_nm"));
                    row.put("protection_status", rs.getString("protection_status"));
                    row.put("rescue_date", rs.getDate("rescue_date"));
                    row.put("move_date", rs.getDate("move_date"));
                    row.put("updated_at", rs.getTimestamp("updated_at"));
                    return row;
                });
                out.put("recent_rows", recent);
            } catch (Exception ex) {
                out.put("has_rescued_table", false);
                out.put("rescued_exists_error", ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }

            return ResponseEntity.ok(out);
        } catch (Exception e) {
            out.put("error", e.getClass().getSimpleName());
            out.put("message", e.getMessage());
            out.put("stacktraceTop", Arrays.stream(e.getStackTrace()).limit(5).map(StackTraceElement::toString).toList());
            return ResponseEntity.status(500).body(out);
        }
    }

    /**
     * 전체 보호소: 기간/상태/윈도우
     *   GET /admin/rescued/import-all?state=protect&window=30
     *   (기본: 최근 1년)
     */
    @GetMapping("/import-all")
    public ResponseEntity<?> importAll(
            @RequestParam(value = "bgnde", required = false) String bgnde,
            @RequestParam(value = "endde", required = false) String endde,
            @RequestParam(value = "state", required = false, defaultValue = "protect") String state,
            @RequestParam(value = "window", required = false, defaultValue = "30") int windowDays
    ) {
        LocalDate end = (endde != null) ? LocalDate.parse(endde, YMD) : LocalDate.now();
        LocalDate start = (bgnde != null) ? LocalDate.parse(bgnde, YMD) : end.minusYears(1);
        return ResponseEntity.ok(service.importAll(start, end, state, windowDays));
    }

    /**
     * 여러 보호소: 기간/상태/윈도우
     *   GET /admin/rescued/import?careRegNo=111&careRegNo=222&window=21
     */
    @GetMapping("/import")
    public ResponseEntity<?> importForList(
            @RequestParam("careRegNo") List<String> careRegNos,
            @RequestParam(value = "bgnde", required = false) String bgnde,
            @RequestParam(value = "endde", required = false) String endde,
            @RequestParam(value = "state", required = false, defaultValue = "protect") String state,
            @RequestParam(value = "window", required = false, defaultValue = "30") int windowDays
    ) {
        LocalDate end = (endde != null) ? LocalDate.parse(endde, YMD) : LocalDate.now();
        LocalDate start = (bgnde != null) ? LocalDate.parse(bgnde, YMD) : end.minusYears(1);
        return ResponseEntity.ok(service.importForList(careRegNos, start, end, state, windowDays));
    }

    /**
     * 단일 보호소: 기간/상태/윈도우
     *   GET /admin/rescued/import-one?careRegNo=326340201100001
     */
    @GetMapping("/import-one")
    public ResponseEntity<?> importOne(
            @RequestParam("careRegNo") String careRegNo,
            @RequestParam(value = "bgnde", required = false) String bgnde,
            @RequestParam(value = "endde", required = false) String endde,
            @RequestParam(value = "state", required = false, defaultValue = "protect") String state,
            @RequestParam(value = "window", required = false, defaultValue = "30") int windowDays
    ) {
        LocalDate end = (endde != null) ? LocalDate.parse(endde, YMD) : LocalDate.now();
        LocalDate start = (bgnde != null) ? LocalDate.parse(bgnde, YMD) : end.minusYears(1);
        int changed = service.importOne(careRegNo, start, end, state, windowDays);
        return ResponseEntity.ok(Map.of(
                "careRegNo", careRegNo,
                "from", start.toString(),
                "to", end.toString(),
                "state", state,
                "windowDays", windowDays,
                "changed", changed
        ));
    }

    /**
     * 적재 건수 확인
     *   GET /admin/rescued/count?careRegNo=326340201100001
     */
    @GetMapping("/count")
    public ResponseEntity<?> count(@RequestParam("careRegNo") String careRegNo) {
        return ResponseEntity.ok(Map.of(
                "careRegNo", careRegNo,
                "count", rescuedMapper.countByCareRegNo(careRegNo)
        ));
    }
}
