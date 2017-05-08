package com.devchallenge.batch;

import com.devchallenge.domain.vote.VoteRepository;
import com.devchallenge.domain.vote.VoteResults;
import com.devchallenge.service.SimilarityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VoteManager {

    Set<String> deputies=new HashSet<>();
    @Autowired
    VoteParser parser;
    @Autowired
    PdfReader pdfReader;
    @Autowired
    VoteRepository repository;
    @Autowired
    SimilarityService similarityService;

    public void process(String filePath) {
        File file = new File(filePath);
        try {
            pdfReader.init(file);
            while (pdfReader.hasNext()) {
                String text = pdfReader.read();
                //read the rest of info on next page
                if (!parser.isCompletePage(text)) {
                    text += pdfReader.read();
                }
                VoteResults voteResults = parser.parse(text);
                repository.save(voteResults);
            }
            pdfReader.close();
            similarityService.recreateIndex();
            log.info("Finished parsing {}", filePath);
        } catch (IOException e) {
            log.error("Something went wrong when parsing {}.\n{}", filePath, e);
        }
    }

}
