package com.wcc.challenge.geodistancecalc.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DistanceApiIntegrationTest {

    private static final String USER = "testuser";
    private static final String PASS = "testpass";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsDistanceJsonForTwoKnownPostcodes() throws Exception {
        mockMvc.perform(get("/api/v1/distance")
                        .param("from", "AB10 1XG")
                        .param("to", "BB1 4HY")
                        .with(httpBasic(USER, PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start.postcode").value("AB101XG"))
                .andExpect(jsonPath("$.start.latitude").value(57.14414))
                .andExpect(jsonPath("$.end.postcode").value("BB14HY"))
                .andExpect(jsonPath("$.distance").isNumber())
                .andExpect(jsonPath("$.unit").value("KM"));
    }

    @Test
    void unknownPostcodeReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/distance")
                        .param("from", "XY99 9ZZ")
                        .param("to", "BB1 4HY")
                        .with(httpBasic(USER, PASS)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void missingParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/distance")
                        .param("from", "AB10 1XG")
                        .with(httpBasic(USER, PASS)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestWithoutCredentialsIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/distance")
                        .param("from", "AB10 1XG")
                        .param("to", "BB1 4HY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postcodeCanBeUpdatedAndReadBack() throws Exception {
        mockMvc.perform(put("/api/v1/postcodes/AB10 1XG")
                        .with(httpBasic(USER, PASS))
                        .contentType("application/json")
                        .content("{\"latitude\": 57.2, \"longitude\": -2.2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(57.2));

        mockMvc.perform(get("/api/v1/postcodes/AB101XG")
                        .with(httpBasic(USER, PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.longitude").value(-2.2));
    }

    @Test
    void coordinatesOutsideUkAreRejected() throws Exception {
        mockMvc.perform(put("/api/v1/postcodes/AB10 1XG")
                        .with(httpBasic(USER, PASS))
                        .contentType("application/json")
                        .content("{\"latitude\": 45.0, \"longitude\": 2.0}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }
}