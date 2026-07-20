package com.wcc.challenge.geodistancecalc.service;

public final class PostcodeNormalizer {
    private PostcodeNormalizer(){}

    public static String normalize(String raw){
        if (raw == null) {
            return null;
        }
        return raw.replaceAll("\\s+", "").toUpperCase();
    }
}
