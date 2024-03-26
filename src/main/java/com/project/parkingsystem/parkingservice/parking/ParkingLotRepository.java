package com.project.parkingsystem.parkingservice.parking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    ParkingLot findByNameAndParkingEntityId(String lotName, Long parkingEntityName);
}
