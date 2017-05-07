package com.devchallenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.devchallenge.util.Utils.listFilesForFolder;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class VoteWrapper {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @JsonIgnore
    private Long voteResultId;

    @Column
    @ElementCollection(targetClass=String.class)
    private List<String> accepted,rejected,notVoted,abstained,absent;

    public List<String> getAllDeputies(){
        List<String> result=new ArrayList<>(accepted);
        result.addAll(rejected);
        result.addAll(notVoted);
        result.addAll(absent);
        result.addAll(abstained);
        return result;
    }
}
