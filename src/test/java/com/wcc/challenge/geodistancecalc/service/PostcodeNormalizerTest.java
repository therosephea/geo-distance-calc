package com.wcc.challenge.geodistancecalc.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostcodeNormalizerTest {

    @Test
    void uppercaseConverted() {
        assertThat(PostcodeNormalizer.normalize("fei22iif")).isEqualTo("FEI22IIF");
        assertThat(PostcodeNormalizer.normalize("B11AA")).isEqualTo("B11AA");
    }
    @Test
    void whitespaceRemoved() {
        assertThat(PostcodeNormalizer.normalize("acb 92j kFC")).isEqualTo("ACB92JKFC");
    }

    @Test
    void nullReturnsNull(){
        assertThat(PostcodeNormalizer.normalize(null)).isNull();
    }

}