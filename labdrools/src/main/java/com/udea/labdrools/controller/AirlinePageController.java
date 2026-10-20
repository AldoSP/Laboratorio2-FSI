package com.udea.labdrools.controller;

import com.udea.labdrools.api.dto.AirlineEvaluationRequest;
import com.udea.labdrools.api.dto.AirlineEvaluationResponse;
import com.udea.labdrools.model.Flight;
import com.udea.labdrools.model.Passenger;
import com.udea.labdrools.model.Seat;
import com.udea.labdrools.service.AirlineEvaluationService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AirlinePageController {

    private final AirlineEvaluationService evaluationService;

    public AirlinePageController(AirlineEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @ModelAttribute("request")
    public AirlineEvaluationRequest createRequest() {
        AirlineEvaluationRequest request = new AirlineEvaluationRequest();

        Passenger passenger = new Passenger();
        passenger.setMembershipStatus("Basic");
        passenger.setAge(30);
        passenger.setSeatClass("Economy");
        passenger.setSeatPreference("Any");
        request.setPassenger(passenger);

        Flight flight = new Flight();
        flight.setFlightId("FL-001");
        flight.setDurationHours(2.0);
        request.setFlight(flight);

        Seat seat = new Seat();
        seat.setFlightId(flight.getFlightId());
        seat.setSeatNumber("12A");
        seat.setEmergencyExit(true);
        request.setSeats(new ArrayList<>(java.util.List.of(seat)));

        return request;
    }

    @GetMapping("/airline/form")
    public String showForm() {
        return "airline_form";
    }

    @PostMapping("/airline/evaluate")
    public String evaluate(
        @Valid @ModelAttribute("request") AirlineEvaluationRequest request,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "airline_form";
        }

        String flightId = request.getFlight().getFlightId();
        Passenger passenger = request.getPassenger();
        passenger.setFlightId(flightId);
        request.getSeats().forEach(seat -> seat.setFlightId(flightId));

        Passenger evaluatedPassenger = evaluationService.evaluate(passenger, request.getFlight(), request.getSeats());
        model.addAttribute("evaluation", new AirlineEvaluationResponse(evaluatedPassenger, request.getSeats()));
        return "airline_result";
    }
}
