package com.devchallenge.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Entity
public class IndividualVote {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String deputyFullName;
    private String decision;
}
   