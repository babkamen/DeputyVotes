Deputy Votes
------------
REST JSON service to parse pdf files([Example PDF](https://www.dropbox.com/s/pj7pkchqado3jyy/%D0%A0%D0%B5%D0%B7%D1%83%D0%BB%D1%8C%D1%82%D0%B0%D1%82%20%D0%BF%D0%BE%D1%96%D0%BC%D0%B5%D0%BD%D0%BD%D0%BE%D0%B3%D0%BE%20%D0%B3%D0%BE%D0%BB%D0%BE%D1%81%D1%83%D0%B2%D0%B0%D0%BD%D0%BD%D1%8F%204.08.2016%20%281%29.pdf?dl=0)) and show deputies vote results. 
Additionally, it has features:

- Calculates the similarity index for particular deputy(using Jaccard index );   
- Scans folder for new pdf files.

It was done in a week for [DevChallenge](http://devchallenge.it/)

Online demo:
---
- Main:

https://deputyvotes.herokuapp.com

- Vote Results:

https://deputyvotes.herokuapp.com/votes

- Similarity index:

https://deputyvotes.herokuapp.com/similarity/search?deputyFullName=%D0%A1%D0%B0%D0%BF%D0%BE%D0%B6%D0%BA%D0%BE%20%D0%86%D0%B3%D0%BE%D1%80%20%D0%92%D0%B0%D1%81%D0%B8%D0%BB%D1%8C%D0%BE%D0%B2%D0%B8%D1%87
 
Technologies:
---
- Java 8
- Gradle
- Spring boot(Data Rest, Data JPA)
- Spring retry
- Hibernate
- MySQL(local env),PostgreSQL(Heroku)
- Jackson
- PdfBox
- Lombok
- Mockito, Hamcrest
- Docker, docker-compose
 
### How do I run project ###
  docker-compose up --build 
 - Vote results
  http://localhost:8080/votes
 - Similarity index
  http://localhost:8080/similarity/search?deputyFullName={DeputyFullName}
 - For example
 http://localhost:8080/similarity/search?deputyFullName=Сапожко%20Ігор%20Васильович





















































































































































































































































































































































































































































































































































































































































































 






























