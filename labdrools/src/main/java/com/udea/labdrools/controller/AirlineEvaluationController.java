package com.udea.labdrools.controller;

import com.udea.labdrools.api.dto.AirlineEvaluationRequest;
import com.udea.labdrools.api.dto.AirlineEvaluationResponse;
import com.udea.labdrools.model.Passenger;
import com.udea.labdrools.service.AirlineEvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/airline/api")
public class AirlineEvaluationController {

    private final AirlineEvaluationService evaluationService;

    public AirlineEvaluationController(AirlineEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/evaluate")
    public AirlineEvaluationResponse evaluate(@Valid @RequestBody AirlineEvaluationRequest request) {
        Passenger evaluatedPassenger = evaluationService.evaluate(request.getPassenger(), request.getFlight(), request.getSeats());
        return new AirlineEvaluationResponse(evaluatedPassenger, request.getSeats());
    }
}
