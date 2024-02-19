package com.volvo.congestion.tax.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tax Request for calculating the tax.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest {

    /**
     * Vehicle type.
     */
    private String vehicle;

    /**
     * Dates of vehicle entry for which the tax to be calculated.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> dates;
}
