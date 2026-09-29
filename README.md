# sed-batch

This utility program use the <a href="https://github.com/tools4j/unix4j">unix4j</a> to replace the string in the given file(s).

https://github.com/tools4j/unix4j

<img width="1220" height="260" alt="Screenshot from 2026-09-28 22-12-00" src="https://github.com/user-attachments/assets/8fa9fe3c-653c-47f0-a576-009ea0b7fc81" />

## Command

### Single File

#### Dry-run (i.e. file is not changed, just display the difference)
<pre>java -jar sed-batch-0.0.1-SNAPSHOT.jar file=pom.xml regexp=0.0.1 replacement=0.0.2</pre>

<pre>[main] [INFO ] [SedBatch.info(SedBatch.java:136)]: /tmp/pom.xml
[main] [INFO ] [SedBatch.info(SedBatch.java:147)]: @@ -7,1 +7,1 @@
[main] [INFO ] [SedBatch.info(SedBatch.java:147)]: -	<version>0.0.1-SNAPSHOT</version>
[main] [INFO ] [SedBatch.info(SedBatch.java:147)]: +	<version>0.0.2-SNAPSHOT</version></pre>

#### Update the given file
<pre>java -jar sed-batch-0.0.1-SNAPSHOT.jar file=pom.xml regexp=0.0.1 replacement=0.0.2 execute=true</pre>

<pre>[main] [INFO ] [SedBatch.info(SedBatch.java:136)]: /tmp/pom.xml
[main] [INFO ] [SedBatch.info(SedBatch.java:147)]: @@ -7,1 +7,1 @@
[main] [INFO ] [SedBatch.info(SedBatch.java:147)]: -	<version>0.0.1-SNAPSHOT</version>
[main] [INFO ] [SedBatch.info(SedBatch.java:147)]: +	<version>0.0.2-SNAPSHOT</version>
[main] [INFO ] [SedBatch.info(SedBatch.java:136)]: Updated</pre>

### Multiple files

#### Dry-run (i.e. file is not changed, just display the difference)
<pre>java -jar sed-batch-0.0.1-SNAPSHOT.jar fileNameList=filesNames.txt regexp=0.0.1 replacement=0.0.2</pre>

#### Update the given files
<pre>java -jar sed-batch-0.0.1-SNAPSHOT.jar fileNameList=filesNames.txt regexp=0.0.1 replacement=0.0.2 execute=true</pre>
