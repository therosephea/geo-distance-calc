package com.wcc.challenge.geodistancecalc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdatePostcodeRequest(

        @NotNull(message = "is required")
        @Min(value = -90, message = "must be between -90 and 90")
        @Max(value = 90, message = "must be between -90 and 90")
        Double latitude,

        @NotNull(message = "is required")
        @Min(value = -180, message = "must be between -180 and 180")
        @Max(value = 180, message = "must be between -180 and 180")
        Double longitude) {
}