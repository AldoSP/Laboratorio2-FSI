package com.udea.labdrools.api.dto;

import com.udea.labdrools.model.Passenger;
import com.udea.labdrools.model.Seat;
import java.util.List;

public class AirlineEvaluationResponse {

    private final Passenger passenger;
    private final List<Seat> seats;

    public AirlineEvaluationResponse(Passenger passenger, List<Seat> seats) {
        this.passenger = passenger;
        this.seats = seats;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}
