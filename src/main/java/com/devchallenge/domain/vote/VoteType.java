package com.devchallenge.domain.vote;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum VoteType {
    ACCEPTED("За"),REJECTED("Проти"),ABSTAINED("Утримався"),ABSENT("Відсутній"),NOT_VOTED("Не голосував");
    private String value;
    public static VoteType from(String value){
        Optional<VoteType> voteType = Arrays.stream(VoteType.values())
                .filter(v -> v.getValue().equals(value)).findFirst();

        if(!voteType.isPresent()){
            throw new IllegalArgumentException("Cannot find voteType with value=" + value +
                    ".Possible value are " +
                    Arrays.stream(VoteType.values())
                            .map(VoteType::getValue)
                            .collect(Collectors.toList()));
        }
        return voteType.get();
    }

}
