package com.volvo.congestion.tax.service;

import com.volvo.congestion.tax.config.Congestion;
import com.volvo.congestion.tax.dto.TaxDetails;
import com.volvo.congestion.tax.dto.TaxRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Service layer for the Congestion Tax Calculation implementation.
 */
@Service
@Slf4j
public class CongestionTaxCalculatorServiceImpl implements CongestionTaxCalculatorService {

    private final Congestion congestionProperty;

    /**
     * Adding the dependency of congestion property.
     */
    @Autowired
    public CongestionTaxCalculatorServiceImpl(Congestion congestionProperty) {
        this.congestionProperty = congestionProperty;
    }

    /**
     * The toll-free vehicles for eliminating the tax calculation.
     */
    private Set<String> tollFreeVehicles;

    /**
     * holiday and advanced holiday for eliminating the tax calculation.
     *
     * @See constructHolidayAndAdvanceDay
     */
    private Set<LocalDate> holidayAndAdvancedDay;

    /**
     * post constructor for the Holiday and Advanced holidays to variable holidayAndAdvancedDay.
     *
     * @implNote this is used to precook the holiday and advanced holidays as part of performance improvement
     * and will get the value in O(n).
     */
    @PostConstruct
    private void constructHolidayAndAdvanceDay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<LocalDate> dates = this.congestionProperty.getHolidays().stream()
                .map(s -> LocalDate.parse(s, formatter))
                .toList();
        Set<LocalDate> previousDay = dates.stream()
                .map(localDate -> localDate.minusDays(1))
                .collect(Collectors.toSet());
        HashSet<LocalDate> tollFreeDates = new HashSet<>(dates);
        tollFreeDates.addAll(previousDay);
        this.holidayAndAdvancedDay = tollFreeDates;
    }

    /**
     * post constructor for the toll-free vehicle.
     * <p>
     * toll-free vehicles are converted to lower case to get the data with O(n) with Set -> contains
     */
    @PostConstruct
    private void constructTollFreeVehicle() {
        this.tollFreeVehicles = this.congestionProperty.getTaxFreeVehicle().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    /**
     * Calculate the tax for the vehicle and the given dates.
     * <p>
     * The dates are grouped in date for calculating every day tax.
     *
     * @param request vehicle and dates
     * @return calculated tax
     */
    @Override
    public Integer calculateCongestionTax(final TaxRequest request) {

        log.info("CongestionTaxCalculatorServiceImpl - calculateCongestionTax");

        var vehicle = request.getVehicle();

        Map<LocalDate, List<LocalDateTime>> groupDate = request.getDates().stream()
                .sorted()
                .collect(groupingBy(LocalDateTime::toLocalDate, toList()));
        int totalTax = 0;
        for (Map.Entry<LocalDate, List<LocalDateTime>> entry : groupDate.entrySet()) {
            List<LocalDateTime> vehicleTime = entry.getValue();
            LocalDateTime initTime = null;
            LocalDateTime initEndTime = null;
            int dailyTax = 0;
            List<Integer> tempValues = new ArrayList<>();
            for (LocalDateTime vehicleDate : vehicleTime) {
                //setting the value for first time
                if (initTime == null) {
                    initTime = vehicleDate;
                    initEndTime = initTime.plusMinutes(60).plusNanos(1);
                }
                //resetting and adding tax value if the time is beyond 60 minutes
                if (!vehicleDate.isBefore(initEndTime)) {
                    initTime = vehicleDate;
                    initEndTime = initTime.plusMinutes(60).plusNanos(1);
                    dailyTax = dailyTax + Collections.max(tempValues);
                    tempValues.clear();
                }
                tempValues.add(getTollFee(vehicleDate, vehicle));
            }
            dailyTax = dailyTax + Collections.max(tempValues);
            // calculating if the maximum value is reached beyond 60
            dailyTax = Math.min(dailyTax, 60);
            totalTax = totalTax + dailyTax;
        }
        return totalTax;
    }

    /**
     * Checks if the vehicle is toll-free vehicle.
     *
     * @param vehicle vehicle
     * @return is toll-free
     */
    public boolean isTollFreeVehicle(final String vehicle) {
        if (null == vehicle) {
            return false;
        } else {
            return (this.tollFreeVehicles.contains(vehicle.toLowerCase()));
        }
    }

    /**
     * Get the tax for a time and vehicle.
     * @implNote The toll slab can be configured in properties
     *
     * @param date    date
     * @param vehicle vehicle
     * @return tax
     */
    public int getTollFee(final LocalDateTime date, final String vehicle) {

        log.info("CongestionTaxCalculatorServiceImpl - getTollFee");

        if (isTollFreeDate(date) || isTollFreeVehicle(vehicle)) return 0;
        var vehicleTime = date.toLocalTime();
        Optional<TaxDetails> tax = this.congestionProperty.getTax().stream()
                .filter(t -> vehicleTime.isAfter(t.getStartTime().minusMinutes(1))
                        && vehicleTime.isBefore(t.getEndTime().plusMinutes(1)))
                .findFirst();
        return tax.map(TaxDetails::getTax).orElse(0);
    }

    /**
     * Check if it is a toll-free date.
     * <p>
     * Weekends(Saturday and Sunday), JULY month, Public holiday and
     * Previous holiday date is not considered for tax calculation
     *
     * @param date date
     * @return is tax-free date
     * @implNote Scope reduced to 2013
     */
    private boolean isTollFreeDate(final LocalDateTime date) {
        int year = date.getYear();
        Month month = date.getMonth();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == SUNDAY || dayOfWeek == SATURDAY) {
            return true;
        }
        if (year == 2013) {
            if (month == Month.JULY) {
                return true;
            }
            return holidayAndAdvancedDay.contains(date.toLocalDate());
        } else {
            return true;
        }
    }

}
