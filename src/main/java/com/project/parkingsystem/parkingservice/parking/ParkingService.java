package com.project.parkingsystem.parkingservice.parking;

import com.project.parkingsystem.parkingservice.commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ParkingService {
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingEntityRepository parkingEntityRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingTicketRepository parkingTicketRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    public ParkingService(ParkingLotRepository parkingLotRepository,
                          ParkingEntityRepository parkingEntityRepository,
                          ParkingSpotRepository parkingSpotRepository,
                          ParkingTicketRepository parkingTicketRepository,
                          VehicleTypeRepository vehicleTypeRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingEntityRepository = parkingEntityRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingTicketRepository = parkingTicketRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public ResponseEntity<?> getAvailableSpots(@RequestParam String parkingLotName, @RequestParam String vehicle) {

        VehicleType vehicleType = vehicleTypeRepository.findByName(vehicle);

        ParkingLot parkingLot = parkingLotRepository.findByName(parkingLotName);

        if(vehicleType == null || parkingLot == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid values");
        }

        int availableSpots = parkingSpotRepository.countAvailableSpotsByParkingLotNameAndVehicleType(parkingLotName, vehicleType);

        if(availableSpots == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available parking spots");
        }

        AvailableSpotsResponse response = new AvailableSpotsResponse(availableSpots);

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<?> parkVehicle(ParkingRequest request) {

        ParkingEntity parkingEntity = parkingEntityRepository.findByName(request.getParkingEntityName());
        if (parkingEntity == null) {
            ParkingResponse response = new ParkingResponse("Parking Entity not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ParkingLot lot = parkingLotRepository.findByNameAndParkingEntityId(request.getParkingLotName(), parkingEntity.getId());
        if (lot == null) {
            ParkingResponse response = new ParkingResponse("Parking Lot not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        VehicleType vehicleType = vehicleTypeRepository.findByName(request.getVehicleType());
        if (vehicleType == null) {
            ParkingResponse response = new ParkingResponse("Vehicle Type not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<ParkingSpot> optionalParkingSpot = parkingSpotRepository.findFirstByParkingLotIdAndVehicleTypeIdAndOccupiedFalse(lot.getId(), vehicleType.getId());

        if (optionalParkingSpot.isEmpty()) {
            ParkingResponse response = new ParkingResponse("No available parking spots");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ParkingSpot parkingSpot = optionalParkingSpot.get();

        parkingSpot.setOccupied(true);
        parkingSpotRepository.save(parkingSpot);

        ParkingTicket parkingTicket = new ParkingTicket();
        parkingTicket.setParkingSpot(parkingSpot);
        parkingTicket.setEntryTime(LocalDateTime.now());
        parkingTicket.setCreatedAt(LocalDateTime.now());
        parkingTicket.setLastUpdatedAt(LocalDateTime.now());
        parkingTicketRepository.save(parkingTicket);

        ParkingResponse response = new ParkingResponse();
        response.setTicketId(parkingTicket.getId().toString());
        response.setParkingSpotId(parkingSpot.getId().toString());
        response.setEntryTime(parkingTicket.getEntryTime().toString());
        response.setMessage("Vehicle is parked");
        return ResponseEntity.ok().body(response);
    }
}