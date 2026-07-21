package com.wcc.challenge.geodistancecalc.service;

import com.wcc.challenge.geodistancecalc.domain.Postcode;
import com.wcc.challenge.geodistancecalc.exception.PostcodeNotFoundException;
import com.wcc.challenge.geodistancecalc.exception.PostcodeOutOfBoundsException;
import com.wcc.challenge.geodistancecalc.repository.PostcodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wcc.challenge.geodistancecalc.domain.UKBounds.*;

/**
 * Lookup and maintenance of the postcode -> coordinates mapping.
 */
@Service
public class PostcodeService {

    private final PostcodeRepository postcodeRepository;

    public PostcodeService(PostcodeRepository postcodeRepository) {
        this.postcodeRepository = postcodeRepository;
    }

    @Transactional(readOnly = true)
    public Postcode getByCode(String parsedCode) {
        String code = PostcodeNormalizer.normalize(parsedCode);
        return postcodeRepository.findById(code)
                .orElseThrow(() -> new PostcodeNotFoundException(parsedCode));
    }

    @Transactional
    public Postcode updateCoordinates(String rawCode, double latitude, double longitude) {
        assertWithinUkBounds(latitude, longitude);

        String code = PostcodeNormalizer.normalize(rawCode);
        Postcode postcode = postcodeRepository.findById(code)
                .map(existing -> {
                    existing.updateCoordinates(latitude, longitude);
                    return existing;
                })
                .orElseGet(() -> new Postcode(code, latitude, longitude));
        return postcodeRepository.save(postcode);
    }

    private void assertWithinUkBounds(double latitude, double longitude) {
        boolean inBounds = latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE
                && longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
        if (!inBounds) {
            throw new PostcodeOutOfBoundsException(latitude, longitude);
        }
    }

    @Transactional(readOnly = true)
    public Page<Postcode> getAll(Pageable pageable) {
        return postcodeRepository.findAll(pageable);
    }
}