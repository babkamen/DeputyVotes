package com.devchallenge.domain;

import java.util.List;

public interface SimilarityRepositoryCustom {
    List<SimilarityIndex> findByDeputyFullName(String fullName);
}
