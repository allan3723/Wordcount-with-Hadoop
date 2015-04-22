Instructions:
- Compiling everything (on Cloudera CDH4)

$ javac Preprocess.java
$ mkdir job_classes
$ javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/* -d job_classes Job.java
$ jar -cvf job.jar -C job_classes/ .
$ mkdir job2_classes
$ javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop/client-0.20/* -d job2_classes Job2.java
$ jar -cvf job2.jar -C job2_classes/ .


Running:
(After setting up the node clusters and the proxy and linking to the HDFS and 
assuming that input is the input path & output/output2 is the output path)
(Also, Email-Enron.txt is the file we got online).

$ java Preprocess Email-Enron.txt > pp.txt
-> make the input directory on the HDFS and put pp.txt in it
$ hadoop jar job.jar org.myorg.Job /input /output
$ hadoop jar job2.jar org.myorg.Job2 /output/part-* /output2
-> output2 will contain the file that has all the triangles.


Notes: 
- The three core parts of the triangle count program are: Preprocess.java/Preprocess.class, job.jar, job2.jar.
- Preprocess is step 1 (from the course webpage under "Details for counting Triangle"), job is step 2 and job2 is step 3. 
- Job has to be ran using the normalized outputs from Preprocess. The inputs of Job2 is the outputs of Job, so they have to be ran sequentially to get the proper output. 
- When we wanted to run the triangle counting program with more reducers, we had to include:
  job.setNumReduceTasks(n) where n is the number of reducers. This was added in the main functions of Job.java and Job2.java (we did it after conf.setJobName(...)). The current Job.java and Job2.java does not have it so it will run in default settings.
- For the top 10 nodes, we ran wordcount on it and then made a java program that chose the top 10 nodes with the most frequency.
