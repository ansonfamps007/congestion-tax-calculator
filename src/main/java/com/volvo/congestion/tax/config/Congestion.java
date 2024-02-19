package com.volvo.congestion.tax.config;

import com.volvo.congestion.tax.dto.TaxDetails;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Congestion property Configuration reader.
 */
@Data
@Primary
@Component
@ConfigurationProperties(prefix = "congestion")
public class Congestion {

    /**
     * hold the list of holidays of year 2013.
     */
    private List<String> holidays;

    /**
     * Tax details with respect to time.
     */
    private List<TaxDetails> tax;

    /**
     * list of tax-free vehicle.
     *
     * @implNote vehicle are taken from the properties since the scope may change according to the city.
     */
    private List<String> taxFreeVehicle;
}
