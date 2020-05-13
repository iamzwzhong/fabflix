## CS 122B 

### Demo Video URL
Project 1: https://youtu.be/xTdry2Og_kA \
Project 2: https://www.youtube.com/watch?v=8G1S9Wchas8 \
Project 3: https://youtu.be/bSIMO2DKPqA

### To run this example (Note: same as instructions given): 
1. Clone this repository using 
`git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-155.git`
2. Inside the repository, where the pom.xml file is located, build the war file:
`mvn package`
3. Copy your newly built war file:
`cp ./target/*.war /home/ubuntu/tomcat/webapps`
4. Open your AWS Tomcat and head to the Manager App
5. Under `War file to deploy`, select the war file that was built and click deploy
6. You can now navigate to the website  using the specified URL in the manager app


### Substring Matching
For Searching, user types in input and it goes into query like: "%input%" 
For Browsing, we used ^[^a-zA-Z0-9] and we used "input%"

### Prepared Statements
[LoginServlet](src/LoginServlet.java)
[PaymentServlet](src/PaymentServlet.java)
[MoviesServlet](src/MoviesServlet.java)
[SingleMovieServlet](src/SingleMovieServlet.java)
[SingleStarServlet](src/SingleStarServlet.java)


### Contributions
Zhi Wen Zhong
```
P1
-----
Implemented everything related to movies list
Implemented half of the tables in createtable.sql and added in convenient views
Updated README.md

P2
-----
Login/Shopping Cart/Payment/Place Order/Single Pages
-> Everything related to adding to a cart and checking out

P3
-----
XML Parsing, reCAPTCHA, Password Encryption, Prepared Statement

```
Tyler Foey
```
P1
-----
Implemented everything related to single movie page
Implemented everything related to single star page
Implemented half of the tables in createtable.sql

P2
-----
Searching/Browsing/Movie List/Main Page

P3
-----
HTTPS, Employee Dashboard, Prepared Statement, Stored Procedure
```
