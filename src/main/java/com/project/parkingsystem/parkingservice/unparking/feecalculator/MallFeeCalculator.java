package com.project.parkingsystem.parkingservice.unparking.feecalculator;

import com.project.parkingsystem.parkingservice.unparking.FeeModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallFeeCalculator implements FeeCalculator {
    public double calculateFee(Long hours, List<FeeModel> feeModels) {
        double totalFee = 0;
        //double totalHours = Math.ceil(hours);
        double duration = hours;

        for(FeeModel feeModel : feeModels) {
            totalFee+=duration*feeModel.getHourlyRate();
        }

        return totalFee;
    }
}
