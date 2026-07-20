package com.wcc.challenge.geodistancecalc.service;

import com.wcc.challenge.geodistancecalc.domain.Postcode;
import com.wcc.challenge.geodistancecalc.dto.DistanceResponse;
import com.wcc.challenge.geodistancecalc.exception.PostcodeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistanceServiceTest {
    @Mock
    private PostcodeService postcodeService;

    @Mock
    private GeoDistanceCalculator calculator;

    @InjectMocks
    private DistanceService distanceService;

    @Test
    void returnsBothLocationsDistanceAndUnit() {
        Postcode luton = new Postcode("LU29LY", 51.8756, -0.3727);
        Postcode edinburgh = new Postcode("EH128DG", 55.9473, -3.3608);
        when(postcodeService.getByCode("LU29LY")).thenReturn(luton);
        when(postcodeService.getByCode("EH128DG")).thenReturn(edinburgh);
        when(calculator.distanceinKM(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(493.94827);

        DistanceResponse response = distanceService.distanceBetween("LU29LY", "EH128DG");

        assertThat(response.start().postcode()).isEqualTo("LU29LY");
        assertThat(response.start().latitude()).isEqualTo(51.8756);
        assertThat(response.end().postcode()).isEqualTo("EH128DG");
        assertThat(response.distance()).isEqualTo(493.948);   // rounded to 3 dp
        assertThat(response.unit()).isEqualTo("KM");
    }

    @Test
    void passesCorrectCoordinatesToTheCalculator() {
        Postcode luton = new Postcode("LU29LY", 51.8756, -0.3727);
        Postcode edinburgh = new Postcode("EH128DG", 55.9473, -3.3608);
        when(postcodeService.getByCode("LU29LY")).thenReturn(luton);
        when(postcodeService.getByCode("EH128DG")).thenReturn(edinburgh);
        when(calculator.distanceinKM(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(100.0);

        distanceService.distanceBetween("LU29LY", "EH128DG");

        verify(calculator).distanceinKM(51.8756, -0.3727, 55.9473, -3.3608);
    }

    @Test
    void propagatesNotFoundException() {
        when(postcodeService.getByCode("XY999ZZ"))
                .thenThrow(new PostcodeNotFoundException("XY99 9ZZ"));

        assertThatThrownBy(() -> distanceService.distanceBetween("XY999ZZ", "LU29LY"))
                .isInstanceOf(PostcodeNotFoundException.class);
    }
}