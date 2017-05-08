package com.devchallenge.batch;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.ExponentialBackOff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Getter
@Component
public class PdfReader {
    public static final String DECISION = "Рішення:";
    private PDDocument doc;
    PDFTextStripper pdfStripper = new PDFTextStripper();
    private String fileName;
    private int pageNum = 1;
    private int count;

    public PdfReader() throws IOException {
    }

    //Retry if canot open file
    @Retryable(maxAttempts = 10)
    public PdfReader init(File file) throws IOException {
        fileName = file.getAbsolutePath();
        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(1000L, 2);
        exponentialBackOff.setMaxInterval(30_000L);
        exponentialBackOff.start();
        doc = PDDocument.load(file);
        count = doc.getPages().getCount();
        this.reset();
        return this;
    }

    public PdfReader init(InputStream inputStream, String fileName) throws IOException {
        this.fileName = fileName;
        doc = PDDocument.load(inputStream);
        count = doc.getPages().getCount();
        this.reset();
        return this;
    }

    public String read() throws IOException {
        assert doc != null;
        pdfStripper.setStartPage(pageNum);
        pdfStripper.setEndPage(pageNum++);
        String s = pdfStripper.getText(doc);
        log.info("Processing {}.PageNum {} out of {}", fileName, pageNum, count);
        return s;
    }

    public boolean hasNext() {
        return pageNum <= count;
    }

    public void reset() {
        this.pageNum = 1;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void close() throws IOException {
        doc.close();
    }
}
