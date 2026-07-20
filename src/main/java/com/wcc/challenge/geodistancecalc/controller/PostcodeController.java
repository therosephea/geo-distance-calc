package com.wcc.challenge.geodistancecalc.controller;

import com.wcc.challenge.geodistancecalc.dto.LocationDTO;
import com.wcc.challenge.geodistancecalc.dto.UpdatePostcodeRequest;
import com.wcc.challenge.geodistancecalc.service.PostcodeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/postcodes")
public class PostcodeController {

    private final PostcodeService postcodeService;

    public PostcodeController(PostcodeService postcodeService) {
        this.postcodeService = postcodeService;
    }

    @GetMapping("/{code}")
    public LocationDTO get(@PathVariable String code) {
        return LocationDTO.from(postcodeService.getByCode(code));
    }

    @PutMapping("/{code}")
    public LocationDTO update(@PathVariable String code,
                              @Valid @RequestBody UpdatePostcodeRequest request) {
        return LocationDTO.from(
                postcodeService.updateCoordinates(code, request.latitude(), request.longitude()));
    }
}