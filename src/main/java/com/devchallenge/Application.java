package com.devchallenge;

import com.devchallenge.batch.VoteManager;
import com.devchallenge.config.WatcherProps;
import com.devchallenge.service.SimilarityService;
import com.devchallenge.service.WatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static com.devchallenge.util.Utils.listFilesForFolder;

@SpringBootApplication
@EntityScan(basePackages = {"com.devchallenge.domain"})
@EnableJpaRepositories(basePackages = {"com.devchallenge.domain"})
@EnableAsync
public class Application {

    @Autowired
    WatcherService watcherService;
    @Autowired
    VoteManager voteManager;
    @Autowired
    WatcherProps configProps;

    @Bean("threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setThreadNamePrefix("DevChallengeRunner");
        threadPoolTaskExecutor.setMaxPoolSize(10);
        return threadPoolTaskExecutor;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        context.getBean(Application.class).init();
    }

    @Autowired
    SimilarityService similarityService;
    public void init(){
        scanDir();
        similarityService.save();
        watcherService.start();

    }

    private void scanDir() {
        listFilesForFolder(configProps.getPath()).forEach(voteManager::process);
    }


}
