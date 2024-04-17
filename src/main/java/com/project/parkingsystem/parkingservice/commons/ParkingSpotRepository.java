package com.project.parkingsystem.parkingservice.commons;

import com.project.parkingsystem.parkingservice.parking.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    Optional<ParkingSpot> findFirstByParkingLotIdAndVehicleTypeIdAndOccupiedFalse(Long parkingLotId, Long vehicleTypeId);

    int countAvailableSpotsByParkingLotNameAndVehicleType(String parkingLotName, VehicleType vehicleType);
}
