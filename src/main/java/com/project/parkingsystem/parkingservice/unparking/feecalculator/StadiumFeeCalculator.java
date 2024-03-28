package com.project.parkingsystem.parkingservice.unparking.feecalculator;

import com.project.parkingsystem.parkingservice.unparking.FeeModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StadiumFeeCalculator implements FeeCalculator {

    @Override
    public double calculateFee(Long hours, List<FeeModel> feeModels) {
        double totalFee = 0;
        //double totalHours = Math.ceil(hours);
        double duration = hours;

        for(FeeModel feeModel : feeModels) {
            if(feeModel.isStartInclusive()) {
                if (feeModel.getStart() < hours) {
                    totalFee += feeModel.getBaseFee();
                    if (feeModel.getEnd() != null) {
                        if (!feeModel.isEndInclusive()) {
                            if (feeModel.getEnd() <= hours) {
                                double intervalDifference = feeModel.getEnd() - feeModel.getStart();
                                duration -= intervalDifference;
                            }
                        }
                    }
                    totalFee += duration * feeModel.getHourlyRate();
                }
            }
        }

        return totalFee;
    }
}
