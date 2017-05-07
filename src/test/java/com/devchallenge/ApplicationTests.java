package com.devchallenge;

import com.devchallenge.batch.PdfReader;
import com.devchallenge.batch.VoteManager;
import com.devchallenge.batch.VoteParser;
import com.devchallenge.config.WatcherProps;
import com.devchallenge.domain.VoteResults;
import com.devchallenge.service.SimilarityService;
import com.devchallenge.service.WatcherService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Mock
    WatcherProps configProps;
    @Mock
    VoteManager manager;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testWatcher() throws Exception {
        folder.create();
        String absolutePath = folder.getRoot().getAbsolutePath();
        WatcherService watcherService = new WatcherService();
        watcherService.setConfigProps(configProps);
        watcherService.setVoteManager(manager);
        log.info("Abs path={}", absolutePath);
        when(configProps.getPath()).thenReturn(absolutePath);
        when(configProps.getDelay()).thenReturn(0);
        Runnable runnable = () -> watcherService.start();
        executor.schedule(runnable, 1, TimeUnit.MICROSECONDS);
        Thread.sleep(1000L);
        log.info("Create new file");
        File file1 = folder.newFile("1.pdf");
        Thread.sleep(1000L);
        File file2 = folder.newFile("2.pdf");
        Thread.sleep(1000L);
        verify(manager).process(file1.getAbsolutePath());
        verify(manager).process(file2.getAbsolutePath());
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
