package com.project.parkingsystem.parkingservice.unparking.feecalculator;

import com.project.parkingsystem.parkingservice.unparking.FeeModel;

import java.util.List;

public interface FeeCalculator {
    double calculateFee(Long hours, List<FeeModel> feeModels);
}
