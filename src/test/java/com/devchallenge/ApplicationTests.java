package com.devchallenge;

import com.devchallenge.batch.PdfReader;
import com.devchallenge.domain.VoteResults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;


//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Test
    public void readSuccss() throws Exception {
        java.net.URL url = getClass().getResource("/Результат поіменного голосування 4.08.2016.pdf");
        File file = new File(url.toURI());
        PdfReader pdfReader = new PdfReader().init(file);

		VoteResults actual = pdfReader.parseNext();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        LocalDate date = LocalDate.parse("04.08.16", formatter);
        VoteResults expected = VoteResults.builder()
                .date(date)
                .title("")
                .build();
        log.debug("Actual= " + actual);
        assertThat(actual.getDate(), is(date));
        assertThat(actual.getDecision(), is("ПРИЙНЯТО"));
        assertThat(actual.getAccepted(), is(33));
        assertThat(actual.getRejected(), is(0));
        assertThat(actual.getVotes().getAccepted(), hasSize(greaterThan(0)));
        log.debug("Actual {}",actual);
    }

    @Test
    public void whenReadProposalShouldBeRight() throws Exception {
        java.net.URL url = getClass().getResource("/Результат поіменного голосування 4.08.2016.pdf");
        File file = new File(url.toURI());
        PdfReader pdfReader = new PdfReader().init(file);
        pdfReader.setPageNum(2);
        VoteResults voteResults = pdfReader.parseNext();
        log.debug("Res={}", voteResults);
        URL resource = getClass().getResource("/proposal2Page.txt");
        String expectedProposalName = new java.util.Scanner(
                new File(resource.toURI()),"UTF8")
                .useDelimiter("\\Z")
                .next();
        expectedProposalName = expectedProposalName.replaceAll("\r\n", " ");
        assertThat(voteResults.getProposalName(),is(expectedProposalName));
    }


}
