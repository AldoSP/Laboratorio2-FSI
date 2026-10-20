package com.udea.labdrools.model;

public class Seat {

    private String flightId;
    private String seatNumber;
    private boolean emergencyExit;
    private boolean occupied;

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isEmergencyExit() {
        return emergencyExit;
    }

    public void setEmergencyExit(boolean emergencyExit) {
        this.emergencyExit = emergencyExit;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
