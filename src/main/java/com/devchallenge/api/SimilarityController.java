package com.devchallenge.api;

import com.devchallenge.domain.similarity.SimilarityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SimilarityController {
    @Autowired
    SimilarityRepository similarityRepository;
    @RequestMapping(value = "/similarity/search",method = RequestMethod.GET)
    public ResponseEntity<List> findCoef(@RequestParam String deputyFullName){
        return ResponseEntity.ok(similarityRepository.findByDeputyFullName(deputyFullName));
    }
}
