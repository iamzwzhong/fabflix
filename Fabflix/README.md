### To run this example (Note: same as instructions given): 
1. Clone this repository using 
`git clone https://github.com/iamzwzhong/Fabflix.git`
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

### Connection Pooling
[LoginServlet](src/LoginServlet.java)
[PaymentServlet](src/PaymentServlet.java)
[MoviesServlet](src/MoviesServlet.java)
[SingleMovieServlet](src/SingleMovieServlet.java)
[SingleStarServlet](src/SingleStarServlet.java)
[AddToCartServlet](src/AddToCartServlet.java)
[AuthocompleteServlet](src/AutocompleteServlet.java)
[CartServlet](src/CartServlet.java)
[ConfirmationServlet](src/ConfirmationServlet.java)
[EmployeeLoginServlet](src/EmployeeLoginServlet.java)
[MainSearchServlet](src/MainSearchServlet.java)
[PaymentServlet](src/PaymentServlet.java)
[\_DashboardServlet](src/_DashboardServlet.java)
[context](WebContent/META-INF/context.xml)
[web](WebContent/WEB-INF/web.xml)

Connection Pooling is utilized by reducing the response time of the server and also making our program more secure as the login information is not shown in the url for the server. The response time is reduced because the connections are taken from the pool which are precreated and when we do dbcon.close() it does not physically close the connection. This increases JDBC query performance by allocating a pool of connections,  each of which can be shared by multiple threads at different times to communicate with the backend database.

Declare a resource for in the web.xml for both of the backend databases. In context.xml, we define those resources and make sure that they both contain "factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"". In one of them, the URL should be the localhost whilst the other one should contain the private IP of the external database. In our case, we have a slave and a master. Master should only use its connection pool whereas slave should use its connection for read but change to master's when a user wants to write into the database.

### Master/Slave

[context.xml](WebContent/META-INF/context.xml) 
[web.xml](WebContent/WEB-INF/context.xml) 
[ConfirmationServlet](src/ConfirmationServlet.java) 
[\_DashboardServlet.java](src/_DashboardServlet.java)

In the master AWS instance, only master's mysql resource (jdbc/moviedb) is defined in its context.xml. All read and write requests go to its own mysql which will also update slave's. So we use @Resource(name = "jdbc/moviedb") everywhere. In the slave AWS instance, both master's mysql resource (jdbc/masterdb) and slave's mysql resource (jdbc/moviedb) is defined in its context.xml. All reads go to its own mysql but all writes go to master's mysql since slave cannot write to its own. So we use @Resource(name = "jdbc/moviedb") for reads and @Resource(name = "jdbc/masterdb") for writes.

### JMeter TS/TJ Time Logs

To use log_processing.py, you need to have python installed on your machine. Once you do, you have to put the log_processing.py into the same folder as the logs you want it to run on. You can run it by either running it through the IDLE or you can do py log_processing.py. Once it is running, you will be prompted to input the log's name and then it will calculate the average servlet time and average JDBC time if it exists and prints it.

### JMeter TS/TJ Time Measurement Report

| Single-instance Version Test                   | Graph Results              | Average Query Time(ms) | Average Search Servlet Time(ms) | Average JDBC Time(ms) | Analysis                                                                                                                                                                                                                                                            |
|------------------------------------------------|----------------------------|------------------------|---------------------------------|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                          | [Single1](img/Single1.png) | 178                    | 177.71543111279334              | 172.4959672975019     | Has faster query time due to less load on the backend database.                                                                                                                                                                                                     |
| Case 2: HTTP/10 threads                        | [Single2](img/Single2.png) | 335                    | 329.8843979560939               | 319.5441554125662     | Even though the average query time is longer than case 1, multiple queries are waiting to be processed so there is little to no down time between the database processing queries unlike case 1.                                                                    |
| Case 3: HTTPS/10 threads                       | [Single3](img/Single3.png) | 324                    | 318.8061278198334               | 310.6010211582135     | This case has the same benefits as case 2 as well as not having to redirect from http to https which is why the average query time is lower than case 2.                                                                                                            |
| Case 4: HTTP/10 threads/ No connection pooling | [Single4](img/Single4.png) | 379                    | 365.2252658591976               | 315.378165329296      | Case 4 has the downside of case 2 of redirecting to https as well as lack of connection pooling. Extra time is sure to be expected when the threads create a new connection every time they query instead of reusing existing connections from the connection pool. |






| Scaled Version Test                            | Graph Results              | Average Query Time(ms) | Average Search Servlet Time(ms) | Average JDBC Time(ms) | Analysis                                                                                                                                                                                                                                                                                     |
|------------------------------------------------|----------------------------|------------------------|---------------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Case 1: HTTP/1 thread                          | [Scaled1](img/Scaled1.png) | 286                    | 219.86930978993186              | 213.46370853368657    | Has faster query time due to less load on the backend database, but due to the performance of the aws instance, the scaled version performs slower than the single version.                                                                                                                  |
| Case 2: HTTP/10 threads                        | [Scaled2](img/Scaled2.png) | 1310                   | 1244.0784277445116              | 1211.8695259242998    | Even though the average query time is longer than case 1, multiple queries are waiting to be processed so there is little to no down time between the database processing queries unlike case 1.                                                                                             |
| Case 3: HTTP/10 threads/ No connection pooling | [Scaled3](img/Scaled3.png) | 1179                   | 1113.3373816324754              | 1078.870950998486     | Even though the average query time is lower than case 2, on paper case 3 should be higher than case 2. This unexpected outcome could be created because of the performance of the aws instance and 10 threads might not be enough to produce stress on the database to produce a difference. |


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

P4
-----
Autocomplete, Full-text Search

P5
-----
Worked together through all tasks

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

P4
-----
Android Implementation

P5
-----
Worked together through all tasks
```
