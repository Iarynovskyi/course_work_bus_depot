package org.buspark.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.temporal.ChronoUnit;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "buses")
public class Bus {
    @Id
    @Column(name = "bus_number")
    private String busNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "route_number")
    private int routeNumber;

    @Column(name = "status")
    private int status;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "departure_time")
    private LocalTime departureTime;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    public Bus() {}

    public Bus(String busNumber, String firstName, String lastName, int routeNumber, LocalTime dep, LocalTime arr) {
        this.busNumber = busNumber.toUpperCase();
        this.firstName = firstName;
        this.lastName = lastName;
        this.routeNumber = routeNumber;
        this.departureTime = dep.truncatedTo(ChronoUnit.MINUTES);
        this.arrivalTime = arr.truncatedTo(ChronoUnit.MINUTES);
        this.status = 0;
    }

    public String getBusNumber() { return busNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getRouteNumber() { return routeNumber; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime.truncatedTo(ChronoUnit.MINUTES);
    }
    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime.truncatedTo(ChronoUnit.MINUTES);
    }
}