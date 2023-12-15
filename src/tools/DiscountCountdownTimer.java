package tools;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscountCountdownTimer {
    private ScheduledExecutorService executorService;
    private volatile boolean stopTimer;

    public void startTimer() {
        stopTimer = false;

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (stopTimer) {
                executorService.shutdown();
                return;
            }

            // Calculate time until next discount (adjust according to your logic)
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime nextDiscountDateTime = LocalDateTime.of(currentDateTime.getYear(), Month.JUNE, 1, 0, 0);
            if (currentDateTime.getMonthValue() >= Month.JUNE.getValue()) {
                nextDiscountDateTime = nextDiscountDateTime.plusYears(1);
            }

            Duration duration = Duration.between(currentDateTime, nextDiscountDateTime);

            long secondsRemaining = duration.getSeconds();
            long months = secondsRemaining / (60 * 60 * 24 * 30);
            secondsRemaining %= (60 * 60 * 24 * 30);
            long weeks = secondsRemaining / (60 * 60 * 24 * 7);
            secondsRemaining %= (60 * 60 * 24 * 7);
            long days = secondsRemaining / (60 * 60 * 24);
            secondsRemaining %= (60 * 60 * 24);
            long hours = secondsRemaining / (60 * 60);
            secondsRemaining %= (60 * 60);
            long minutes = secondsRemaining / 60;
            long seconds = secondsRemaining % 60;

            System.out.println("Time until discount: " + months + " month(s) " + weeks + " week(s) " +
                    days + " day(s) " + hours + " hour(s) " + minutes + " minute(s) " + seconds + " second(s)");
        }, 0, 1, TimeUnit.SECONDS);
    }

}