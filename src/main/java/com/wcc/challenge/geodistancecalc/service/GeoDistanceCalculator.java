package com.wcc.challenge.geodistancecalc.service;

/**
 * Strategy for computing the distance between two UK postcodes.
 * Abstracting this allows the algorithm to be swapped without
 * touching callers, and allows services to be tested with a stub.
 */

public interface GeoDistanceCalculator {
    double distanceinKM(double lat1, double long1, double lat2, double long2);
}
