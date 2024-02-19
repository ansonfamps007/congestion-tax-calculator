package com.volvo.congestion.tax.controller;

import com.volvo.congestion.tax.dto.TaxRequest;
import com.volvo.congestion.tax.service.CongestionTaxCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest end points to calculate the tax.
 */
@RestController
@RequestMapping("api/v1/congestions/tax")
@Slf4j
public class TaxController {

    private final CongestionTaxCalculatorService taxService;

    /**
     * Adding the dependency of Tax service.
     */
    @Autowired
    public TaxController(CongestionTaxCalculatorService taxService) {
        this.taxService = taxService;
    }

    /**
     * Http Post end point for calculate the tax of the vehicles.
     *
     * @param request vehicle and dates
     * @return calculated tax
     */
    @PostMapping
    public ResponseEntity<Integer> calculateTax(@RequestBody final TaxRequest request) {
        log.info("TaxController - calculateTax {} ");
        return ResponseEntity.ok(taxService.calculateCongestionTax(request));
    }

}