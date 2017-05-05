package com.devchallenge;

import com.devchallenge.batch.PdfReader;
import com.devchallenge.domain.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@EntityScan(basePackages = { "com.devchallenge.domain" })
@EnableJpaRepositories(basePackages = {"com.devchallenge.domain"})
public class Application {

    @Autowired
    VoteRepository voteRepository;
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	@PostConstruct
    public void init() throws IOException, URISyntaxException {
        java.net.URL url = getClass().getResource("/Результат поіменного голосування 4.08.2016.pdf");
        File file = new File(url.toURI());
        PdfReader pdfReader = new PdfReader().init(file);
        while (pdfReader.hasNext()){
            voteRepository.save(pdfReader.parseNext());
        }
    }
}
