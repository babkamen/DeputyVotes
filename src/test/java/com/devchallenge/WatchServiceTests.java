package com.devchallenge;

import com.devchallenge.batch.VoteManager;
import com.devchallenge.config.WatcherProps;
import com.devchallenge.service.WatcherService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class WatchServiceTests {
    @Mock
    VoteManager manager;
    @Mock
    WatcherProps configProps;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void testWatcher() throws Exception {
        //prepare
        folder.create();
        String absolutePath = folder.getRoot().getAbsolutePath();
        WatcherService watcherService = new WatcherService();
        watcherService.setConfigProps(configProps);
        watcherService.setVoteManager(manager);
        when(configProps.getPath()).thenReturn(absolutePath);
        when(configProps.getDelay()).thenReturn(0);
        Runnable runnable = () -> watcherService.start();
        //run
        executor.schedule(runnable, 1, TimeUnit.MICROSECONDS);
        Thread.sleep(1000L);
        File file1 = folder.newFile("1.pdf");
        Thread.sleep(1000L);
        File file2 = folder.newFile("2.pdf");
        Thread.sleep(1000L);
        //validate
        verify(manager).process(file1.getAbsolutePath());
        verify(manager).process(file2.getAbsolutePath());
    }

}
