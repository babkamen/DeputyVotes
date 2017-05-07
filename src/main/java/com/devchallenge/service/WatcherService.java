package com.devchallenge.service;

import com.devchallenge.batch.VoteManager;
import com.devchallenge.config.WatcherProps;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
@Service
@Setter
public class WatcherService {
    @Autowired
    private VoteManager voteManager;
    @Autowired
    WatcherProps configProps;

    public void start() {
        String watchPath = configProps.getPath();
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(watchPath);
            dir.register(watcher, ENTRY_CREATE);

            log.info("Watch Service registered for dir: " + watchPath);
            for (; ; ) {

                // wait for key to be signaled
                WatchKey key;
                try {
                    key = watcher.poll(configProps.getDelay(), TimeUnit.SECONDS);
                } catch (InterruptedException x) {
                    return;
                }
                if (key != null)
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // This key is registered only
                        // for ENTRY_CREATE events,
                        // but an OVERFLOW event can
                        // occur regardless if events
                        // are lost or discarded.
                        if (kind == OVERFLOW) {
                            continue;
                        }

                        // The filename is the
                        // context of the event.
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        Path child = dir.resolve(filename);
                        String contentType = URLConnection.guessContentTypeFromName(child.getFileName().toString());
                        if ("application/pdf".equals(contentType)) {
                            log.info("Going to process " + filename);
                            voteManager.process(child.toString());
                        } else {
                            log.error("Found file with name " + child.toAbsolutePath() + " which content type is not pdf!");
                        }
                        boolean valid = key.reset();
                        if (!valid) {
                            break;
                        }
                    }
            }
        } catch (IOException ex) {
            log.error("{}",ex);
        }
    }
}