package com.wcc.challenge.geodistancecalc.service;

import com.wcc.challenge.geodistancecalc.domain.Postcode;
import com.wcc.challenge.geodistancecalc.dto.DistanceResponse;
import com.wcc.challenge.geodistancecalc.dto.LocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DistanceService {
    private static final Logger log = LoggerFactory.getLogger(DistanceService.class);
    private final PostcodeService postcodeService;
    private final GeoDistanceCalculator calculator;

    public DistanceService(PostcodeService postcodeService, GeoDistanceCalculator calculator) {
        this.postcodeService = postcodeService;
        this.calculator = calculator;
    }

    public DistanceResponse distanceBetween(String fromCode, String toCode) {
        Postcode from = postcodeService.getByCode(fromCode);
        Postcode to = postcodeService.getByCode(toCode);

        double distanceKm = calculator.distanceinKM(
                from.getLatitude(), from.getLongitude(),
                to.getLatitude(), to.getLongitude());

        log.info("distance_request from={} to={} distance_km={}",
                from.getCode(), to.getCode(), distanceKm);

        return new DistanceResponse(
                LocationDTO.from(from),
                LocationDTO.from(to),
                round(distanceKm));
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
