package com.wcc.challenge.geodistancecalc.exception;

public class PostcodeNotFoundException extends RuntimeException {

    public PostcodeNotFoundException(String postcode) {
        super("Unknown UK postcode: '" + postcode + "'");
    }
}