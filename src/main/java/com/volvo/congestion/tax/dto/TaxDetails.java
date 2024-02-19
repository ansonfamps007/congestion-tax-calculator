package com.volvo.congestion.tax.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * Tax details with hours and amounts for congestion tax.
 */
@Data
public class TaxDetails {

    /**
     * Start time for the tax slab.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    /**
     * End time for the tax slab.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;

    /**
     * Tax Slab.
     */
    private Integer tax;
}
