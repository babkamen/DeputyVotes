package com.devchallenge.domain.similarity;

import com.devchallenge.dto.SimilarityIndexForDeputy;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SimilarityRepositoryImpl implements SimilarityRepositoryCustom{
    @PersistenceContext
    private EntityManager em;

    /**
     *
     * @return similarity index for defined deputy with full name
     * @param fullName - deputy full name
     * Uses @SqlResultSetMapping to map result to non entity class
     * @see SimilarityIndex
     */
    @Override
    public List<SimilarityIndexForDeputy> findByDeputyFullName(String fullName) {
        List<SimilarityIndex> resultList =
                em.createQuery("SELECT DISTINCT c FROM SimilarityIndex c WHERE c.deputyName1=?1 " +
                        "OR c.deputyName2=?1 ORDER BY c.coefficient DESC ", SimilarityIndex.class)
                .setParameter(1, fullName)
                .getResultList();

        return resultList.stream().map(si->{
            //map other deputy name to dto object and coefficient
            String deputyName = si.getDeputyName1().equals(fullName) ? si.getDeputyName2() : si.getDeputyName1();
            return new SimilarityIndexForDeputy(deputyName,si.getCoefficient());
        }).collect(Collectors.toList());
    }

}
