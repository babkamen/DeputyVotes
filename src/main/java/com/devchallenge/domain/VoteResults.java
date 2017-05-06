package com.devchallenge.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class VoteResults {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title, placeName;
    private String numberOfSession;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Lob
    @Column(length = 20971520)
    //назва закону
    private String proposal;
    @OneToOne(targetEntity = VoteWrapper.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "vote_id")
    //рішення депутатів
    private VoteWrapper votes;
    private String decision;
    //за проти утримались
    private int accepted, rejected, abstained, absent, notVoted;


}
