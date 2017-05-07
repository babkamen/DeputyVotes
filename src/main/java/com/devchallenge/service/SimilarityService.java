package com.devchallenge.service;

import com.devchallenge.domain.SimilarityIndex;
import com.devchallenge.domain.SimilarityRepository;
import com.devchallenge.domain.VoteWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SimilarityService {
    @Autowired
    SimilarityRepository similarityRepository;



    private Map<Map.Entry<String, String>, AtomicLong> pairs = new HashMap<>(666, 1);
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
            long l = pairs.get(e).longValue();
            double coef = l / count * 100;
            res.add(SimilarityIndex.builder()
                    .deputyName1(e.getKey())
                    .deputyName2(e.getValue())
                    .coef(coef)
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
        List<SimilarityIndex> similarityDistances = this.generateJacardIndex();
        similarityRepository.save(similarityDistances);
    }

    public void reset() {
        this.pairs.clear();
        this.count = 0;
    }

    public List<Map.Entry<String, String>> generateUniquePairs(List<String> in) {
        final List<Map.Entry<String, String>> pairs = new ArrayList<>();
        for (int i = 0; i < in.size(); ++i) {
            for (int j = i + 1; j < in.size(); ++j) {
                String[] e = {in.get(i), in.get(j)};
                Arrays.sort(e);
                pairs.add(new AbstractMap.SimpleImmutableEntry<>(e[0], e[1]));
            }
        }
        Collections.shuffle(pairs);
//        for (Map.Entry<String, String> pair : pairs) {
//
//            System.out.println(pair);
//        }
        return pairs;
    }

    public static List createSet(Integer... a) {
        return Arrays.asList(a);
    }

    public static String jacardIndex(List... lists) {
        Integer union = Arrays
                .stream(lists)
                .map(List::size)
                .reduce((c, b) -> c + b).get();
        List intersection = new ArrayList(lists[0]);
        for (int i = 1; i < lists.length; i++) {
            intersection.retainAll(lists[i]);
        }
        return intersection.size() + "/" + union;
    }

    public static void main(String[] args) {
        List set1 = createSet(1, 1, 1, 2);
        List set2 = createSet(1, 1, 2, 2, 3);
        List set3 = createSet(1, 2, 3, 4);
//        System.out.println("Union=" + union);
//        List intersection = (List) set1.parallelStream()
//                .filter(set2::contains)
//                .filter(set3::contains)
//                .collect(Collectors.toList());
//        System.out.println("Intersection=" + set1.size());
        System.out.println("JacardIndex=" + jacardIndex(set1, set2, set3));

    }
    @Override
    public String toString() {
        return "SimilarityService{" +
                "pairs=" + pairs +
                ", count=" + count +
                '}';
    }
}
