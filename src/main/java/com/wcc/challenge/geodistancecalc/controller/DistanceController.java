package com.wcc.challenge.geodistancecalc.controller;

import com.wcc.challenge.geodistancecalc.dto.DistanceResponse;
import com.wcc.challenge.geodistancecalc.service.DistanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/v1/distance?from=LU2%209LY&to=EH12%208DG
 */
@RestController
@RequestMapping("/api/v1/distance")
public class DistanceController {

    private final DistanceService distanceService;

    public DistanceController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @GetMapping
    public DistanceResponse distance(@RequestParam("from") String from,
                                     @RequestParam("to") String to) {
        return distanceService.distanceBetween(from, to);
    }


}
