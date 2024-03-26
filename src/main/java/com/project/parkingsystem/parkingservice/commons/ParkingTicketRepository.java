package com.project.parkingsystem.parkingservice.commons;

import com.project.parkingsystem.parkingservice.commons.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
}
