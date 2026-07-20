package com.wcc.challenge.geodistancecalc.service;

import com.wcc.challenge.geodistancecalc.domain.Postcode;
import com.wcc.challenge.geodistancecalc.exception.PostcodeNotFoundException;
import com.wcc.challenge.geodistancecalc.exception.PostcodeOutOfBoundsException;
import com.wcc.challenge.geodistancecalc.repository.PostcodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostcodeServiceTest {

    @Mock
    private PostcodeRepository postcodeRepository;

    @InjectMocks
    private PostcodeService postcodeService;

    @Test
    void returnsPostcode(){
        Postcode stored = new Postcode("AB101XG", 57.14414, -2.114871);
        when(postcodeRepository.findById("AB101XG")).thenReturn(Optional.of(stored));

        Postcode result = postcodeService.getByCode("AB101XG");

        assertThat(result.getCode()).isEqualTo("AB101XG");
        assertThat(result.getLatitude()).isEqualTo(57.14414);
    }

    @Test
    void normalizeInputBeforeLookup(){
        Postcode stored = new Postcode("BB15NJ", 53.75938,-2.468225);
        when(postcodeRepository.findById("BB15NJ")).thenReturn(Optional.of(stored));

        Postcode result = postcodeService.getByCode("Bb1 5nj");

        assertThat(result.getCode()).isEqualTo("BB15NJ");
    }

    @Test
    void throwsExceptionWhenPostcodeNotFound(){
        when(postcodeRepository.findById("XY999ZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postcodeService.getByCode("XY99 9ZZ"))
                .isInstanceOf(PostcodeNotFoundException.class).hasMessageContaining("XY99 9ZZ");
    }

    @Test
    void throwsWhenCoordinatesOutsideUkBounds() {
        assertThatThrownBy(() -> postcodeService.updateCoordinates("AB101XG", 45.0, 2.0))
                .isInstanceOf(PostcodeOutOfBoundsException.class)
                .hasMessageContaining("outside UK bounds");
    }

    @Test
    void createsPostcodeWhenItDoesNotExist() {
        when(postcodeRepository.findById("ZZ11ZZ")).thenReturn(Optional.empty());
        when(postcodeRepository.save(any(Postcode.class))).thenAnswer(inv -> inv.getArgument(0));

        Postcode result = postcodeService.updateCoordinates("ZZ1 1ZZ", 57.2, -2.2);

        assertThat(result.getCode()).isEqualTo("ZZ11ZZ");
        assertThat(result.getLatitude()).isEqualTo(57.2);
    }
}