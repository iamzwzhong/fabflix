# CS122BParser
Separate part of Fabflix Project where we use a Parser
to parse XML files and add them to our Fabflix database.

#Performance Tuning Report
When using the naive implementation which included multiple mySQL requests
and inserts per compiled object in the XML files, the whole process was painfully
slow. Running the whole parser took nearly 2 hours with the casts.xml taking up 95%
of the time. 

Optimizations:
HashMap in Memory: allows us to quickly check whether or not files are duplicate by compiling all the information into a HashMap before we parse the document
Batch Inserts: allows us to compile thousands of inserts into one. It takes much longer to insert thousands of single inserts compared to one very large insert.

Time Reductions:
mains243.xml |  2m -> 2s
actors63.xml |	1m30s -> 2s
casts124.xml |	1hr50m -> 4s

Total Time: 1h53m30s -> 8s
