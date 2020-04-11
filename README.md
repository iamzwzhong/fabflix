## CS 122B Project 1 

### Demo Video URL
Link: https://youtu.be/xTdry2Og_kA

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

### Contributions
Zhi Wen Zhong
```
Implemented everything related to movies list
Implemented half of the tables in createtable.sql and added in convenient views
Updated README.md
Demo
```
Tyler Foey
```
Implemented everything related to single movie page
Implemented everything related to single star page
Implemented half of the tables in createtable.sql
Demo
```