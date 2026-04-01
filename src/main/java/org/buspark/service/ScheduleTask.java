package org.buspark.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {
    private final BusService service = new BusService();

    @Scheduled(fixedRateString = "${SCHEDULER_RATE:60000}")
    public void autoUpdateBuses() {
        System.out.println("Cloud Scheduler: Checking bus statuses...");
        int changes = service.checkSchedules();
        if (changes > 0) {
            System.out.println("Cloud Scheduler: Updated " + changes + " buses.");
        }
    }
}