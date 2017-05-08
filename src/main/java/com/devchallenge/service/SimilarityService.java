package com.devchallenge.service;

import com.devchallenge.domain.similarity.SimilarityIndex;
import com.devchallenge.domain.similarity.SimilarityRepository;
import com.devchallenge.domain.vote.VoteRepository;
import com.devchallenge.domain.vote.VoteResults;
import com.devchallenge.domain.vote.VoteWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class SimilarityService {
    @Autowired
    SimilarityRepository similarityRepository;
    @Autowired
    VoteRepository voteRepository;
    private Map<Map.Entry<String, String>, AtomicLong> pairs = new HashMap<>();
    int count = 0;

    private void increase(List<Map.Entry<String, String>> entries) {
        for (Map.Entry<String, String> entry : entries) {
            pairs.putIfAbsent(entry, new AtomicLong(0));
            pairs.get(entry).incrementAndGet();
        }
    }

    public List<SimilarityIndex> generateJacardIndex() {
        List<SimilarityIndex> res = new ArrayList<>();
        for (Map.Entry<String, String> e : pairs.keySet()) {
            int l = pairs.get(e).intValue();
            double l1 = (double)l / count;
            double coef = l1;
            res.add(SimilarityIndex.builder()
                    .deputyName1(e.getKey())
                    .deputyName2(e.getValue())
                    .coefficient(coef)
                    .build());
        }
        return res;
    }

    public void add(VoteWrapper votes) {
        List[] lists = {votes.getAccepted(), votes.getAbstained(), votes.getRejected()};
        Arrays.stream(lists).forEach(l -> {
            increase(generateUniquePairs(l));
            this.count += l.size();
        });
    }


    public void save() {
        List<SimilarityIndex> similarityIndices = this.generateJacardIndex();
        similarityRepository.save(similarityIndices);
    }

    public void reset() {
        this.pairs.clear();
        this.count = 0;
    }

    public List<Map.Entry<String, String>> generateUniquePairs(List<String> in ) {
        final List<Map.Entry<String, String>> pairs = new ArrayList<>();

        for (int i = 0; i < in.size(); ++i) {
            for (int j = i + 1; j < in.size(); ++j) {
                String[] e = {in.get(i), in.get(j)};
                Arrays.sort(e);
                pairs.add(new AbstractMap.SimpleImmutableEntry<>(e[0], e[1]));
            }
        }
        Collections.shuffle(pairs);
        for (Map.Entry<String, String> pair : pairs) {
            log.debug("Pair={}",pair);
        }
        return pairs;
    }

    public void recreateIndex() {
        reset();
        for (VoteResults voteResults : voteRepository.findAll()) {
            add(voteResults.getVotes());
        }
        save();
    }
}
