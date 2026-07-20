package com.wcc.challenge.geodistancecalc.service;

import org.springframework.stereotype.Component;

@Component
public class HaversineDistanceCalculator implements GeoDistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public double distanceinKM(double lat1, double long1, double lat2, double long2) {
        double lat1radius = Math.toRadians(lat1);
        double lat2radius = Math.toRadians(lat2);
        double long1radius = Math.toRadians(long1);
        double long2radius = Math.toRadians(long2);

        double a = haversine(lat1radius, lat2radius)
                + Math.cos(lat1radius) * Math.cos(lat2radius) * haversine(long1radius, long2radius);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;

    }

    private double haversine(double rad1, double rad2) {
        return square(Math.sin((rad1 - rad2) / 2.0));
    }

    private double square(double x) {
        return x*x;
    }
}
