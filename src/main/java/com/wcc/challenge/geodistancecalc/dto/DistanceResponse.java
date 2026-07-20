package com.wcc.challenge.geodistancecalc.dto;

public record DistanceResponse(LocationDTO start, LocationDTO end, double distance, String unit) {
    public DistanceResponse(LocationDTO start, LocationDTO end, double distance) {
        this(start, end, distance, "KM");
    }
}
