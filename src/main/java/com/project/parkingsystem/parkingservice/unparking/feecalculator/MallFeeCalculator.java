package com.project.parkingsystem.parkingservice.unparking.feecalculator;

import com.project.parkingsystem.parkingservice.unparking.FeeModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallFeeCalculator implements FeeCalculator {
    public double calculateFee(Long hours, List<FeeModel> feeModels) {
        double totalFee = 0;

        for(FeeModel feeModel : feeModels) {
            totalFee+=hours*feeModel.getHourlyRate();
        }
        return totalFee;
    }
}
