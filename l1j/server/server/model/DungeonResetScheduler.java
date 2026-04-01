package l1j.server.server.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import l1j.server.server.TimeController.DungeonQuitController;

public class DungeonResetScheduler {

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        scheduleNext();
    }

    private static void scheduleNext() {
        long delay = getDelayToNextReset();
        scheduler.schedule(() -> {
            try {
                resetDungeon();
            } finally {
                scheduleNext(); // 다음 회차 예약
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private static long getDelayToNextReset() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now
                .withHour(20)
                .withMinute(0)
                .withSecond(0);

        if (now.compareTo(next) >= 0) {
            next = next.plusDays(1);
        }

        return Duration.between(now, next).toMillis();
    }

    private static void resetDungeon() {
        DungeonQuitController.getInstance().isgameStart = true;
        System.out.println("모든 던전시간 초기화 완료");
    }
}
