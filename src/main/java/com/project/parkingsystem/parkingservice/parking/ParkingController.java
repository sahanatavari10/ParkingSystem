package com.project.parkingsystem.parkingservice.parking;

import com.project.parkingsystem.parkingservice.commons.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    //GET /availableSpots?parkingLotName=mallA&vehicleType=car
    @GetMapping("/availableSpots")
    public ResponseEntity<?> availableSpots(@RequestParam String parkingLotName, @RequestParam String vehicle) {
        ResponseEntity<?> response = parkingService.getAvailableSpots(parkingLotName, vehicle);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    //POST /parking
    @PostMapping("/parking")
    public ResponseEntity<?> parking(@RequestBody ParkingRequest request) {
        ResponseEntity<?> response = parkingService.parkVehicle(request);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
