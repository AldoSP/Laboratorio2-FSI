package com.udea.labdrools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabdroolsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void showAirlineEvaluationForm() throws Exception {
		mockMvc.perform(get("/airline/form"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Evaluación de reglas de vuelo")));
	}

	@Test
	void submitAirlineFormShowsEvaluationResults() throws Exception {
		mockMvc.perform(post("/airline/evaluate")
				.param("passenger.membershipStatus", "Gold")
				.param("passenger.age", "30")
				.param("passenger.luggageWeightKg", "5")
				.param("passenger.seatPreference", "Window")
				.param("passenger.seatClass", "Economy")
				.param("flight.flightId", "FL-3")
				.param("flight.delayMinutes", "90")
				.param("flight.durationHours", "3")
				.param("seats[0].seatNumber", "14A")
				.param("seats[0].emergencyExit", "true")
				.param("seats[0].occupied", "false"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Evaluación completada")))
			.andExpect(content().string(containsString("Business")));
	}

	@Test
	void evaluateEnablesPriorityCheckInForSeniorPassenger() throws Exception {
		mockMvc.perform(post("/airline/api/evaluate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "passenger": {"flightId": "FL-1", "membershipStatus": "Basic", "age": 70},
					  "flight": {"flightId": "FL-1", "delayMinutes": 0, "durationHours": 1.5},
					  "seats": []
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.priorityCheckIn").value(true));
	}

	@Test
	void evaluateAssignsAndOccupiesAnAvailableEmergencyExitSeat() throws Exception {
		mockMvc.perform(post("/airline/api/evaluate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "passenger": {"flightId": "FL-2", "age": 25, "seatPreference": "Any"},
					  "flight": {"flightId": "FL-2", "delayMinutes": 0, "durationHours": 2.5},
					  "seats": [{"flightId": "FL-2", "seatNumber": "12A", "emergencyExit": true, "occupied": false}]
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.assignedSeatNumber").value("12A"))
			.andExpect(jsonPath("$.seats[0].occupied").value(true));
	}

	@Test
	void evaluateAppliesDiscountForLightLuggage() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-6", "luggageWeightKg": 9.5},
			  "flight": {"flightId": "FL-6", "delayMinutes": 0, "durationHours": 3},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.discountPercent").value(10.0));
	}

	@Test
	void evaluateDeniesUpgradeForOverweightLuggage() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-7", "membershipStatus": "Gold", "age": 35, "luggageWeightKg": 24, "seatClass": "Economy"},
			  "flight": {"flightId": "FL-7", "delayMinutes": 90, "durationHours": 2},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.upgradeDenied").value(true))
			.andExpect(jsonPath("$.passenger.seatClass").value("Economy"));
	}

	@Test
	void evaluateCompensatesForExtremeDelays() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-8", "compensationAmount": 0},
			  "flight": {"flightId": "FL-8", "delayMinutes": 181, "durationHours": 2},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.compensationAmount").value(200.0));
	}

	@Test
	void evaluateAwardsLongFlightLoyaltyPointsOnce() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-9", "membershipStatus": "Gold", "loyaltyPoints": 250},
			  "flight": {"flightId": "FL-9", "delayMinutes": 0, "durationHours": 6},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.loyaltyPoints").value(750))
			.andExpect(jsonPath("$.passenger.longFlightBonusAwarded").value(true));
	}

	@Test
	void evaluateRestrictsHeavyLuggageOnShortFlights() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-10", "membershipStatus": "Basic", "luggageWeightKg": 20},
			  "flight": {"flightId": "FL-10", "delayMinutes": 0, "durationHours": 1.5},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.luggageRestricted").value(true));
	}

	@Test
	void evaluateGrantsVipLoungeAccessToPlatinumMembers() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-11", "membershipStatus": "Platinum"},
			  "flight": {"flightId": "FL-11", "delayMinutes": 0, "durationHours": 2},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.vipLoungeAccess").value(true));
	}

	@Test
	void evaluateAssignsFamilyPreferredSeat() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-12", "age": 34, "travelingWithChildren": true, "seatPreference": "Any"},
			  "flight": {"flightId": "FL-12", "delayMinutes": 0, "durationHours": 2},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.assignedSeatType").value("FAMILY_PREFERRED"));
	}

	@Test
	void evaluateDoesNotTriggerStrictRulesAtTheirThresholds() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-13", "membershipStatus": "Gold", "age": 65, "luggageWeightKg": 10, "seatClass": "Economy", "loyaltyPoints": 100},
			  "flight": {"flightId": "FL-13", "delayMinutes": 60, "durationHours": 5},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.priorityCheckIn").value(false))
			.andExpect(jsonPath("$.passenger.discountPercent").value(0.0))
			.andExpect(jsonPath("$.passenger.seatClass").value("Economy"))
			.andExpect(jsonPath("$.passenger.loyaltyPoints").value(100));
	}

	@Test
	void evaluateAllowsUpgradeAtMaximumLuggageWeight() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-14", "membershipStatus": "Gold", "age": 35, "luggageWeightKg": 23, "seatClass": "Economy"},
			  "flight": {"flightId": "FL-14", "delayMinutes": 61, "durationHours": 3},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.upgradeDenied").value(false))
			.andExpect(jsonPath("$.passenger.seatClass").value("Business"));
	}

	@Test
	void evaluateLeavesOccupiedEmergencyExitSeatUnassigned() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-15", "age": 25, "seatPreference": "Any"},
			  "flight": {"flightId": "FL-15", "delayMinutes": 0, "durationHours": 3},
			  "seats": [{"flightId": "FL-15", "seatNumber": "12A", "emergencyExit": true, "occupied": true}]
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.assignedSeatNumber").value(nullValue()))
			.andExpect(jsonPath("$.seats[0].occupied").value(true));
	}

	@Test
	void evaluateDoesNotCompensateAtExactly180Minutes() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-16", "compensationAmount": 0},
			  "flight": {"flightId": "FL-16", "delayMinutes": 180, "durationHours": 3},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.compensationAmount").value(0.0));
	}

	@Test
	void evaluateDoesNotAwardLongFlightBonusAtExactlyFiveHours() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-17", "membershipStatus": "Gold", "loyaltyPoints": 100},
			  "flight": {"flightId": "FL-17", "delayMinutes": 0, "durationHours": 5},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.loyaltyPoints").value(100))
			.andExpect(jsonPath("$.passenger.longFlightBonusAwarded").value(false));
	}

	@Test
	void evaluateDoesNotRestrictLuggageAt15Kg() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-18", "luggageWeightKg": 15},
			  "flight": {"flightId": "FL-18", "delayMinutes": 0, "durationHours": 1.5},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.luggageRestricted").value(false));
	}

	@Test
	void evaluateKeepsSpecificSeatPreferenceForFamilies() throws Exception {
		evaluate("""
			{
			  "passenger": {"flightId": "FL-19", "travelingWithChildren": true, "seatPreference": "Window"},
			  "flight": {"flightId": "FL-19", "delayMinutes": 0, "durationHours": 2},
			  "seats": []
			}
			""")
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passenger.assignedSeatType").value(nullValue()));
	}

	private ResultActions evaluate(String request) throws Exception {
		return mockMvc.perform(post("/airline/api/evaluate")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));
	}

}
