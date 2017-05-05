package com.devchallenge;

import com.devchallenge.batch.PdfReader;
import com.devchallenge.domain.GeneralVote;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;


//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ApplicationTests {

    @Test
    //test read
    public void readSuccss() throws Exception {
        java.net.URL url = getClass().getResource("/Результат поіменного голосування 4.08.2016.pdf");
        File file = new File(url.toURI());
        PdfReader pdfReader = new PdfReader().init(file);

//		while (pdfReader.hasNext())
//		System.out.println(pdfReader.parseNext());

        GeneralVote actual = pdfReader.parseNext();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        LocalDate date = LocalDate.parse("04.08.16", formatter);
        GeneralVote expected = GeneralVote.builder()
                .date(date)
                .title("")
                .build();
        System.out.println("Actual=\n" + actual);
        assertThat(actual.getDate(), is(date));
        assertThat(actual.getDecision(), is("ПРИЙНЯТО"));
        assertThat(actual.getAggreed(), is(33));
        assertThat(actual.getDisaggred(), is(0));
        assertThat(actual.getVotes(), hasSize(greaterThan(0)));
        System.out.println(actual);
    }

}
