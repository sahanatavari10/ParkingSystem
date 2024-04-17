package com.project.parkingsystem.parkingservice.unparking;

import com.project.parkingsystem.parkingservice.commons.ParkingSpot;
import com.project.parkingsystem.parkingservice.commons.ParkingSpotRepository;
import com.project.parkingsystem.parkingservice.commons.ParkingTicket;
import com.project.parkingsystem.parkingservice.commons.ParkingTicketRepository;
import com.project.parkingsystem.parkingservice.unparking.feecalculator.AirportFeeCalculator;
import com.project.parkingsystem.parkingservice.unparking.feecalculator.FeeCalculator;
import com.project.parkingsystem.parkingservice.unparking.feecalculator.MallFeeCalculator;
import com.project.parkingsystem.parkingservice.unparking.feecalculator.StadiumFeeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UnparkingService {

    private final ParkingTicketRepository parkingTicketRepository;
    private final FeeModelRepository feeModelRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final MallFeeCalculator mallFeeCalculator;
    private final StadiumFeeCalculator stadiumFeeCalculator;
    private final AirportFeeCalculator airportFeeCalculator;

    FeeCalculator feeCalculator;

    private final Map<Long, FeeCalculator> feeCalculatorMap = new HashMap<>();

    @Autowired
    public UnparkingService(ParkingTicketRepository parkingTicketRepository, FeeModelRepository feeModelRepository, ParkingSpotRepository parkingSpotRepository, MallFeeCalculator mallFeeCalculator, StadiumFeeCalculator stadiumFeeCalculator, AirportFeeCalculator airportFeeCalculator) {
        this.parkingTicketRepository = parkingTicketRepository;
        this.feeModelRepository = feeModelRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.mallFeeCalculator = mallFeeCalculator;
        this.stadiumFeeCalculator = stadiumFeeCalculator;
        this.airportFeeCalculator = airportFeeCalculator;
    }

    public ResponseEntity<?> unparkVehicle(UnparkingRequest request) {
        Optional<ParkingTicket> optionalParkingTicket = parkingTicketRepository.findById(request.getTicketId());
        if (optionalParkingTicket.isEmpty()) {
            UnparkingResponse response = new UnparkingResponse("Parking ticket not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ParkingTicket parkingTicket = optionalParkingTicket.get();

        LocalDateTime entryTime = parkingTicket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();

        Duration duration = Duration.between(entryTime, exitTime);

        if(duration.toMinutesPart()>0){
            duration = duration.plusHours(1);
        }

        long hours = (long) Math.ceil(duration.toHours());


        List<FeeModel> feeModels = feeModelRepository.findByParkingEntityIdAndVehicleTypeId(parkingTicket.getParkingSpot().getParkingLot().getParkingEntity().getId(), parkingTicket.getParkingSpot().getVehicleType().getId());
        if (feeModels.isEmpty()) {
            UnparkingResponse response = new UnparkingResponse("Parking entity or Vehicle type not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        feeCalculatorMap.put(1L, mallFeeCalculator);
        feeCalculatorMap.put(2L, stadiumFeeCalculator);
        feeCalculatorMap.put(3L, airportFeeCalculator);

        feeCalculator = feeCalculatorMap.get(parkingTicket.getParkingSpot().getParkingLot().getParkingEntity().getId());
        if (feeCalculator == null) {
            UnparkingResponse response = new UnparkingResponse("Parking entity not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        double fee = feeCalculator.calculateFee(hours, feeModels);

        parkingTicket.setExitTime(exitTime);
        parkingTicket.setAmount(fee);
        parkingTicketRepository.save(parkingTicket);

        ParkingSpot parkingSpot = parkingTicket.getParkingSpot();
        parkingSpot.setOccupied(false);
        Long spot = parkingSpot.getId();

        parkingSpotRepository.save(parkingSpot);

        UnparkingResponse response = new UnparkingResponse();
        response.setTicketId(parkingTicket.getId());
        response.setEntryTime(entryTime);
        response.setExitTime(exitTime);
        response.setAmount(fee);
        response.setParkingSpotId(spot);
        response.setMessage("Vehicle is unparked");

        return ResponseEntity.ok().body(response);
    }
}
