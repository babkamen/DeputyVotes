package com.devchallenge;

import com.devchallenge.domain.VoteWrapper;
import com.devchallenge.service.SimilarityService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SimilarityServiceTests {
    SimilarityService similarityService = new SimilarityService();
    @Before
    public void init(){
        similarityService.reset();
    }
    @Test
    public void testSucss(){
        //prepare
        VoteWrapper voteWrapper = VoteWrapper.builder()
                .accepted(Arrays.asList("1", "2", "3"))
                .rejected(Arrays.asList("1", "2"))
                .build();
        //run
        similarityService.add(voteWrapper);
        //validate
        assertThat(similarityService.generateJacardIndex(), is(0.25D));
    }
}
