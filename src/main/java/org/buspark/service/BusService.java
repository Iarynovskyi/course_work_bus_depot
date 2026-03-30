package org.buspark.service;

import org.buspark.model.Bus;
import org.buspark.repository.BusRepository;
import java.time.LocalTime;
import java.util.List;

public class BusService {
    private final BusRepository repository = new BusRepository();

    public boolean registerBus(Bus bus) {
        if (repository.findById(bus.getBusNumber()) != null) {
            return false;
        }

        int initialStatus = calculateStatusByTime(bus.getDepartureTime(), bus.getArrivalTime());
        bus.setStatus(initialStatus);

        repository.saveOrUpdate(bus);
        return true;
    }

    private int calculateStatusByTime(LocalTime dep, LocalTime arr) {
        LocalTime now = LocalTime.now();
        boolean shouldBeOnRoute;

        if (dep.isBefore(arr)) {
            shouldBeOnRoute = now.isAfter(dep) && now.isBefore(arr);
        } else {
            shouldBeOnRoute = now.isAfter(dep) || now.isBefore(arr);
        }
        return shouldBeOnRoute ? 1 : 0;
    }

    public int checkSchedules() {
        LocalTime now = LocalTime.now();
        int changes = 0;

        List<Bus> allBuses = repository.findAll();
        for (Bus b : allBuses) {
            if (b.getStatus() == 0 && now.isAfter(b.getDepartureTime()) && now.isBefore(b.getArrivalTime())) {
                b.setStatus(1);
                repository.saveOrUpdate(b);
                changes++;
            }
            else if (b.getStatus() == 1 && (now.isAfter(b.getArrivalTime()) || now.isBefore(b.getDepartureTime()))) {
                b.setStatus(0);
                repository.saveOrUpdate(b);
                changes++;
            }
        }
        return changes;
    }

    public List<Bus> getBusesInDepot() { return repository.findByStatus(0); }
    public List<Bus> getBusesOnRoute() { return repository.findByStatus(1); }
}