package com.udea.labdrools.service;

import com.udea.labdrools.model.Flight;
import com.udea.labdrools.model.Passenger;
import com.udea.labdrools.model.Seat;
import java.util.List;
import java.util.Objects;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
public class AirlineEvaluationService {

    private final KieContainer kieContainer;

    public AirlineEvaluationService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public Passenger evaluate(Passenger passenger, Flight flight, List<Seat> seats) {
        Objects.requireNonNull(passenger, "passenger must not be null");
        Objects.requireNonNull(flight, "flight must not be null");
        Objects.requireNonNull(seats, "seats must not be null");

        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(passenger);
            kieSession.insert(flight);
            seats.forEach(kieSession::insert);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        return passenger;
    }
}
