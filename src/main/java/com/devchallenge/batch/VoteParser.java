package com.devchallenge.batch;

import com.devchallenge.domain.vote.VoteResults;
import com.devchallenge.domain.vote.VoteType;
import com.devchallenge.domain.vote.VoteWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devchallenge.batch.PdfReader.DECISION;
import static com.devchallenge.util.Utils.getLastDigits;

@Slf4j
@Component
public class VoteParser {
    public static final String PROPOSAL_REGEX = "(?<=Результат поіменного голосування:)(.*)(?=№:.*)";
    public static final String DEPUTY_VOTES_START_WORD = "голосування";
    public static final String NOT = "Не";
    public static final String VOTE_RESULTS = "ПІДСУМКИ ГОЛОСУВАННЯ";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");

    PdfReader reader;

    @Autowired
    public VoteParser(PdfReader reader) {
        this.reader = reader;
    }

    public VoteResults parse(String s) throws IOException {
        String[] str = s.split("\\r?\\n");
        log.debug("PageInfo={}", Arrays.toString(str));

        String session = str[2];
        String parsedDate = session.substring(session.length() - 8);
        String decision = str[str.length - 2];
        decision = decision.substring(DECISION.length() + 1);

        String acceptedVotes = str[str.length - 7];
        log.debug("Parsing numb of accepted votes from {}", acceptedVotes);
        Integer accepted = getLastDigits(acceptedVotes);

        String rejectedVotes = str[str.length - 6];
        log.debug("Parsing numb of rejected votes from {}", rejectedVotes);
        Integer rejected = getLastDigits(rejectedVotes);

        String abstainedVotes = str[str.length - 5];
        log.debug("Parsing numb of abstained votes from {}", abstainedVotes);
        Integer abstained = getLastDigits(abstainedVotes);

        String notVotedDeputies = str[str.length - 3];
        log.debug("Parsing numb of notVoted deputies from {}", notVotedDeputies);
        Integer notVoted = getLastDigits(notVotedDeputies);

        String absentDeputies = str[str.length - 4];
        log.debug("Parsing numb of absent deputies from {}", absentDeputies);
        Integer absent = getLastDigits(absentDeputies);

        log.debug("Agreed=" + str[str.length - 7] + "\n" + accepted);
        VoteWrapper votes = null;
        try {
            votes = parseIndividualVotes(s);
        } catch (IllegalArgumentException e) {
            log.error("{} when trying to parse file {} pageNumb=", e.getMessage(),
                    reader.getFileName(), reader.getPageNum() - 1);
        }
        String proposal = findByRegex(s, PROPOSAL_REGEX).replaceAll("\r\n", "").trim();
        log.debug("Proposal={}", proposal);
        return VoteResults.builder()
                .date(LocalDate.parse(parsedDate, formatter))
                .title(str[0])
                .placeName(str[1])
                .proposal(proposal)
                .votes(votes)
                .decision(decision)
                .accepted(accepted)
                .rejected(rejected)
                .abstained(abstained)
                .absent(absent)
                .notVoted(notVoted)
                .build();
    }


    private String findByRegex(String s, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        return matcher.group(1);
    }

    public static VoteWrapper parseIndividualVotes(String s) {
        Map<VoteType, List<String>> votes = new HashMap<>(5, 1);
        for (VoteType voteType : VoteType.values()) {
            votes.put(voteType, new LinkedList<>());
        }
        int startIndex = s.lastIndexOf(DEPUTY_VOTES_START_WORD);
        int endIndex = s.indexOf(VOTE_RESULTS);

        String x = s.substring(startIndex + DEPUTY_VOTES_START_WORD.length(), endIndex).replaceAll("\\r\\n", " ");
        String[] str = x.trim().split("[\\s\\xA0]+");
        for (int i = 0; i < str.length; i += 5) {
            String fistName = str[i + 1],
                    lastName = str[i + 2],
                    middleName = str[i + 3];
            String fullName = fistName + " " + lastName + " " + middleName;
            String decision = str[i + 4];
            if (NOT.equals(decision)) {
                decision += " " + str[i + 5];
                i++;
            }
            votes.get(VoteType.from(decision)).add(fullName);
        }
        return VoteWrapper.builder()
                .accepted(votes.get(VoteType.ACCEPTED))
                .rejected(votes.get(VoteType.REJECTED))
                .absent(votes.get(VoteType.ABSENT))
                .abstained(votes.get(VoteType.ABSTAINED))
                .notVoted(votes.get(VoteType.NOT_VOTED))
                .build();
    }

    public boolean isCompletePage(String s) {
        return s.contains(DECISION);
    }

}
