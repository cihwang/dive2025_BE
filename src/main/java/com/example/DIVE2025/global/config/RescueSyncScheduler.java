package com.example.DIVE2025.global.config;

import com.example.DIVE2025.domain.rescued.service.RescuedImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class RescueSyncScheduler {

    private final RescuedImportService service;

    // ==== 튜닝 가능한 옵션들 (application.yml 없으면 기본값 사용) ====
    @Value("${rescued.scheduler.enabled:true}")
    private boolean enabled;

    @Value("${rescued.scheduler.buffer-days:3}")
    private int bufferDays;

    // 최대 지터(초). 0이면 지터 비활성화.
    @Value("${rescued.scheduler.jitter-sec:0}")
    private int maxJitterSec;

    // 실패 시 재시도 횟수(추가 시도 수). 0이면 재시도 없음.
    @Value("${rescued.scheduler.retries:0}")
    private int retries;

    // 재시도 간 기본 대기(초). i번째 재시도에 i배로 증가(단순 점증).
    @Value("${rescued.scheduler.backoff-sec:20}")
    private int baseBackoffSec;

    // 스케줄 시간대(기본 서울)
    /**
     * 새벽 2시 동기화
     */
    //1분에 한번 스케줄링
//    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "${rescued.scheduler.cron-am:0 0 2 * * *}", zone = "${rescued.scheduler.timezone:Asia/Seoul}")
    public void syncAt2am() {
        runSafely("02:00");
    }

    @Value("${rescued.scheduler.timezone:Asia/Seoul}")
    private String tz;
    // 같은 작업 동시 실행 방지(동일 인스턴스 내)

    private final ReentrantLock runLock = new ReentrantLock();

    /**
     * 오후 2시 동기화
     */
    @Scheduled(cron = "${rescued.scheduler.cron-pm:0 0 14 * * *}", zone = "${rescued.scheduler.timezone:Asia/Seoul}")
    public void syncAt2pm() {
        runSafely("14:00");
    }

    // 필요할 때만 주석 해제해서 1회 초기 적재 트리거 가능(스케줄 아님)
    // @EventListener(ApplicationReadyEvent.class)
    // public void initialBuildOnce() {
    //     if (!enabled) return;
    //     try {
    //         int changed = service.initialBuild3YearsProtectOnly();
    //         log.info("[RescueSyncScheduler] initial 3y build done. changedRows={}", changed);
    //     } catch (Exception e) {
    //         log.error("[RescueSyncScheduler] initial build failed", e);
    //     }
    // }

    // ================== 내부 공용 실행기 ==================
    private void runSafely(String label) {
        if (!enabled) {
            log.info("[RescueSyncScheduler] {} skipped: scheduler disabled", label);
            return;
        }

        // 동일 인스턴스에서 겹치면 스킵
        if (!runLock.tryLock()) {
            log.warn("[RescueSyncScheduler] {} skipped: previous run still in progress", label);
            return;
        }

        long t0 = System.currentTimeMillis();
        try {
            // 지터(옵션): 여러 인스턴스일 때 API 부하 분산에 유용
            if (maxJitterSec > 0) {
                int jitter = ThreadLocalRandom.current().nextInt(0, maxJitterSec + 1);
                if (jitter > 0) {
                    log.info("[RescueSyncScheduler] {} adding jitter {}s", label, jitter);
                    sleepSec(jitter);
                }
            }

            int attempts = Math.max(1, 1 + Math.max(0, retries));
            int changed = 0;
            int attempt = 0;

            while (true) {
                attempt++;
                try {
                    changed = service.syncRecentDays(bufferDays);
                    long t1 = System.currentTimeMillis();
                    log.info("[RescueSyncScheduler] {} sync done: changedRows={}, elapsed={}ms, bufferDays={}",
                            label, changed, (t1 - t0), bufferDays);
                    break;
                } catch (Exception ex) {
                    if (attempt >= attempts) {
                        log.error("[RescueSyncScheduler] {} sync failed (final attempt {}/{}).",
                                label, attempt, attempts, ex);
                        break;
                    }
                    int wait = Math.max(1, baseBackoffSec) * attempt; // 점증 백오프
                    log.warn("[RescueSyncScheduler] {} sync attempt {}/{} failed: {} -> retry in {}s",
                            label, attempt, attempts, ex.toString(), wait);
                    sleepSec(wait);
                }
            }
        } finally {
            runLock.unlock();
        }
    }

    private void sleepSec(int sec) {
        try { TimeUnit.SECONDS.sleep(sec); } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
