package com.volvo.congestion.tax.service;

import com.volvo.congestion.tax.dto.TaxRequest;

/**
 * Service layer for the Congestion Tax Calculation.
 */
public interface CongestionTaxCalculatorService {

    /**
     * Calculate the tax for the vehicle and the given dates.
     *
     * @param request vehicle and dates
     * @return calculated tax
     */
    Integer calculateCongestionTax(TaxRequest request) ;
}
