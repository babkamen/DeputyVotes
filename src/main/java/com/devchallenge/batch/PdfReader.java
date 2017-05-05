package com.devchallenge.batch;

import com.devchallenge.domain.VoteResults;
import com.devchallenge.domain.VoteType;
import com.devchallenge.domain.VoteWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devchallenge.util.Utils.getLastDigits;

@Slf4j
public class PdfReader {
    public static final String DECISION = "Рішення:";
    public static final String DEPUTY_VOTES_REGEX =
            "Результат\r\nголосування.*Результат\r\nголосування(.+?)ПІДСУМКИ ГОЛОСУВАННЯ";
    private PDDocument doc;
    private int pageNum = 1;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
    PDFTextStripper pdfStripper = new PDFTextStripper();
    private int count;
    private String fileName;

    public PdfReader() throws IOException {
    }

    public PdfReader init(File file) throws IOException {
        fileName = file.getAbsolutePath();
        doc = PDDocument.load(file);
        count = doc.getPages().getCount();
        this.reset();
        return this;
    }

    public String read() throws IOException {
        assert doc != null;
        pdfStripper.setStartPage(pageNum);
        pdfStripper.setEndPage(pageNum++);
        return pdfStripper.getText(doc);
    }

    public VoteResults parseNext() throws IOException {
        String s = this.read();
        //залишок інформації на наступній сторінці
        if (!s.contains(DECISION)) {
            s += this.read();
        }
        String[] str = s.split("\\r\\n");
        log.debug(s);
        log.debug(Arrays.toString(str));

        String session = str[2];
        String parsedDate = session.substring(session.length() - 8);
        String decision = str[str.length - 2];
        decision = decision.substring(DECISION.length() + 1);

        Integer accepted = getLastDigits(str[str.length - 7]);
        Integer rejected = getLastDigits(str[str.length - 6]);
        Integer abstained = getLastDigits(str[str.length - 5]);
        Integer notVoted = getLastDigits(str[str.length - 3]);
        Integer absent = getLastDigits(str[str.length - 4]);

        log.debug("Agreed=" + str[str.length - 7] + "\n" + accepted);
        VoteWrapper votes = null;
        try {
            votes = parseIndividualVotes(s);
        } catch (IllegalArgumentException e) {
            log.error("{} when trying to parse file {} pageNumb=", e.getMessage(), this.fileName, this.pageNum - 1);
        }

//TODO add next lines
        String proposalName = str[4];
        return VoteResults.builder()
                .date(LocalDate.parse(parsedDate, formatter))
                .title(str[0])
                .placeName(str[1])
                .proposalName(proposalName)
//                .proporsalType(str[5].replace(" №: Б/н  ",""))
                .votes(votes)
                .decision(decision)
                .accepted(accepted)
                .rejected(rejected)
                .abstained(abstained)
                .absent(absent)
                .notVoted(notVoted)
                .build();
    }

    private VoteWrapper parseIndividualVotes(String s) {
//        VoteWrapper res = new VoteWrapper();
        Map<VoteType, List<String>> votes = new HashMap<>(5, 1);
        for (VoteType voteType : VoteType.values()) {
            votes.put(voteType, new LinkedList<>());
        }
        final Pattern pattern = Pattern.compile(DEPUTY_VOTES_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        String x = matcher.group(1).replaceAll("\\r\\n", " ");
        String[] str = x.trim().split("\\s+");
        for (int i = 0; i < str.length; i += 5) {
            String fullName = str[i + 1] + " " + str[i + 2] + " " + str[i + 3];
            String decision = str[i + 4];
            if ("Не".equals(decision)) {
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

    public boolean hasNext() {
        return pageNum <= count;
    }

    public void reset() {
        this.pageNum = 1;
    }
}
