package com.devchallenge.domain.similarity;

import java.util.List;

public interface SimilarityRepositoryCustom {
    List findByDeputyFullName(String fullName);
}
