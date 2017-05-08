package com.devchallenge;

import com.devchallenge.domain.vote.VoteWrapper;
import com.devchallenge.service.SimilarityService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class SimilarityServiceTests {
    SimilarityService similarityService = new SimilarityService();
    @Before
    public void init(){
        similarityService.reset();
    }
    @Test
    public void testSucss(){
        VoteWrapper voteWrapper = VoteWrapper.builder()
                .accepted(Arrays.asList("1", "2", "3"))
                .rejected(Arrays.asList("1", "2"))
                .abstained(new ArrayList<>())
                .build();

        similarityService.add(voteWrapper);

        System.out.println(similarityService.generateJacardIndex());
    }
}
