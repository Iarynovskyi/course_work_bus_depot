package org.buspark.controller;

import org.buspark.model.Bus;
import org.buspark.service.BusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
@CrossOrigin(origins = "http://localhost:5173")
public class BusController {

    private final BusService service = new BusService();

    @GetMapping
    public List<Bus> getAllBuses() {
        List<Bus> allBuses = new ArrayList<>();
        allBuses.addAll(service.getBusesInDepot());
        allBuses.addAll(service.getBusesOnRoute());
        return allBuses;
    }

    @PostMapping
    public ResponseEntity<String> addBus(@RequestBody Bus bus) {
        try {
            boolean isRegistered = service.registerBus(bus);

            if (isRegistered) {
                return ResponseEntity.ok("Bus added successfully");
            } else {
                return ResponseEntity.status(400).body("Bus with number " + bus.getBusNumber() + " already exists!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> addBusesBulk(@RequestBody List<Bus> buses) {
        int addedCount = 0;
        int skippedCount = 0;

        for (Bus bus : buses) {
            boolean created = service.registerBus(bus);
            if (created) addedCount++;
            else skippedCount++;
        }

        return ResponseEntity.ok("Import finished: " + addedCount + " added, " + skippedCount + " skipped (already exist).");
    }
}