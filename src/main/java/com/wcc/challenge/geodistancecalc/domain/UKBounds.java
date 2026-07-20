package com.wcc.challenge.geodistancecalc.domain;

public class UKBounds {
    public static final double MIN_LATITUDE = 49.5;
    public static final double MAX_LATITUDE = 61.0;
    public static final double MIN_LONGITUDE = -8.7;
    public static final double MAX_LONGITUDE = 2.0;

    private UKBounds() {
        // utility class — never instantiated
    }

    public static boolean contains(double latitude, double longitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE
                && longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }
}
