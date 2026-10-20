package com.udea.labdrools.api.dto;

import com.udea.labdrools.model.Flight;
import com.udea.labdrools.model.Passenger;
import com.udea.labdrools.model.Seat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AirlineEvaluationRequest {

    @NotNull
    @Valid
    private Passenger passenger;

    @NotNull
    @Valid
    private Flight flight;

    @NotNull
    private List<@NotNull @Valid Seat> seats;

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
