package com.devchallenge.domain.similarity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityIndexForDeputy {
    private String deputyFullName;
    private double coefficient;
}
