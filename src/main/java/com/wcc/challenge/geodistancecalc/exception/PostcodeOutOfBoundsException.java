package com.wcc.challenge.geodistancecalc.exception;

public class PostcodeOutOfBoundsException extends RuntimeException {
    public PostcodeOutOfBoundsException(double latitude, double longitude) {

        super("Coordinates (" + latitude + ", " + longitude + ") are outside UK bounds");

    }
}
