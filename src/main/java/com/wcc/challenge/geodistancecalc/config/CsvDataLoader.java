package com.wcc.challenge.geodistancecalc.config;

import com.wcc.challenge.geodistancecalc.domain.Postcode;
import com.wcc.challenge.geodistancecalc.repository.PostcodeRepository;
import com.wcc.challenge.geodistancecalc.service.PostcodeNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);

    private final ResourceLoader resourceLoader;
    private final PostcodeRepository postcodeRepository;
    private final String csvLocation;

    public CsvDataLoader(ResourceLoader resourceLoader, PostcodeRepository postcodeRepository, @Value("${app.postcodes.csv}") String csvLocation) {
        this.resourceLoader = resourceLoader;
        this.postcodeRepository = postcodeRepository;
        this.csvLocation = csvLocation;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Importing postcodes from {}", csvLocation);
        Resource resource = resourceLoader.getResource(csvLocation);
        long imported = 0;
        long skipped = 0;

        try (BufferedReader reader = new BufferedReader(
           new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Postcode postcode = getPostCodeFromLine(line);
                if (postcode == null) {
                    skipped++;
                    continue;
                }
                postcodeRepository.save(postcode);
                imported++;
            }
        }

        log.info("Imported {} postcodes ({} rows skipped)", imported, skipped);
    }

    private static Postcode getPostCodeFromLine(String line) {
        //parts[0] = id, parts[1] = code, parts[2] = latitude, parts[3] = longitude
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return null;
        }
        try {
            String code = PostcodeNormalizer.normalize(parts[1]);
            if (code == null || code.isBlank()){
                return null;
            }
            double latitude = Double.parseDouble(parts[2].trim());
            double longitude = Double.parseDouble(parts[3].trim());
            return new Postcode(code,latitude,longitude);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
