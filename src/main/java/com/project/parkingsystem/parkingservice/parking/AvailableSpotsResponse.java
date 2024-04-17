package com.project.parkingsystem.parkingservice.parking;

public class AvailableSpotsResponse {

    private int availableSpots;

    public AvailableSpotsResponse(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }
}
