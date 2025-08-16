package com.example.DIVE2025.config;

import com.example.DIVE2025.domain.rescued.service.RescuedImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class RescueSyncScheduler {

    private final RescuedImportService service;

    /**
     * 새벽 2시 동기화
     * - 최근 3일 버퍼를 두고 동기화(공공 API 갱신 지연/네트워크 문제 대비)
     * - 실패해도 애플리케이션이 죽지 않도록 try-catch 처리
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void syncAt2am() {
        try {
            int changed = service.syncRecentDays(3);
            log.info("[RescueSyncScheduler] 02:00 sync done. changedRows={}", changed);
        } catch (Exception e) {
            log.error("[RescueSyncScheduler] 02:00 sync failed", e);
        }
    }

    /**
     * 오후 2시 동기화
     * - 정책 동일: 최근 3일 버퍼로 동기화
     */
    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    public void syncAt2pm() {
        try {
            int changed = service.syncRecentDays(3);
            log.info("[RescueSyncScheduler] 14:00 sync done. changedRows={}", changed);
        } catch (Exception e) {
            log.error("[RescueSyncScheduler] 14:00 sync failed", e);
        }
    }


//     (옵션) 초기에 DB를 한 번 구성해야 할 때, 임시로 주석 풀고 배포 후 1회 실행하고 다시 주석 처리.

//    @EventListener(ApplicationReadyEvent.class)
//    public void initialBuildOnce() {
//        try {
//            int changed = service.initialBuild3YearsProtectOnly();
//            log.info("[RescueSyncScheduler] initial 3y build done. changedRows={}", changed);
//        } catch (Exception e) {
//            log.error("[RescueSyncScheduler] initial build failed", e);
//        }
//    }
    
}
