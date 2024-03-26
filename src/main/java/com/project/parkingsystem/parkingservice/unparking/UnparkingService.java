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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UnparkingService {

    @Autowired
    private ParkingTicketRepository parkingTicketRepository;

    @Autowired
    private FeeModelRepository feeModelRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private MallFeeCalculator mallFeeCalculator;

    @Autowired
    private StadiumFeeCalculator stadiumFeeCalculator;

    @Autowired
    private AirportFeeCalculator airportFeeCalculator;

    FeeCalculator feeCalculator;

    private final Map<Long, FeeCalculator> feeCalculatorMap = new HashMap<>();

    public UnparkingResponse unparkVehicle(UnparkingRequest request) {
        Optional<ParkingTicket> optionalParkingTicket = parkingTicketRepository.findById(request.getTicketId());
        if (optionalParkingTicket.isEmpty()) {
            return new UnparkingResponse("Parking ticket not found");
        }
        ParkingTicket parkingTicket = optionalParkingTicket.get();

        LocalDateTime entryTime = parkingTicket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();

        Duration duration = Duration.between(entryTime, exitTime);

        long hours = (long) Math.ceil(duration.toHours());

        List<FeeModel> feeModels = feeModelRepository.findByParkingEntityIdAndVehicleTypeId(
                parkingTicket.getParkingSpot().getParkingLot().getParkingEntity().getId(),
                parkingTicket.getParkingSpot().getVehicleType().getId());
        if (feeModels.isEmpty()) {
            return new UnparkingResponse("Fee model not found");
        }

        feeCalculatorMap.put(1L, mallFeeCalculator);
        feeCalculatorMap.put(2L, stadiumFeeCalculator);
        feeCalculatorMap.put(3L, airportFeeCalculator);

        feeCalculator = feeCalculatorMap.get(parkingTicket.getParkingSpot().getParkingLot().getParkingEntity().getId());
        if (feeCalculator == null) {
            return new UnparkingResponse("Invalid parking entity");
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

        return response;
    }
}