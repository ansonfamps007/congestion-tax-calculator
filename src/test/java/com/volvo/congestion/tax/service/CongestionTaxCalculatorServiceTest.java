package com.volvo.congestion.tax.service;

import com.volvo.congestion.tax.dto.TaxRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CongestionTaxCalculatorServiceTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private CongestionTaxCalculatorService congestionTaxCalculatorService;

    @Test
    void shouldCalculateTaxTestWithinMaxOneHour() {
        List<LocalDateTime> dates = List.of(parse("2013-02-08 06:59:00", formatter),
                parse("2013-02-08 07:00:00", formatter), parse("2013-02-08 07:59:00", formatter));
        TaxRequest request = new TaxRequest("car", dates);
        var taxOutput = congestionTaxCalculatorService.calculateCongestionTax(request);
        assertEquals(18, taxOutput);
    }

    @Test
    void shouldCalculateTaxTestWithinMaxOneHourEndPointForDifferentDays() {
        List<LocalDateTime> dates = List.of(parse("2013-02-08 06:59:00", formatter),
                parse("2013-02-07 07:00:00", formatter), parse("2013-02-06 07:59:00", formatter));
        TaxRequest request = new TaxRequest("car", dates);
        var taxOutput = congestionTaxCalculatorService.calculateCongestionTax(request);
        assertEquals(49, taxOutput);
    }

    @Test
    void shouldCalculateTaxTestMaxTaxOfDay() {

        List<LocalDateTime> dates = List.of(parse("2013-02-08 05:59:00", formatter),
                parse("2013-02-08 06:59:00", formatter), parse("2013-02-08 07:59:00", formatter),
                parse("2013-02-08 07:58:00", formatter), parse("2013-02-08 08:58:00", formatter),
                parse("2013-02-08 09:58:00", formatter), parse("2013-02-08 13:58:00", formatter),
                parse("2013-02-08 14:58:00", formatter), parse("2013-02-08 15:58:00", formatter),
                parse("2013-02-08 16:58:00", formatter), parse("2013-02-08 17:58:00", formatter),
                parse("2013-02-08 18:58:00", formatter), parse("2013-02-08 19:58:00", formatter),
                parse("2013-02-08 20:58:00", formatter));

        TaxRequest request = new TaxRequest("car", dates);
        var taxOutput = congestionTaxCalculatorService.calculateCongestionTax(request);
        assertEquals(60, taxOutput);

    }

    @Test
    void shouldCalculateTaxTestMaxTaxOfDayMultipleDay() {

        List<LocalDateTime> dates = List.of(parse("2013-02-08 05:59:00", formatter),
                parse("2013-02-08 06:59:00", formatter), parse("2013-02-08 07:59:00", formatter),
                parse("2013-02-08 07:58:00", formatter), parse("2013-02-08 08:58:00", formatter),
                parse("2013-02-08 09:58:00", formatter), parse("2013-02-08 13:58:00", formatter),
                parse("2013-02-08 14:58:00", formatter), parse("2013-02-08 15:58:00", formatter),
                parse("2013-02-08 16:58:00", formatter), parse("2013-02-08 17:58:00", formatter),
                parse("2013-02-08 18:58:00", formatter), parse("2013-02-08 19:58:00", formatter),
                parse("2013-02-08 20:58:00", formatter), parse("2013-02-07 06:59:00", formatter),
                parse("2013-02-06 07:59:00", formatter));

        TaxRequest request = new TaxRequest("car", dates);
        var taxOutput = congestionTaxCalculatorService.calculateCongestionTax(request);
        assertEquals(91, taxOutput);

    }

    @Test
    void shouldCalculateTaxTestNonTaxableDays() {

        List<LocalDateTime> dates = new ArrayList<>();
        //holiday and day before holiday
        dates.add(parse("2013-03-27 17:58:00", formatter));
        dates.add(parse("2013-03-28 06:08:00", formatter));
        // weekends
        dates.add(parse("2013-02-09 06:59:00", formatter));
        dates.add(parse("2013-02-10 06:59:00", formatter));
        //july month
        dates.add(parse("2013-07-08 19:58:00", formatter));

        TaxRequest request = new TaxRequest("car", dates);
        var taxOutput = congestionTaxCalculatorService.calculateCongestionTax(request);
        assertEquals(0, taxOutput);

    }

    @Test
    void shouldCalculateTaxTestNonTaxableVehicles() {

        List<LocalDateTime> dates = new ArrayList<>();
        dates.add(parse("2013-04-27 17:58:00", formatter));
        dates.add(parse("2013-04-28 06:08:00", formatter));
        dates.add(parse("2013-02-08 06:59:00", formatter));
        dates.add(parse("2013-02-07 06:59:00", formatter));
        dates.add(parse("2013-07-08 19:58:00", formatter));
        TaxRequest request = new TaxRequest("Military vehicles", dates);
        var taxOutput = congestionTaxCalculatorService.calculateCongestionTax(request);
        assertEquals(0, taxOutput);

    }
}
