package com.devchallenge.domain;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Slf4j
public class SimilarityRepositoryImpl implements SimilarityRepositoryCustom{
    @PersistenceContext
    private EntityManager em;



    @Override
    public List<SimilarityIndex> findByDeputyFullName(String fullName) {
        return em.createQuery(
                "SELECT c FROM SimilarityDistance c WHERE c.deputyName1=?1 OR c.deputyName2=?1")
                .setParameter(1, fullName)
                .getResultList();
    }
}
