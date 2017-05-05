package com.devchallenge.batch;

import com.devchallenge.domain.GeneralVote;
import com.devchallenge.domain.IndividualVote;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devchallenge.util.Utils.getLastDigits;

public class PdfReader {
    public static final String DECISION = "Рішення:";
    public static final String REGEX = "Результат\r\nголосування.*Результат\r\nголосування(.+?)ПІДСУМКИ ГОЛОСУВАННЯ";
    private PDDocument doc;
    private int pageNum = 1;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
    PDFTextStripper pdfStripper = new PDFTextStripper();
    private int count;

    public PdfReader() throws IOException {
    }

    public PdfReader init(File file) throws IOException {
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

    public GeneralVote parseNext() throws IOException {
        String s = this.read();
        //залишок інформації на наступній сторінці
        if(!s.contains(DECISION)){
            s += this.read();
        }
        String[] str = s.split("\\r\\n");
        System.out.println(s);
        System.out.println(Arrays.toString(str));

        String session = str[2];
        String parsedDate=session.substring(session.length()-8);
        String decision=str[str.length-2];
        decision = decision.substring(DECISION.length()+1);

        Integer aggreed = getLastDigits(str[str.length - 7]);
        Integer disaggreed = getLastDigits(str[str.length - 6]);
        Integer abstained = getLastDigits(str[str.length - 5]);

        System.out.println("Agreed="+str[str.length - 7]+"\n"+aggreed);
        List<IndividualVote> votes=parseIndividualVotes(s);
        return GeneralVote.builder()
                .date(LocalDate.parse(parsedDate, formatter))
                .title(str[0])
                .placeName(str[1])
                .propositionName(str[4])
                .votes(votes)
                .decision(decision)
                .aggreed(aggreed)
                .disaggred(disaggreed)
                .abstained(abstained)
                .build();
    }

    private List<IndividualVote> parseIndividualVotes(String s) {
        List<IndividualVote> res = new ArrayList<>();
        final Pattern pattern = Pattern.compile(REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        String x = matcher.group(1).replaceAll("\\r\\n", " ");
        String[] str = x.trim().split("\\s+");
        for(int i=0;i<str.length;i+=5){
            String fullName = str[i + 1]+" "+ str[i + 2]+" "+ str[i + 3];
            String decision = str[i + 4];
            if ("Не".equals(decision)) {
                decision += " "+str[i + 5];
                i++;
            }
            res.add(IndividualVote.builder()
                    .decision(decision)
                    .deputyFullName(fullName)
                    .build());
        }
        return res;
    }

    public boolean hasNext() {
        return pageNum <= count;
    }

    public void reset() {
        this.pageNum = 1;
    }
}
