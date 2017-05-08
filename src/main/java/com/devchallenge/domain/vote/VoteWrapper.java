package com.devchallenge.domain.vote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.devchallenge.util.Utils.listFilesForFolder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class VoteWrapper {


    @Column
    @ElementCollection(targetClass=String.class)
    @LazyCollection(LazyCollectionOption.FALSE)
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
