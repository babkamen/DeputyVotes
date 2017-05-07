package com.devchallenge.batch;

import com.devchallenge.domain.VoteRepository;
import com.devchallenge.domain.VoteResults;
import com.devchallenge.service.SimilarityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class VoteManager {
    @Autowired
    VoteParser parser;
    @Autowired
    PdfReader pdfReader;
    @Autowired
    VoteRepository repository;
    @Autowired
    SimilarityService similarityService;
    public void process(String filePath){
        log.info("Going to try to check if file exists");
        File file = new File(filePath);
        try {
            waitForFileToBeOpenable(file);
            pdfReader.init(file);
            while (pdfReader.hasNext()) {
                String text = pdfReader.read();
                if (!parser.isCompletePage(text)) {
                    text += pdfReader.read();
                }
                VoteResults voteResults = parser.parse(text);
                repository.save(voteResults);
                similarityService.add(voteResults.getVotes());
            }
            pdfReader.close();
            log.info("Finished parsing {}", filePath);
        }catch (IOException e) {
            log.error("Something went wrong when parsing {}.\n{}", filePath, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitForFileToBeOpenable(File file) throws InterruptedException {
        int i=0;
        while(!file.exists()&&i<30){
            Thread.sleep(1000L);
            i++;
            log.debug("Waiting for file open. {}",file.getAbsolutePath());
        }
    }
}
