package com.devchallenge.domain;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "similarity", path = "similarity")
public interface SimilarityRepository extends PagingAndSortingRepository<SimilarityIndex, Long>,SimilarityRepositoryCustom {
}
