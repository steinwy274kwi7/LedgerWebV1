package kr.co.ledger.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import kr.co.ledger.service.UserService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DormantBatchListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
       
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> UserService.getInstance().processDormantUsers();

        scheduler.scheduleAtFixedRate(task, 1, 24 * 60, TimeUnit.MINUTES);
        System.out.println("[시스템] 휴면 계정 자동 전환 스케줄러 가동 완료 (web.xml 설정)");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
            System.out.println("[시스템] 휴면 계정 자동 전환 스케줄러 정상 종료");
        }
    }
}