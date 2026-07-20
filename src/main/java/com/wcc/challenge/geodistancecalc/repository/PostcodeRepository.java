package com.wcc.challenge.geodistancecalc.repository;

import com.wcc.challenge.geodistancecalc.domain.Postcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostcodeRepository extends JpaRepository<Postcode,String> {

}
