package com.devchallenge.domain;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
public class GeneralVote {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String title,placeName;
    private String numberOfSession;
    private LocalDate date;
    //назва закону
    private String propositionName;
    // За основу За пропозицію...
    private String kindOfProposition;
    //рішення депутатів
    @OneToMany(targetEntity=IndividualVote.class, fetch=FetchType.EAGER)
    @JoinColumn(name="vote_id")
    @Cascade(CascadeType.SAVE_UPDATE)
    private List<IndividualVote> votes;
    private String decision;
    //за проти утримались
    private int aggreed, disaggred, abstained;


}
