package com.udea.labdrools.model;

public class Passenger {

    private String flightId;
    private String membershipStatus;
    private int age;
    private double luggageWeightKg;
    private String seatPreference;
    private String seatClass;
    private boolean priorityCheckIn;
    private double discountPercent;
    private boolean upgradeDenied;
    private double compensationAmount;
    private int loyaltyPoints;
    private boolean longFlightBonusAwarded;
    private boolean luggageRestricted;
    private boolean vipLoungeAccess;
    private boolean travelingWithChildren;
    private String assignedSeatNumber;
    private String assignedSeatType;

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getLuggageWeightKg() {
        return luggageWeightKg;
    }

    public void setLuggageWeightKg(double luggageWeightKg) {
        this.luggageWeightKg = luggageWeightKg;
    }

    public String getSeatPreference() {
        return seatPreference;
    }

    public void setSeatPreference(String seatPreference) {
        this.seatPreference = seatPreference;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public boolean isPriorityCheckIn() {
        return priorityCheckIn;
    }

    public void setPriorityCheckIn(boolean priorityCheckIn) {
        this.priorityCheckIn = priorityCheckIn;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public boolean isUpgradeDenied() {
        return upgradeDenied;
    }

    public void setUpgradeDenied(boolean upgradeDenied) {
        this.upgradeDenied = upgradeDenied;
    }

    public double getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(double compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public boolean isLongFlightBonusAwarded() {
        return longFlightBonusAwarded;
    }

    public void setLongFlightBonusAwarded(boolean longFlightBonusAwarded) {
        this.longFlightBonusAwarded = longFlightBonusAwarded;
    }

    public boolean isLuggageRestricted() {
        return luggageRestricted;
    }

    public void setLuggageRestricted(boolean luggageRestricted) {
        this.luggageRestricted = luggageRestricted;
    }

    public boolean isVipLoungeAccess() {
        return vipLoungeAccess;
    }

    public void setVipLoungeAccess(boolean vipLoungeAccess) {
        this.vipLoungeAccess = vipLoungeAccess;
    }

    public boolean isTravelingWithChildren() {
        return travelingWithChildren;
    }

    public void setTravelingWithChildren(boolean travelingWithChildren) {
        this.travelingWithChildren = travelingWithChildren;
    }

    public String getAssignedSeatNumber() {
        return assignedSeatNumber;
    }

    public void setAssignedSeatNumber(String assignedSeatNumber) {
        this.assignedSeatNumber = assignedSeatNumber;
    }

    public String getAssignedSeatType() {
        return assignedSeatType;
    }

    public void setAssignedSeatType(String assignedSeatType) {
        this.assignedSeatType = assignedSeatType;
    }
}
