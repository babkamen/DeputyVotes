package com.devchallenge;

import com.devchallenge.batch.PdfReader;
import com.devchallenge.batch.VoteManager;
import com.devchallenge.batch.VoteParser;
import com.devchallenge.config.WatcherProps;
import com.devchallenge.domain.vote.VoteResults;
import com.devchallenge.service.SimilarityService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URL;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@Slf4j
public class VoteParserTests {

    @Mock
    WatcherProps configProps;
    @Mock
    VoteManager manager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenParseIndDeputyRecordShouldReturnVoteType() throws Exception {
        java.net.URL url = getClass().getResource("/Результат поіменного голосування_15.12.2016.pdf");
        assert url != null;
        File file = new File(url.toURI());
        PdfReader pdfReader = new PdfReader().init(file);
        pdfReader.setPageNum(116);
        String s = pdfReader.read();

        VoteResults voteResults = new VoteParser(pdfReader).parse(s);

        log.debug("Res={}", voteResults);
        assertThat(voteResults.getVotes().getAccepted(), hasItem("Бабич Петро Іванович"));

        SimilarityService similarityService = new SimilarityService();
        similarityService.add(voteResults.getVotes());
        System.out.println(similarityService);
        System.out.println(similarityService.generateJacardIndex());

    }


    @Test
    public void whenReadProposalShouldBeRight() throws Exception {
        java.net.URL url = getClass().getResource("/Результат поіменного голосування 4.08.2016.pdf");
        File file = new File(url.toURI());
        PdfReader pdfReader = new PdfReader().init(file);
        pdfReader.setPageNum(2);
        String s = pdfReader.read();
        VoteResults voteResults = new VoteParser(pdfReader).parse(s);
        log.debug("Res={}", voteResults);
        URL resource = getClass().getResource("/proposal2Page.txt");
        String expectedProposalName = new java.util.Scanner(
                new File(resource.toURI()), "UTF8")
                .useDelimiter("\\Z")
                .next();
        expectedProposalName = expectedProposalName.replaceAll("\r\n", " ");
        assertThat(voteResults.getProposal(), is(expectedProposalName));
    }
}
