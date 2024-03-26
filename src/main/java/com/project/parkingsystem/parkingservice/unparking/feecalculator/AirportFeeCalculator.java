package com.project.parkingsystem.parkingservice.unparking.feecalculator;

import com.project.parkingsystem.parkingservice.unparking.FeeModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportFeeCalculator implements FeeCalculator {

    public double calculateFee(Long hours, List<FeeModel> feeModels) {
        double totalFee = 0;
        double duration = hours;

        for (FeeModel feeModel : feeModels) {
            // Check if the parking duration falls within the current interval
            if (feeModel.getStart() <= hours) {
                totalFee += feeModel.getBaseFee();
                if (feeModel.getEnd() != null) {
                    if (feeModel.getEnd() <= hours) {
                        double intervalDifference = feeModel.getEnd() - feeModel.getStart();
                        duration -= intervalDifference;
                    }
                }
            }
            totalFee += Math.ceil(duration / 24) * feeModel.getHourlyRate();
        }

        return totalFee;
    }
}
