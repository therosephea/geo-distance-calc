package com.wcc.challenge.geodistancecalc.dto;

import com.wcc.challenge.geodistancecalc.domain.Postcode;

public record LocationDTO(String postcode, double latitude, double longitude) {

    public static LocationDTO from(Postcode postcode){
         return new LocationDTO(postcode.getCode(), postcode.getLatitude(), postcode.getLongitude());
     }
}
