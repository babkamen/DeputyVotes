package com.devchallenge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "watcher")
public class WatcherProps {
    @NotNull
    private String path;
    @NotNull
    private int delay;
    @PostConstruct
    public void init() {
        if (Files.notExists(Paths.get(path))) {
            throw new RuntimeException("Path to folder with pdfs doesn't exist = " + path);
        }

    }
}
