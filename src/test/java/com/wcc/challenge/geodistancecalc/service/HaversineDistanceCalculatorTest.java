package com.wcc.challenge.geodistancecalc.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

public class HaversineDistanceCalculatorTest {
    private final GeoDistanceCalculator calculator = new HaversineDistanceCalculator();

    @Test
    void distanceBetweenSamePointIsZero() {
        double distance = calculator.distanceinKM(51.007, -0.1246, 51.007, -0.1246);

        assertThat(distance).isEqualTo(0.00);
    }

    @Test
    void londonToEdinburghMatchesReferenceDistance() {
        double distance = calculator.distanceinKM(51.8756, -0.37270135860765197, 55.947346588013005, -3.360751687795672);

        assertThat(distance).isCloseTo(493.9, within(5.0));
    }

    @Test
    void symmetricDistance(){
        double FroToDistance = calculator.distanceinKM(51.8756, -0.3727, 55.9473, -3.3608);
        double ToFroDistance = calculator.distanceinKM(55.9473, -3.3608, 51.8756, -0.3727);

        assertThat(FroToDistance).isEqualTo(ToFroDistance);
    }

    @Test
    void handlesCrossingThePrimeMeridian() {
        // Cambridge (+0.1384, east of Greenwich) to Bristol (-2.5891, west of Greenwich)
        double distance = calculator.distanceinKM(52.1955, 0.1384, 51.4534, -2.5891);

        assertThat(distance).isGreaterThan(150).isLessThan(250);
    }

}
