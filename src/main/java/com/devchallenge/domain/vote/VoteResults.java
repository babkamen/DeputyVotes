package com.devchallenge.domain.vote;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Lob
    @Column(length = 20971520)
    //назва закону
    private String proposal;
    @Embedded
    private VoteWrapper votes;
    //рішення депутатів
    private String decision;
    //за проти утримались
    private int accepted, rejected, abstained, absent, notVoted;

}
